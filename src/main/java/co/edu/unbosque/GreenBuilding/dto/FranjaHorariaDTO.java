package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO que representa una franja de una hora en el calendario de un espacio.
 */
public class FranjaHorariaDTO {

	/** Hora de inicio de la franja (HH:mm). */
	private String horaInicio;

	/** Hora de fin de la franja (HH:mm). */
	private String horaFin;

	/** Indica si la franja se puede reservar. */
	private boolean disponible;

	/** Constructor por defecto. Crea una instancia vacía de FranjaHorariaDTO. */
	public FranjaHorariaDTO() {}

	/**
	 * Constructor con horaInicio, horaFin, disponible.
	 *
	 * @param horaInicio Hora de inicio de la franja (HH:mm).
	 * @param horaFin Hora de fin de la franja (HH:mm).
	 * @param disponible Indica si la franja se puede reservar.
	 */
	public FranjaHorariaDTO(String horaInicio, String horaFin, boolean disponible) {
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.disponible = disponible;
	}

	/**
	 * Obtiene: hora de inicio de la franja (HH:mm).
	 *
	 * @return Hora de inicio de la franja (HH:mm)
	 */
	public String getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece: hora de inicio de la franja (HH:mm).
	 *
	 * @param horaInicio Hora de inicio de la franja (HH:mm)
	 */
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene: hora de fin de la franja (HH:mm).
	 *
	 * @return Hora de fin de la franja (HH:mm)
	 */
	public String getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece: hora de fin de la franja (HH:mm).
	 *
	 * @param horaFin Hora de fin de la franja (HH:mm)
	 */
	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene: indica si la franja se puede reservar.
	 *
	 * @return Indica si la franja se puede reservar
	 */
	public boolean isDisponible() {
		return disponible;
	}

	/**
	 * Establece: indica si la franja se puede reservar.
	 *
	 * @param disponible Indica si la franja se puede reservar
	 */
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(horaInicio, horaFin, disponible);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos FranjaHorariaDTO son iguales si
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
		FranjaHorariaDTO other = (FranjaHorariaDTO) obj;
		return Objects.equals(horaInicio, other.horaInicio)
				&& Objects.equals(horaFin, other.horaFin)
				&& disponible == other.disponible;
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "FranjaHoraria ["
				+ "horaInicio="
				+ horaInicio
				+ ", horaFin="
				+ horaFin
				+ ", disponible="
				+ disponible
				+ "]";
	}
}
