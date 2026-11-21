package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando un texto supera el número máximo de caracteres
 * permitido.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class LengthNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 7359426801134567893L;

	/**
	 * Constructor por defecto que inicializa la excepción con un mensaje de error
	 * estándar.
	 */
	public LengthNotValidException() {
		super("Uno de los textos es demasiado largo.");
	}

	/**
	 * Constructor que indica el campo y el máximo de caracteres permitido.
	 *
	 * @param campo  Nombre del campo
	 * @param maximo Número máximo de caracteres
	 */
	public LengthNotValidException(String campo, int maximo) {
		super("El campo " + campo + " no puede tener más de " + maximo + " caracteres.");
	}
}
