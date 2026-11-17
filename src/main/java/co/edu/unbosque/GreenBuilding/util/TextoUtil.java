package co.edu.unbosque.GreenBuilding.util;

import java.text.Normalizer;

/**
 * Clase de utilidad para limpiar, comparar y escapar textos.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class TextoUtil {

	/** Constructor privado: esta clase solo tiene métodos estáticos. */
	private TextoUtil() {}

	/**
	 * Indica si un texto es nulo o está vacío.
	 *
	 * @param texto Texto a revisar
	 * @return true si está vacío
	 */
	public static boolean vacio(String texto) {
		return texto == null || texto.isBlank();
	}

	/**
	 * Quita los espacios del inicio y del final. Un texto nulo se vuelve vacío.
	 *
	 * @param texto Texto a limpiar
	 * @return Texto limpio
	 */
	public static String limpiar(String texto) {
		return texto == null ? "" : texto.trim();
	}

	/**
	 * Limpia un texto opcional. Un texto vacío se vuelve null.
	 *
	 * @param texto Texto a limpiar
	 * @return Texto limpio, o null si está vacío
	 */
	public static String opcional(String texto) {
		return vacio(texto) ? null : texto.trim();
	}

	/**
	 * Convierte un correo a minúsculas y sin espacios.
	 *
	 * @param correo Correo a normalizar
	 * @return Correo normalizado
	 */
	public static String correo(String correo) {
		return limpiar(correo).toLowerCase();
	}

	/**
	 * Quita las tildes y pasa a minúsculas, para comparar textos.
	 *
	 * @param texto Texto a normalizar
	 * @return Texto normalizado
	 */
	public static String normalizar(String texto) {
		String sinTildes =
				Normalizer.normalize(limpiar(texto), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return sinTildes.toLowerCase();
	}

	/**
	 * Indica si un texto contiene otro, sin importar tildes ni mayúsculas.
	 *
	 * @param texto Texto donde se busca
	 * @param busqueda Texto a buscar
	 * @return true si lo contiene
	 */
	public static boolean contiene(String texto, String busqueda) {
		return normalizar(texto).contains(normalizar(busqueda));
	}

	/**
	 * Escapa los caracteres especiales de HTML para mostrarlos de forma segura en un correo.
	 *
	 * @param texto Texto a escapar
	 * @return Texto escapado
	 */
	public static String escaparHtml(String texto) {
		return limpiar(texto)
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
