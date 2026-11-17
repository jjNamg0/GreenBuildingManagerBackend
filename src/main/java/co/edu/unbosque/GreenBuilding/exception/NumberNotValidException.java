package co.edu.unbosque.GreenBuilding.exception;

/**
 * Excepción que se lanza cuando un número obligatorio llega vacío o no es mayor a cero.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class NumberNotValidException extends Exception {

	/** Número de serie para la serialización. */
	private static final long serialVersionUID = 6640192385517730086L;

	/** Constructor por defecto que inicializa la excepción con un mensaje de error estándar. */
	public NumberNotValidException() {
		super("El valor debe ser un número mayor a 0.");
	}

	/**
	 * Constructor que indica el nombre del campo numérico.
	 *
	 * @param campo Nombre del campo
	 */
	public NumberNotValidException(String campo) {
		super("El campo " + campo + " debe ser un número mayor a 0.");
	}
}
