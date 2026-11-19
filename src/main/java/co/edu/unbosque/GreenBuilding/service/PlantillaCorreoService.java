package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.util.FechaUtil;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que arma el HTML de los correos: códigos de verificación,
 * invitaciones a eventos con plantillas, cancelaciones y respuestas a reservas.
 * Todo texto escrito por usuarios se escapa.
 */
@Service
public class PlantillaCorreoService {

	/**
	 * Colores y título de cada plantilla, en ese orden. Las filas van en el mismo
	 * orden del enum Plantilla: CLASICO, ELEGANTE, FIESTA, INFANTIL, DEPORTIVO y
	 * ASAMBLEA.
	 */
	private static final String[][] ESTILOS = { { "#f4f7f2", "#2e7d32", "#1f2a1f", "Estás invitado" },
			{ "#1c1c24", "#c9a54c", "#f4efe2", "Invitación especial" },
			{ "#fff3e6", "#e65100", "#3a2410", "¡Vamos de fiesta!" },
			{ "#eef7ff", "#1e88e5", "#13304d", "¡Plan para los niños!" },
			{ "#eefaf1", "#00897b", "#10332d", "¡A movernos!" }, { "#f3f4f7", "#37474f", "#1d262b", "Convocatoria" } };

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired
	private EspacioRepository espacioRepo;

	/** Constructor por defecto. */
	public PlantillaCorreoService() {
	}

	/**
	 * Arma el correo con el código de verificación o de restablecimiento.
	 *
	 * @param nombre    Nombre del usuario
	 * @param codigo    Código de 6 dígitos
	 * @param proposito Para qué sirve el código
	 * @return HTML del correo
	 */
	public String codigo(String nombre, String codigo, Proposito proposito) {
		String intro = "Recibimos una solicitud para restablecer tu contraseña. Usa este código:";
		if (proposito == Proposito.VERIFICACION) {
			intro = "Gracias por registrarte en Green Building. Usa este código para verificar tu cuenta:";
		}
		String cuerpo = "<p>Hola " + TextoUtil.escaparHtml(nombre) + ",</p><p>" + intro + "</p>"
				+ "<p style=\"font-size:32px;font-weight:700;letter-spacing:8px;color:#2e7d32;text-align:center;\">"
				+ TextoUtil.escaparHtml(codigo) + "</p>"
				+ "<p style=\"color:#666;font-size:13px;\">El código vence en 15 minutos. Si no fuiste tú, ignora este correo.</p>";
		return marco("#f4f7f2", "#2e7d32", "#1f2a1f", cuerpo);
	}

	/**
	 * Arma la vista previa de la invitación a un evento, con el botón del enlace
	 * sin destino.
	 *
	 * @param evento Evento (puede no estar guardado)
	 * @return HTML de la invitación
	 */
	public String invitacion(Evento evento) {
		return invitacion(evento, "#");
	}

	/**
	 * Arma la invitación a un evento con la plantilla elegida y el botón hacia el
	 * enlace público.
	 *
	 * @param evento Evento
	 * @param enlace Enlace público de la invitación
	 * @return HTML de la invitación
	 */
	public String invitacion(Evento evento, String enlace) {
		String[] estilo = ESTILOS[0];
		if (evento.getPlantilla() != null) {
			estilo = ESTILOS[evento.getPlantilla().ordinal()];
		}
		String cuerpo = "";
		cuerpo = cuerpo + "<p style=\"text-transform:uppercase;letter-spacing:3px;text-align:center;color:" + estilo[1]
				+ ";margin:8px 0;\">" + estilo[3] + "</p>";
		cuerpo = cuerpo + "<h1 style=\"text-align:center;margin:0 0 16px;\">"
				+ TextoUtil.escaparHtml(evento.getTitulo()) + "</h1>";
		if (!TextoUtil.vacio(evento.getDescripcion())) {
			cuerpo = cuerpo + "<p style=\"text-align:center;\">" + TextoUtil.escaparHtml(evento.getDescripcion())
					+ "</p>";
		}
		cuerpo = cuerpo + "<table style=\"margin:16px auto;border-collapse:collapse;\">";
		String fecha = "";
		if (evento.getFecha() != null) {
			fecha = evento.getFecha().format(FechaUtil.FECHA_LARGA);
		}
		cuerpo = cuerpo + fila("Fecha", fecha);
		cuerpo = cuerpo
				+ fila("Hora", FechaUtil.hora(evento.getHoraInicio()) + " a " + FechaUtil.hora(evento.getHoraFin()));
		cuerpo = cuerpo + fila("Lugar", lugar(evento));
		if (evento.getCupoMaximo() != null) {
			cuerpo = cuerpo + fila("Cupo", evento.getCupoMaximo() + " personas");
		}
		if (evento.isParaNinos()) {
			cuerpo = cuerpo + fila("Público", "Ideal para niños");
		}
		cuerpo = cuerpo + "</table>";
		cuerpo = cuerpo + boton(enlace, estilo[1]);
		return marco(estilo[0], estilo[1], estilo[2], cuerpo);
	}

	/**
	 * Arma la invitación de un residente a su reserva, con el botón hacia el enlace
	 * público.
	 *
	 * @param reserva   Reserva aprobada
	 * @param anfitrion Nombre del residente que invita
	 * @param enlace    Enlace público de la invitación
	 * @return HTML de la invitación
	 */
	public String invitacionReserva(Reserva reserva, String anfitrion, String enlace) {
		String[] estilo = ESTILOS[0];
		String cuerpo = "";
		cuerpo = cuerpo + "<p style=\"text-transform:uppercase;letter-spacing:3px;text-align:center;color:" + estilo[1]
				+ ";margin:8px 0;\">" + TextoUtil.escaparHtml(anfitrion) + " te invita</p>";
		cuerpo = cuerpo + "<h1 style=\"text-align:center;margin:0 0 16px;\">"
				+ TextoUtil.escaparHtml(reserva.getMotivo()) + "</h1>";
		cuerpo = cuerpo + "<table style=\"margin:16px auto;border-collapse:collapse;\">";
		cuerpo = cuerpo + fila("Fecha", reserva.getFecha().format(FechaUtil.FECHA_LARGA));
		cuerpo = cuerpo
				+ fila("Hora", FechaUtil.hora(reserva.getHoraInicio()) + " a " + FechaUtil.hora(reserva.getHoraFin()));
		cuerpo = cuerpo + fila("Lugar", lugarReserva(reserva));
		cuerpo = cuerpo + "</table>";
		cuerpo = cuerpo + boton(enlace, estilo[1]);
		return marco(estilo[0], estilo[1], estilo[2], cuerpo);
	}

	/**
	 * Arma el aviso para los invitados de que una reserva se canceló.
	 *
	 * @param reserva   Reserva cancelada
	 * @param anfitrion Nombre del residente que había invitado
	 * @return HTML del aviso
	 */
	public String cancelacionReserva(Reserva reserva, String anfitrion) {
		String cuerpo = "<h2>Plan cancelado</h2><p>" + TextoUtil.escaparHtml(anfitrion) + " canceló el plan <strong>"
				+ TextoUtil.escaparHtml(reserva.getMotivo()) + "</strong> del "
				+ reserva.getFecha().format(FechaUtil.FECHA_LARGA) + ", de " + FechaUtil.hora(reserva.getHoraInicio())
				+ " a " + FechaUtil.hora(reserva.getHoraFin()) + ", en " + TextoUtil.escaparHtml(lugarReserva(reserva))
				+ ".</p>";
		return marco("#fff4f4", "#c62828", "#2b1a1a", cuerpo);
	}

	/**
	 * Arma el aviso de cancelación de un evento.
	 *
	 * @param evento Evento cancelado
	 * @return HTML del aviso
	 */
	public String cancelacionEvento(Evento evento) {
		String cuerpo = "<h2>Evento cancelado</h2><p>El evento <strong>" + TextoUtil.escaparHtml(evento.getTitulo())
				+ "</strong> del " + evento.getFecha().format(FechaUtil.FECHA_LARGA)
				+ " fue cancelado.</p><p><strong>Motivo:</strong> "
				+ TextoUtil.escaparHtml(evento.getMotivoCancelacion()) + "</p>";
		return marco("#fff4f4", "#c62828", "#2b1a1a", cuerpo);
	}

	/**
	 * Arma el aviso de aprobación o rechazo de una reserva.
	 *
	 * @param reserva Reserva revisada
	 * @return HTML del aviso
	 */
	public String estadoReserva(Reserva reserva) {
		boolean aprobada = reserva.getEstado() == Reserva.Estado.APROBADA;
		String estado = "rechazada";
		String color = "#c62828";
		if (aprobada) {
			estado = "aprobada";
			color = "#2e7d32";
		}
		String cuerpo = "<h2>Tu reserva fue " + estado + "</h2>";
		cuerpo = cuerpo + "<p><strong>" + TextoUtil.escaparHtml(nombreEspacio(reserva.getEspacioId())) + "</strong> · "
				+ reserva.getFecha().format(FechaUtil.FECHA_LARGA) + ", " + FechaUtil.hora(reserva.getHoraInicio())
				+ " a " + FechaUtil.hora(reserva.getHoraFin()) + "</p>";
		if (!aprobada && !TextoUtil.vacio(reserva.getObservacion())) {
			cuerpo = cuerpo + "<p><strong>Observación:</strong> " + TextoUtil.escaparHtml(reserva.getObservacion())
					+ "</p>";
		}
		return marco("#f6f8f6", color, "#1f2a1f", cuerpo);
	}

	/**
	 * Arma el texto del lugar del evento.
	 *
	 * @param evento Evento
	 * @return Espacio y conjunto escapados
	 */
	private String lugar(Evento evento) {
		String conjunto = nombreConjunto(evento.getConjuntoId());
		String espacio = nombreEspacio(evento.getEspacioId());
		if (espacio.isEmpty()) {
			return conjunto;
		}
		return espacio + " · " + conjunto;
	}

	/**
	 * Arma el texto del lugar de una reserva.
	 *
	 * @param reserva Reserva
	 * @return Espacio y conjunto, sin escapar
	 */
	private String lugarReserva(Reserva reserva) {
		Optional<Espacio> espacio = espacioRepo.findById(reserva.getEspacioId());
		if (!espacio.isPresent()) {
			return "";
		}
		return espacio.get().getNombre() + " · " + nombreConjunto(espacio.get().getConjuntoId());
	}

	/**
	 * Arma el botón que lleva a la página pública de la invitación, con el enlace
	 * escrito debajo por si el botón no funciona.
	 *
	 * @param enlace Enlace público de la invitación
	 * @param color  Color del botón
	 * @return HTML del botón
	 */
	private String boton(String enlace, String color) {
		String destino = TextoUtil.escaparHtml(enlace);
		return "<p style=\"text-align:center;margin:24px 0 8px;\"><a href=\"" + destino
				+ "\" style=\"display:inline-block;background:" + color
				+ ";color:#ffffff;text-decoration:none;font-weight:700;padding:12px 24px;border-radius:999px;\">"
				+ "Ver ubicación y cómo llegar</a></p>"
				+ "<p style=\"text-align:center;font-size:12px;color:#888;word-break:break-all;\">Si el botón no abre, copia este enlace: "
				+ destino + "</p>";
	}

	/**
	 * Busca el nombre de un conjunto por su identificador.
	 *
	 * @param id Identificador a buscar (puede ser null)
	 * @return El nombre, o vacío si no existe
	 */
	private String nombreConjunto(Long id) {
		if (id == null) {
			return "";
		}
		Optional<Conjunto> found = conjuntoRepo.findById(id);
		if (found.isPresent()) {
			return found.get().getNombre();
		}
		return "";
	}

	/**
	 * Busca el nombre de un espacio por su identificador.
	 *
	 * @param id Identificador a buscar (puede ser null)
	 * @return El nombre, o vacío si no existe
	 */
	private String nombreEspacio(Long id) {
		if (id == null) {
			return "";
		}
		Optional<Espacio> found = espacioRepo.findById(id);
		if (found.isPresent()) {
			return found.get().getNombre();
		}
		return "";
	}

	/**
	 * Arma una fila de la tabla de datos del evento.
	 *
	 * @param etiqueta Etiqueta de la fila
	 * @param valor    Valor de la fila (se escapa)
	 * @return HTML de la fila
	 */
	private String fila(String etiqueta, String valor) {
		return "<tr><td style=\"padding:6px 12px;font-weight:600;\">" + etiqueta
				+ "</td><td style=\"padding:6px 12px;\">" + TextoUtil.escaparHtml(valor) + "</td></tr>";
	}

	/**
	 * Envuelve el contenido en el marco común de los correos.
	 *
	 * @param fondo  Color de fondo
	 * @param acento Color de acento
	 * @param texto  Color del texto
	 * @param cuerpo Contenido HTML
	 * @return Documento HTML completo
	 */
	private String marco(String fondo, String acento, String texto, String cuerpo) {
		return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"></head>"
				+ "<body style=\"margin:0;padding:24px;background:" + fondo
				+ ";font-family:Inter,Arial,sans-serif;color:" + texto
				+ ";\"><div style=\"max-width:560px;margin:0 auto;border:2px solid " + acento
				+ ";border-radius:16px;padding:28px;\">" + "<p style=\"margin:0 0 16px;font-weight:700;color:" + acento
				+ ";\">Green Building</p>" + cuerpo
				+ "</div><p style=\"text-align:center;font-size:12px;color:#888;\">Este correo fue enviado por Green Building Manager.</p>"
				+ "</body></html>";
	}
}
