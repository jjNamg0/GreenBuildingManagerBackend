package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando falta elegir una opción obligatoria o la opción no es una de las
 * permitidas.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class OptionNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = -6118340957260433812L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public OptionNotValidException() {
		super("Elige una opción válida.");
	}

	/**
	 * Constructor con un mensaje personalizado.
	 *
	 * @param mensaje Descripción del error
	 */
	public OptionNotValidException(String mensaje) {
		super(mensaje);
	}
}
