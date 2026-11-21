package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando la dirección de una imagen no es una URL válida.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class UrlNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 6248315790023456782L;

	/**
	 * Constructor por defecto que inicializa la excepción con un mensaje de error
	 * estándar.
	 */
	public UrlNotValidException() {
		super("La imagen debe ser una URL que empiece por http:// o https://.");
	}

	/**
	 * Constructor con un mensaje personalizado.
	 *
	 * @param mensaje Descripción del error
	 */
	public UrlNotValidException(String mensaje) {
		super(mensaje);
	}
}
