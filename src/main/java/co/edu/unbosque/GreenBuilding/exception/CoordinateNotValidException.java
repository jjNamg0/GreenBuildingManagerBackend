package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando falta una ubicación o su latitud o longitud están fuera de rango.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class CoordinateNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 3907415522680147765L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public CoordinateNotValidException() {
		super("Las coordenadas no son válidas.");
	}

	/**
	 * Constructor con un mensaje personalizado.
	 *
	 * @param mensaje Descripción del error
	 */
	public CoordinateNotValidException(String mensaje) {
		super(mensaje);
	}
}
