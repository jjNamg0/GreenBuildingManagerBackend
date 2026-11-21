package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando un teléfono no tiene 10 dígitos.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class PhoneNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 5137264098812345671L;

	/**
	 * Constructor por defecto que inicializa la excepción con un mensaje de error
	 * estándar.
	 */
	public PhoneNotValidException() {
		super("Escribe un teléfono válido de 10 dígitos, sin espacios.");
	}

	/**
	 * Constructor con un mensaje personalizado.
	 *
	 * @param mensaje Descripción del error
	 */
	public PhoneNotValidException(String mensaje) {
		super(mensaje);
	}
}
