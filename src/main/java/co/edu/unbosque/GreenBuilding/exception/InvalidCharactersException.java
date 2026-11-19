package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando un texto contiene caracteres no permitidos, como los símbolos de
 * etiquetas HTML.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class InvalidCharactersException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = -5524110987321640117L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public InvalidCharactersException() {
		super("Solicitud con caracteres inválidos.");
	}
}
