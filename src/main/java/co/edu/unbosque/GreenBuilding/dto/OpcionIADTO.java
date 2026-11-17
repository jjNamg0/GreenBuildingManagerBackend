package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con una de las respuestas que devuelve el modelo de IA.
 */
public class OpcionIADTO {

	/** Mensaje escrito por el asistente. */
	private MensajeIADTO message;

	/** Constructor por defecto. Crea una instancia vacía de OpcionIADTO. */
	public OpcionIADTO() {}

	/**
	 * Constructor con message.
	 *
	 * @param message Mensaje escrito por el asistente.
	 */
	public OpcionIADTO(MensajeIADTO message) {
		this.message = message;
	}

	/**
	 * Obtiene: mensaje escrito por el asistente.
	 *
	 * @return Mensaje escrito por el asistente
	 */
	public MensajeIADTO getMessage() {
		return message;
	}

	/**
	 * Establece: mensaje escrito por el asistente.
	 *
	 * @param message Mensaje escrito por el asistente
	 */
	public void setMessage(MensajeIADTO message) {
		this.message = message;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(message);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos OpcionIADTO son iguales si
	 * todos sus atributos son iguales.
	 *
	 * @param obj Objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		OpcionIADTO other = (OpcionIADTO) obj;
		return Objects.equals(message, other.message);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "OpcionIA ["
				+ "message="
				+ message
				+ "]";
	}
}
