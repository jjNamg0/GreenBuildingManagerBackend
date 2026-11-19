package co.edu.unbosque.GreenBuilding.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO con un tramo de una ruta de OSRM.
 */
public class TramoOsrmDTO {

	/** Pasos del tramo. */
	private List<PasoOsrmDTO> steps;

	/** Constructor por defecto. Crea una instancia vacía de TramoOsrmDTO. */
	public TramoOsrmDTO() {}

	/**
	 * Constructor con steps.
	 *
	 * @param steps Pasos del tramo.
	 */
	public TramoOsrmDTO(List<PasoOsrmDTO> steps) {
		if (steps == null) {
			this.steps = null;
		} else {
			this.steps = new ArrayList<>(steps);
		}
	}

	/**
	 * Obtiene: pasos del tramo.
	 *
	 * @return Pasos del tramo
	 */
	public List<PasoOsrmDTO> getSteps() {
		return steps;
	}

	/**
	 * Establece: pasos del tramo.
	 *
	 * @param steps Pasos del tramo
	 */
	public void setSteps(List<PasoOsrmDTO> steps) {
		if (steps == null) {
			this.steps = null;
		} else {
			this.steps = new ArrayList<>(steps);
		}
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(steps);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos TramoOsrmDTO son iguales si
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
		TramoOsrmDTO other = (TramoOsrmDTO) obj;
		return Objects.equals(steps, other.steps);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "TramoOsrm ["
				+ "steps="
				+ steps
				+ "]";
	}
}
