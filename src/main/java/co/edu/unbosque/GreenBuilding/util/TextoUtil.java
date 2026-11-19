package co.edu.unbosque.GreenBuilding.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de utilidad para limpiar, comparar y escapar textos.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class TextoUtil {

	/** Letras con tilde que se cambian al normalizar. */
	private static final String CON_TILDE = "áàäâãéèëêíìïîóòöôõúùüûñç";

	/** Letra sin tilde que corresponde a cada letra de CON_TILDE. */
	private static final String SIN_TILDE = "aaaaaeeeeiiiiooooouuuunc";

	/** Caracteres que tienen un significado especial en HTML. */
	private static final String ESPECIALES_HTML = "&<>\"'";

	/** Forma segura de escribir en HTML cada carácter de ESPECIALES_HTML. */
	private static final String[] ESCAPES_HTML = {"&amp;", "&lt;", "&gt;", "&quot;", "&#39;"};

	/** Constructor privado: esta clase solo tiene métodos estáticos. */
	private TextoUtil() {}

	/**
	 * Indica si un texto es nulo o está vacío.
	 *
	 * @param texto Texto a revisar
	 * @return true si está vacío
	 */
	public static boolean vacio(String texto) {
		return texto == null || texto.trim().isEmpty();
	}

	/**
	 * Quita los espacios del inicio y del final. Un texto nulo se vuelve vacío.
	 *
	 * @param texto Texto a limpiar
	 * @return Texto limpio
	 */
	public static String limpiar(String texto) {
		if (texto == null) {
			return "";
		}
		return texto.trim();
	}

	/**
	 * Limpia un texto opcional. Un texto vacío se vuelve null.
	 *
	 * @param texto Texto a limpiar
	 * @return Texto limpio, o null si está vacío
	 */
	public static String opcional(String texto) {
		if (vacio(texto)) {
			return null;
		}
		return texto.trim();
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
		String minusculas = limpiar(texto).toLowerCase();
		String sinTildes = "";
		for (int i = 0; i < minusculas.length(); i++) {
			String letra = minusculas.substring(i, i + 1);
			int posicion = CON_TILDE.indexOf(letra);
			if (posicion >= 0) {
				letra = SIN_TILDE.substring(posicion, posicion + 1);
			}
			sinTildes = sinTildes + letra;
		}
		return sinTildes;
	}

	/**
	 * Separa un texto por comas.
	 *
	 * @param texto Texto con valores separados por comas
	 * @return Lista con cada valor, sin los vacíos del final
	 */
	public static List<String> separarPorComas(String texto) {
		List<String> partes = new ArrayList<>();
		String actual = "";
		for (int i = 0; i < texto.length(); i++) {
			String letra = texto.substring(i, i + 1);
			if (letra.equals(",")) {
				partes.add(actual);
				actual = "";
			} else {
				actual = actual + letra;
			}
		}
		partes.add(actual);
		for (int i = partes.size() - 1; i > 0 && partes.get(i).isEmpty(); i--) {
			partes.remove(i);
		}
		return partes;
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
		String limpio = limpiar(texto);
		String escapado = "";
		for (int i = 0; i < limpio.length(); i++) {
			String letra = limpio.substring(i, i + 1);
			int posicion = ESPECIALES_HTML.indexOf(letra);
			if (posicion >= 0) {
				letra = ESCAPES_HTML[posicion];
			}
			escapado = escapado + letra;
		}
		return escapado;
	}
}
