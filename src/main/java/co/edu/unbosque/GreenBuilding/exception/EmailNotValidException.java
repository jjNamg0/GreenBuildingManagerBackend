package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando un correo electrónico no tiene un formato válido.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class EmailNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 4208831502193465201L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public EmailNotValidException() {
		super("Escribe un correo electrónico válido.");
	}

	/**
	 * Constructor con un mensaje personalizado.
	 *
	 * @param mensaje Descripción del error
	 */
	public EmailNotValidException(String mensaje) {
		super(mensaje);
	}
}
