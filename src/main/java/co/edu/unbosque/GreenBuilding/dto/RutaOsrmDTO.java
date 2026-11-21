package co.edu.unbosque.GreenBuilding.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO con una ruta de la respuesta de OSRM.
 */
public class RutaOsrmDTO {

	/** Distancia total en metros. */
	private double distance;

	/** Duración total en segundos. */
	private double duration;

	/** Puntos por los que pasa la ruta. */
	private GeometriaOsrmDTO geometry;

	/** Tramos de la ruta. */
	private List<TramoOsrmDTO> legs;

	/** Constructor por defecto. Crea una instancia vacía de RutaOsrmDTO. */
	public RutaOsrmDTO() {}

	/**
	 * Constructor con distance, duration, geometry, legs.
	 *
	 * @param distance Distancia total en metros.
	 * @param duration Duración total en segundos.
	 * @param geometry Puntos por los que pasa la ruta.
	 * @param legs Tramos de la ruta.
	 */
	public RutaOsrmDTO(double distance, double duration, GeometriaOsrmDTO geometry, List<TramoOsrmDTO> legs) {
		this.distance = distance;
		this.duration = duration;
		this.geometry = geometry;
		if (legs == null) {
			this.legs = null;
		} else {
			this.legs = new ArrayList<>(legs);
		}
	}

	/**
	 * Obtiene: distancia total en metros.
	 *
	 * @return Distancia total en metros
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Establece: distancia total en metros.
	 *
	 * @param distance Distancia total en metros
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * Obtiene: duración total en segundos.
	 *
	 * @return Duración total en segundos
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Establece: duración total en segundos.
	 *
	 * @param duration Duración total en segundos
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * Obtiene: puntos por los que pasa la ruta.
	 *
	 * @return Puntos por los que pasa la ruta
	 */
	public GeometriaOsrmDTO getGeometry() {
		return geometry;
	}

	/**
	 * Establece: puntos por los que pasa la ruta.
	 *
	 * @param geometry Puntos por los que pasa la ruta
	 */
	public void setGeometry(GeometriaOsrmDTO geometry) {
		this.geometry = geometry;
	}

	/**
	 * Obtiene: tramos de la ruta.
	 *
	 * @return Tramos de la ruta
	 */
	public List<TramoOsrmDTO> getLegs() {
		return legs;
	}

	/**
	 * Establece: tramos de la ruta.
	 *
	 * @param legs Tramos de la ruta
	 */
	public void setLegs(List<TramoOsrmDTO> legs) {
		if (legs == null) {
			this.legs = null;
		} else {
			this.legs = new ArrayList<>(legs);
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
		return Objects.hash(distance, duration, geometry, legs);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RutaOsrmDTO son iguales si
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
		RutaOsrmDTO other = (RutaOsrmDTO) obj;
		return Double.doubleToLongBits(distance) == Double.doubleToLongBits(other.distance)
				&& Double.doubleToLongBits(duration) == Double.doubleToLongBits(other.duration)
				&& Objects.equals(geometry, other.geometry)
				&& Objects.equals(legs, other.legs);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "RutaOsrm ["
				+ "distance="
				+ distance
				+ ", duration="
				+ duration
				+ ", geometry="
				+ geometry
				+ ", legs="
				+ legs
				+ "]";
	}
}
