package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando una fecha no tiene el formato AAAA-MM-DD.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class DateNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = -8079946617342005218L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public DateNotValidException() {
		super("La fecha debe tener el formato AAAA-MM-DD.");
	}
}
