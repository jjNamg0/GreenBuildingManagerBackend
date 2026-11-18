package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.ChatDTO;
import co.edu.unbosque.GreenBuilding.dto.MensajeChatDTO;
import co.edu.unbosque.GreenBuilding.dto.MensajeIADTO;
import co.edu.unbosque.GreenBuilding.dto.OpcionIADTO;
import co.edu.unbosque.GreenBuilding.dto.PeticionIADTO;
import co.edu.unbosque.GreenBuilding.dto.RespuestaIADTO;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio del chat de ayuda. Arma la conversación con una instrucción fija
 * sobre Green Building y se la envía al modelo de NVIDIA (API compatible con
 * OpenAI). La API key vive en el application.properties, que no se sube a git,
 * y se envía como Bearer. No guarda nada en la base de datos: el historial lo
 * manda el front en cada pregunta.
 *
 * <p>
 * Códigos de validar: 0 = correcto, 1 = mensaje vacío, 2 = mensaje demasiado
 * largo, 3 = el usuario escribió muy rápido.
 */
@Service
public class IAService {

	/** Rol de los mensajes escritos por el usuario. */
	private static final String ROL_USUARIO = "user";

	/** Rol de los mensajes escritos por el asistente. */
	private static final String ROL_ASISTENTE = "assistant";

	/**
	 * Instrucción fija que define qué puede responder el asistente y cómo funciona
	 * la aplicación.
	 */
	private static final String INSTRUCCION = """
			Eres el asistente de ayuda de Green Building Manager, una aplicación web para conjuntos \
			residenciales de Cundinamarca. Responde siempre en español, con frases cortas (máximo 5) y \
			un tono amable. Solo ayudas a usar la aplicación: si te preguntan otra cosa, di que solo \
			puedes ayudar con Green Building. No inventes datos, horarios, precios ni nombres: si no \
			sabes algo, sugiere consultarlo con la administración del conjunto. Nunca pidas \
			contraseñas ni códigos de verificación.

			Cómo funciona la aplicación:
			- Residentes: se registran y verifican la cuenta con un código que llega al correo. Después \
			el administrador les asigna una unidad. En "Mi conjunto" ven su conjunto y su unidad. En \
			"Reservar" eligen un espacio común y una franja horaria, y el administrador aprueba o \
			rechaza la reserva; también pueden cancelarla. En "Eventos" ven los eventos y confirman su \
			asistencia. En "Para ti" ven recomendaciones según los intereses de su perfil. Hay un mapa \
			con los conjuntos cercanos y rutas en carro, caminando o en bicicleta. Si olvidan la \
			contraseña, piden un código al correo para restablecerla. Pueden cambiar el tema claro u \
			oscuro.
			- Administradores: ven el Panel con los indicadores y gestionan conjuntos, unidades, \
			espacios comunes, eventos (crear, cancelar y enviar invitaciones), usuarios (asignar unidad \
			y activar o desactivar cuentas) y reservas pendientes. También consultan la auditoría.
			""";

	/** Dirección del servicio de chat de NVIDIA. */
	@Value("${app.ia.url:https://integrate.api.nvidia.com/v1/chat/completions}")
	private String url;

	/** Modelo que responde. */
	@Value("${app.ia.modelo:nvidia/nemotron-3.5-bolt-e4b}")
	private String modelo;

	/** API key de NVIDIA. Si está vacía, el asistente no responde. */
	@Value("${app.ia.apikey:}")
	private String apiKey;

	/** Máximo de caracteres de una pregunta. */
	@Value("${app.ia.max-caracteres:500}")
	private int maxCaracteres;

	/** Máximo de mensajes anteriores que se envían al modelo. */
	@Value("${app.ia.max-historial:6}")
	private int maxHistorial;

	/** Milisegundos mínimos entre dos preguntas del mismo usuario. */
	@Value("${app.ia.pausa-ms:1500}")
	private long pausaMs;

	/** Usuarios que ya hicieron alguna pregunta. */
	private final List<String> usuariosRecientes = new ArrayList<>();

	/**
	 * Momento de la última pregunta aceptada de cada usuario, en el mismo orden de
	 * la lista anterior.
	 */
	private final List<Long> momentosRecientes = new ArrayList<>();

	/** Constructor por defecto. */
	public IAService() {
	}

	/**
	 * Valida la pregunta y registra el momento en que la hizo el usuario.
	 *
	 * @param usuario Correo del usuario que pregunta
	 * @param chat    Pregunta y conversación anterior
	 * @return 0 si es correcta, 1 si está vacía, 2 si es demasiado larga, 3 si
	 *         escribió muy rápido
	 */
	public synchronized int validar(String usuario, ChatDTO chat) {
		String mensaje = chat.getMensaje();
		if (mensaje == null || mensaje.isBlank()) {
			return 1;
		}
		if (mensaje.length() > maxCaracteres) {
			return 2;
		}
		long ahora = System.currentTimeMillis();
		int posicion = usuariosRecientes.indexOf(usuario);
		if (posicion >= 0 && ahora - momentosRecientes.get(posicion) < pausaMs) {
			return 3;
		}
		if (posicion >= 0) {
			momentosRecientes.set(posicion, ahora);
		} else {
			usuariosRecientes.add(usuario);
			momentosRecientes.add(ahora);
		}
		return 0;
	}

	/**
	 * Envía la conversación al modelo y devuelve su respuesta.
	 *
	 * @param chat Pregunta y conversación anterior
	 * @return Respuesta del asistente, o null si no hay API key o el servicio falló
	 */
	public String responder(ChatDTO chat) {
		if (apiKey == null || apiKey.isBlank()) {
			return null;
		}
		try {
			RespuestaIADTO respuesta = ExternalHTTPRequestHandler.doPostWithHeadersAndConvertToDto(url,
					new Gson().toJson(armarPeticion(chat)), "Authorization", "Bearer " + apiKey.trim(),
					RespuestaIADTO.class);
			if (respuesta == null) {
				return null;
			}
			return leerRespuesta(respuesta);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Arma la petición que se envía al modelo: la instrucción, los últimos mensajes
	 * y la pregunta.
	 *
	 * @param chat Pregunta y conversación anterior
	 * @return Petición para el modelo
	 */
	private PeticionIADTO armarPeticion(ChatDTO chat) {
		List<MensajeIADTO> mensajes = new ArrayList<>();
		mensajes.add(new MensajeIADTO("system", INSTRUCCION));
		List<MensajeChatDTO> historial = chat.getHistorial();
		if (historial != null) {
			int desde = Math.max(0, historial.size() - maxHistorial);
			for (MensajeChatDTO anterior : historial.subList(desde, historial.size())) {
				if (esMensajeValido(anterior)) {
					mensajes.add(new MensajeIADTO(anterior.getRol(), recortar(anterior.getContenido())));
				}
			}
		}
		mensajes.add(new MensajeIADTO(ROL_USUARIO, chat.getMensaje()));
		return new PeticionIADTO(modelo, mensajes, 0.4, 1024, false);
	}

	/**
	 * Indica si un mensaje del historial se puede enviar al modelo. Solo se aceptan
	 * los roles user y assistant, así el front no puede colar instrucciones de
	 * sistema.
	 *
	 * @param anterior Mensaje del historial
	 * @return true si el rol es user o assistant y tiene texto
	 */
	private boolean esMensajeValido(MensajeChatDTO anterior) {
		boolean rolValido = ROL_USUARIO.equals(anterior.getRol()) || ROL_ASISTENTE.equals(anterior.getRol());
		return rolValido && anterior.getContenido() != null && !anterior.getContenido().isBlank();
	}

	/**
	 * Corta un texto al máximo de caracteres permitido.
	 *
	 * @param texto Texto a recortar
	 * @return El texto, con máximo maxCaracteres caracteres
	 */
	private String recortar(String texto) {
		return texto.length() > maxCaracteres ? texto.substring(0, maxCaracteres) : texto;
	}

	/**
	 * Extrae el texto de la respuesta del modelo y quita el bloque de razonamiento
	 * si lo trae.
	 *
	 * @param respuesta Respuesta del modelo
	 * @return Texto de la respuesta, o null si venía vacío
	 */
	private String leerRespuesta(RespuestaIADTO respuesta) {
		List<OpcionIADTO> opciones = respuesta.getChoices();
		if (opciones == null || opciones.isEmpty() || opciones.get(0).getMessage() == null) {
			return null;
		}
		String contenido = opciones.get(0).getMessage().getContent();
		if (contenido == null) {
			return null;
		}
		String limpio = contenido.replaceAll("(?s)<think>.*?</think>", "").trim();
		return limpio.isEmpty() ? null : limpio;
	}
}
