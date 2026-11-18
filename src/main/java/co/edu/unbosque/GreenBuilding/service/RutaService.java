package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.ManiobraOsrmDTO;
import co.edu.unbosque.GreenBuilding.dto.PasoOsrmDTO;
import co.edu.unbosque.GreenBuilding.dto.PasoRutaDTO;
import co.edu.unbosque.GreenBuilding.dto.RespuestaOsrmDTO;
import co.edu.unbosque.GreenBuilding.dto.RutaDTO;
import co.edu.unbosque.GreenBuilding.dto.RutaOsrmDTO;
import co.edu.unbosque.GreenBuilding.dto.TramoOsrmDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio que calcula rutas con el servidor público de OSRM. Invierte los
 * puntos de [lng, lat] a [lat, lng] y escribe las instrucciones en español.
 */
@Service
public class RutaService {

	/** Modos de transporte que acepta el servicio. */
	private static final String[] MODOS = { "CARRO", "CAMINANDO", "BICICLETA" };

	/** Perfil de OSRM de cada modo de transporte, en el mismo orden que MODOS. */
	private static final String[] PERFILES = { "car", "foot", "bike" };

	/**
	 * Modificadores de giro de OSRM y su texto en español: cada fila es
	 * {modificador, texto}.
	 */
	private static final String[][] DIRECCIONES = { { "left", "a la izquierda" }, { "right", "a la derecha" },
			{ "slight left", "levemente a la izquierda" }, { "slight right", "levemente a la derecha" },
			{ "sharp left", "fuertemente a la izquierda" }, { "sharp right", "fuertemente a la derecha" },
			{ "straight", "derecho" }, { "uturn", "en U" } };

	/** URL base del servidor OSRM. */
	@Value("${app.ruta.url:https://routing.openstreetmap.de}")
	private String urlBase;

	/**
	 * API key del servidor de rutas. Si está vacía, la consulta se hace sin API
	 * key.
	 */
	@Value("${app.ruta.apikey:}")
	private String apiKey;

	/** Constructor por defecto. */
	public RutaService() {
	}

	/**
	 * Indica si un modo de transporte es uno de los que acepta el servicio.
	 *
	 * @param modo Modo de transporte en mayúsculas
	 * @return true si es CARRO, CAMINANDO o BICICLETA
	 */
	public static boolean esModoValido(String modo) {
		for (String valido : MODOS) {
			if (valido.equals(modo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Busca el perfil de OSRM de un modo de transporte.
	 *
	 * @param modo Modo de transporte en mayúsculas
	 * @return Perfil de OSRM; si el modo no existe, el de carro
	 */
	private String perfil(String modo) {
		for (int i = 0; i < MODOS.length; i++) {
			if (MODOS[i].equals(modo)) {
				return PERFILES[i];
			}
		}
		return PERFILES[0];
	}

	/**
	 * Busca el texto en español de un modificador de giro de OSRM.
	 *
	 * @param modificador Modificador de OSRM, por ejemplo left
	 * @return Texto en español, o vacío si el modificador no se conoce
	 */
	private String direccion(String modificador) {
		for (String[] fila : DIRECCIONES) {
			if (fila[0].equals(modificador)) {
				return fila[1];
			}
		}
		return "";
	}

	/**
	 * Consulta la URL enviando la API key en la cabecera cuando hay una
	 * configurada, y convierte la respuesta en el DTO de OSRM.
	 *
	 * @param url URL a consultar
	 * @return Respuesta de OSRM, o null si la petición falló
	 */
	private RespuestaOsrmDTO consultar(String url) {
		if (apiKey == null || apiKey.isBlank()) {
			return ExternalHTTPRequestHandler.doGetAndConvertToDto(url, RespuestaOsrmDTO.class);
		}
		return ExternalHTTPRequestHandler.doGetWithHeadersAndConvertToDto(url, apiKey.trim(), RespuestaOsrmDTO.class);
	}

	/**
	 * Calcula la ruta entre dos puntos.
	 *
	 * @param origenLatitud   Latitud de origen
	 * @param origenLongitud  Longitud de origen
	 * @param destinoLatitud  Latitud de destino
	 * @param destinoLongitud Longitud de destino
	 * @param modo            CARRO, CAMINANDO o BICICLETA
	 * @return La ruta, o null si no se encontró o el servicio falló
	 */
	public RutaDTO calcular(double origenLatitud, double origenLongitud, double destinoLatitud, double destinoLongitud,
			String modo) {
		String url = String.format(Locale.ROOT,
				"%s/routed-%s/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson&steps=true",
				urlBase, perfil(modo), origenLongitud, origenLatitud, destinoLongitud, destinoLatitud);
		try {
			RespuestaOsrmDTO respuesta = consultar(url);
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
	 * Convierte la respuesta de OSRM en una ruta, con los puntos como [latitud,
	 * longitud].
	 *
	 * @param respuesta Respuesta de OSRM
	 * @return La ruta, o null si OSRM no encontró ruta
	 */
	private RutaDTO leerRespuesta(RespuestaOsrmDTO respuesta) {
		List<RutaOsrmDTO> rutas = respuesta.getRoutes();
		if (!"Ok".equals(respuesta.getCode()) || rutas == null || rutas.isEmpty()) {
			return null;
		}
		RutaOsrmDTO ruta = rutas.get(0);
		List<double[]> puntos = new ArrayList<>();
		for (double[] punto : ruta.getGeometry().getCoordinates()) {
			puntos.add(new double[] { punto[1], punto[0] });
		}
		List<PasoRutaDTO> pasos = new ArrayList<>();
		for (TramoOsrmDTO tramo : ruta.getLegs()) {
			for (PasoOsrmDTO paso : tramo.getSteps()) {
				pasos.add(leerPaso(paso));
			}
		}
		return new RutaDTO(ruta.getDistance(), ruta.getDuration(), puntos, pasos);
	}

	/**
	 * Convierte un paso de OSRM en una instrucción en español.
	 *
	 * @param paso Paso de OSRM
	 * @return Paso de la ruta
	 */
	private PasoRutaDTO leerPaso(PasoOsrmDTO paso) {
		ManiobraOsrmDTO maniobra = paso.getManeuver();
		String tipo = texto(maniobra.getType());
		String direccion = direccion(texto(maniobra.getModifier()));
		String via = texto(paso.getName());
		String porVia = via.isEmpty() ? "" : " por " + via;
		String instruccion;
		if (tipo.equals("depart")) {
			instruccion = "Sal" + porVia;
		} else if (tipo.equals("arrive")) {
			instruccion = "Llegaste a tu destino";
		} else if (tipo.equals("roundabout") || tipo.equals("rotary")) {
			int salida = maniobra.getExit() == null ? 1 : maniobra.getExit();
			instruccion = "En la glorieta toma la salida " + salida + porVia;
		} else if (tipo.equals("turn") || tipo.equals("end of road")) {
			instruccion = "Gira " + direccion + (via.isEmpty() ? "" : " en " + via);
		} else if (tipo.equals("fork")) {
			instruccion = "En la bifurcación mantente " + direccion + porVia;
		} else if (tipo.equals("merge")) {
			instruccion = "Incorpórate " + direccion + porVia;
		} else if (tipo.equals("on ramp") || tipo.equals("off ramp")) {
			instruccion = "Toma la salida " + direccion + porVia;
		} else {
			instruccion = ("Continúa " + direccion).trim() + porVia;
		}
		return new PasoRutaDTO(instruccion, paso.getDistance(), paso.getDuration());
	}

	/**
	 * Devuelve un texto o vacío si es null.
	 *
	 * @param valor Texto leído de la respuesta
	 * @return El texto, o vacío si no venía
	 */
	private String texto(String valor) {
		return valor == null ? "" : valor;
	}
}
