package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando una hora no tiene el formato HH:mm o un rango de horas es
 * incorrecto.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class TimeNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 2214857765030019473L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public TimeNotValidException() {
		super("La hora debe tener el formato HH:mm.");
	}

	/**
	 * Constructor con un mensaje personalizado.
	 *
	 * @param mensaje Descripción del error
	 */
	public TimeNotValidException(String mensaje) {
		super(mensaje);
	}
}
