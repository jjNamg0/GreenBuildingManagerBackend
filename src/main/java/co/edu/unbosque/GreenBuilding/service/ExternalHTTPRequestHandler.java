package co.edu.unbosque.GreenBuilding.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Clase para manejar peticiones HTTP a servicios externos. Proporciona funcionalidad para realizar
 * solicitudes GET, POST, PUT y DELETE a APIs externas, enviar una API key por cabecera, convertir
 * la respuesta JSON a un DTO o a una lista de DTOs y mostrarla con formato legible. Todos los
 * métodos devuelven null si la petición falla o el servidor responde con un código de error.
 */
public class ExternalHTTPRequestHandler {

	/** Nombre de la cabecera que lleva la API key. */
	private static final String CABECERA_API_KEY = "X-Apikey";

	/** Tipo de contenido que se envía y se espera en todas las peticiones. */
	private static final String TIPO_JSON = "application/json";

	/**
	 * Cliente HTTP compartido para realizar peticiones. Configurado con HTTP/2 y un tiempo de espera
	 * de conexión de 10 segundos.
	 */
	private static final HttpClient HTTP_CLIENT =
			HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(10)).build();

	/** Constructor privado: esta clase solo tiene métodos estáticos. */
	private ExternalHTTPRequestHandler() {}

	/**
	 * Demostración de uso, como la vista en clase: consulta un dato curioso en una API pública y lo
	 * muestra con formato legible.
	 *
	 * @param args Argumentos de línea de comandos (no se usan)
	 */
	public static void main(String[] args) {
		System.out.println("//// GET ////");
		String fact = "https://uselessfacts.jsph.pl/api/v2/facts/random";
		System.out.println(doGetAndParse(fact));
	}

	/**
	 * Crea el constructor de peticiones con la URL y las cabeceras comunes.
	 *
	 * @param url URL a consultar
	 * @return Constructor de la petición con la configuración común
	 */
	private static HttpRequest.Builder base(String url) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", TIPO_JSON)
				.header("Accept", TIPO_JSON)
				.header("User-Agent", "GreenBuildingManager/1.0");
	}

	/**
	 * Envía la petición, muestra en consola el código de estado y devuelve el cuerpo de la respuesta.
	 *
	 * @param request Petición ya construida
	 * @return Cuerpo de la respuesta, o null si la petición falló o el código fue 400 o superior
	 */
	private static String enviar(HttpRequest request) {
		try {
			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("status code -> " + response.statusCode());
			if (response.statusCode() >= 400) {
				return null;
			}
			return response.body();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Realiza una petición GET y devuelve la respuesta con formato legible.
	 *
	 * @param url URL a consultar
	 * @return JSON con sangría, o null si la petición falló
	 */
	public static String doGet(String url) {
		return prettyPrintUsingGson(enviar(base(url).GET().build()));
	}

	/**
	 * Realiza una petición GET enviando la API key en la cabecera X-Apikey.
	 *
	 * @param url URL a consultar
	 * @param apiKey API key en texto plano (nunca va escrita en el código)
	 * @return JSON con sangría, o null si la petición falló
	 */
	public static String doGetWithHeaders(String url, String apiKey) {
		return prettyPrintUsingGson(
				enviar(base(url).header(CABECERA_API_KEY, apiKey).GET().build()));
	}

	/**
	 * Realiza una petición GET y devuelve la respuesta con formato legible.
	 *
	 * @param url URL a consultar
	 * @return JSON con sangría, o null si la petición falló
	 */
	public static String doGetAndParse(String url) {
		return prettyPrintUsingGson(enviar(base(url).GET().build()));
	}

	/**
	 * Realiza una petición GET y convierte la respuesta JSON en un DTO.
	 *
	 * @param url URL a consultar
	 * @param tipo Clase del DTO al que se convierte la respuesta
	 * @param <T> Tipo del DTO
	 * @return El DTO con los datos de la respuesta, o null si la petición falló
	 */
	public static <T> T doGetAndConvertToDto(String url, Class<T> tipo) {
		String json = doGet(url);
		if (json == null) {
			return null;
		}
		return new GsonBuilder().create().fromJson(json, tipo);
	}

	/**
	 * Realiza una petición GET y devuelve la respuesta JSON convertida en una lista de DTOs. Igual
	 * que en clase, la lista trae el DTO de la respuesta.
	 *
	 * @param url URL a consultar
	 * @param tipo Clase del DTO al que se convierte la respuesta
	 * @param <T> Tipo del DTO
	 * @return Lista con el DTO de la respuesta, o null si la petición falló
	 */
	public static <T> List<T> doGetAndConvertToDtoList(String url, Class<T> tipo) {
		T dto = doGetAndConvertToDto(url, tipo);
		if (dto == null) {
			return null;
		}
		return Arrays.asList(dto);
	}

	/**
	 * Realiza una petición POST con un cuerpo JSON y una cabecera propia, por ejemplo
	 * Authorization con una API key. Devuelve el cuerpo tal como llegó, sin darle formato.
	 *
	 * @param url URL a consultar
	 * @param json Cuerpo de la petición en formato JSON
	 * @param cabecera Nombre de la cabecera
	 * @param valor Valor de la cabecera (la API key ya descifrada, nunca escrita en el código)
	 * @return Cuerpo de la respuesta, o null si la petición falló
	 */
	public static String doPostWithHeaders(String url, String json, String cabecera, String valor) {
		return enviar(
				base(url).header(cabecera, valor).POST(HttpRequest.BodyPublishers.ofString(json)).build());
	}

	/**
	 * Realiza una petición GET enviando la API key en la cabecera X-Apikey y convierte la respuesta
	 * JSON en un DTO.
	 *
	 * @param url URL a consultar
	 * @param apiKey API key en texto plano (nunca va escrita en el código)
	 * @param tipo Clase del DTO al que se convierte la respuesta
	 * @param <T> Tipo del DTO
	 * @return El DTO con los datos de la respuesta, o null si la petición falló
	 */
	public static <T> T doGetWithHeadersAndConvertToDto(String url, String apiKey, Class<T> tipo) {
		String json = doGetWithHeaders(url, apiKey);
		if (json == null) {
			return null;
		}
		return new GsonBuilder().create().fromJson(json, tipo);
	}

	/**
	 * Realiza una petición POST con un cuerpo JSON y una cabecera propia, y convierte la respuesta
	 * JSON en un DTO.
	 *
	 * @param url URL a consultar
	 * @param json Cuerpo de la petición en formato JSON
	 * @param cabecera Nombre de la cabecera
	 * @param valor Valor de la cabecera (la API key ya descifrada, nunca escrita en el código)
	 * @param tipo Clase del DTO al que se convierte la respuesta
	 * @param <T> Tipo del DTO
	 * @return El DTO con los datos de la respuesta, o null si la petición falló
	 */
	public static <T> T doPostWithHeadersAndConvertToDto(
			String url, String json, String cabecera, String valor, Class<T> tipo) {
		String respuesta = doPostWithHeaders(url, json, cabecera, valor);
		if (respuesta == null) {
			return null;
		}
		return new GsonBuilder().create().fromJson(respuesta, tipo);
	}

	/**
	 * Realiza una petición POST con un cuerpo JSON.
	 *
	 * @param url URL a consultar
	 * @param json Cuerpo de la petición en formato JSON
	 * @return Respuesta con formato legible, o null si la petición falló
	 */
	public static String doPost(String url, String json) {
		return prettyPrintUsingGson(enviar(base(url).POST(HttpRequest.BodyPublishers.ofString(json)).build()));
	}

	/**
	 * Realiza una petición PUT con un cuerpo JSON.
	 *
	 * @param url URL a consultar
	 * @param json Cuerpo de la petición en formato JSON
	 * @return Respuesta con formato legible, o null si la petición falló
	 */
	public static String doPut(String url, String json) {
		return prettyPrintUsingGson(enviar(base(url).PUT(HttpRequest.BodyPublishers.ofString(json)).build()));
	}

	/**
	 * Realiza una petición DELETE.
	 *
	 * @param url URL a consultar
	 * @return Respuesta con formato legible, o null si la petición falló
	 */
	public static String doDelete(String url) {
		return prettyPrintUsingGson(enviar(base(url).DELETE().build()));
	}

	/**
	 * Da formato legible (con sangría) a un texto JSON.
	 *
	 * @param uglyJson JSON en una sola línea
	 * @return JSON con sangría, o null si el texto recibido es null
	 */
	public static String prettyPrintUsingGson(String uglyJson) {
		if (uglyJson == null) {
			return null;
		}
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
		JsonElement jsonElement = JsonParser.parseString(uglyJson);
		return gson.toJson(jsonElement);
	}
}
