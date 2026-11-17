package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con la respuesta del modelo de IA. Los nombres de los campos son los mismos del JSON de
 * la API para que Gson lo convierta directamente.
 */
public class RespuestaIADTO {

	/** Respuestas del modelo; se usa la primera. */
	private List<OpcionIADTO> choices;

	/** Constructor por defecto. Crea una instancia vacía de RespuestaIADTO. */
	public RespuestaIADTO() {}

	/**
	 * Constructor con choices.
	 *
	 * @param choices Respuestas del modelo; se usa la primera.
	 */
	public RespuestaIADTO(List<OpcionIADTO> choices) {
		this.choices = choices;
	}

	/**
	 * Obtiene: respuestas del modelo; se usa la primera.
	 *
	 * @return Respuestas del modelo; se usa la primera
	 */
	public List<OpcionIADTO> getChoices() {
		return choices;
	}

	/**
	 * Establece: respuestas del modelo; se usa la primera.
	 *
	 * @param choices Respuestas del modelo; se usa la primera
	 */
	public void setChoices(List<OpcionIADTO> choices) {
		this.choices = choices;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(choices);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RespuestaIADTO son iguales si
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
		RespuestaIADTO other = (RespuestaIADTO) obj;
		return Objects.equals(choices, other.choices);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "RespuestaIA ["
				+ "choices="
				+ choices
				+ "]";
	}
}
