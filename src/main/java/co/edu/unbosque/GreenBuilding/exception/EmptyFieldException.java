package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando un campo obligatorio llega vacío.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class EmptyFieldException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 7356219004871193350L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public EmptyFieldException() {
		super("Completa todos los campos obligatorios.");
	}

	/**
	 * Constructor que indica el nombre del campo vacío.
	 *
	 * @param campo Nombre del campo obligatorio
	 */
	public EmptyFieldException(String campo) {
		super("El campo " + campo + " es obligatorio.");
	}
}
