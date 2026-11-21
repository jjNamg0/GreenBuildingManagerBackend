package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con un paso de una ruta de OSRM.
 */
public class PasoOsrmDTO {

	/** Maniobra que se hace en el paso. */
	private ManiobraOsrmDTO maneuver;

	/** Nombre de la vía. */
	private String name;

	/** Distancia del paso en metros. */
	private double distance;

	/** Duración del paso en segundos. */
	private double duration;

	/** Constructor por defecto. Crea una instancia vacía de PasoOsrmDTO. */
	public PasoOsrmDTO() {}

	/**
	 * Constructor con maneuver, name, distance, duration.
	 *
	 * @param maneuver Maniobra que se hace en el paso.
	 * @param name Nombre de la vía.
	 * @param distance Distancia del paso en metros.
	 * @param duration Duración del paso en segundos.
	 */
	public PasoOsrmDTO(ManiobraOsrmDTO maneuver, String name, double distance, double duration) {
		this.maneuver = maneuver;
		this.name = name;
		this.distance = distance;
		this.duration = duration;
	}

	/**
	 * Obtiene: maniobra que se hace en el paso.
	 *
	 * @return Maniobra que se hace en el paso
	 */
	public ManiobraOsrmDTO getManeuver() {
		return maneuver;
	}

	/**
	 * Establece: maniobra que se hace en el paso.
	 *
	 * @param maneuver Maniobra que se hace en el paso
	 */
	public void setManeuver(ManiobraOsrmDTO maneuver) {
		this.maneuver = maneuver;
	}

	/**
	 * Obtiene: nombre de la vía.
	 *
	 * @return Nombre de la vía
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece: nombre de la vía.
	 *
	 * @param name Nombre de la vía
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Obtiene: distancia del paso en metros.
	 *
	 * @return Distancia del paso en metros
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Establece: distancia del paso en metros.
	 *
	 * @param distance Distancia del paso en metros
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * Obtiene: duración del paso en segundos.
	 *
	 * @return Duración del paso en segundos
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Establece: duración del paso en segundos.
	 *
	 * @param duration Duración del paso en segundos
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(maneuver, name, distance, duration);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos PasoOsrmDTO son iguales si
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
		PasoOsrmDTO other = (PasoOsrmDTO) obj;
		return Objects.equals(maneuver, other.maneuver)
				&& Objects.equals(name, other.name)
				&& Double.doubleToLongBits(distance) == Double.doubleToLongBits(other.distance)
				&& Double.doubleToLongBits(duration) == Double.doubleToLongBits(other.duration);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "PasoOsrm ["
				+ "maneuver="
				+ maneuver
				+ ", name="
				+ name
				+ ", distance="
				+ distance
				+ ", duration="
				+ duration
				+ "]";
	}
}
