package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con los puntos de una ruta de OSRM.
 */
public class GeometriaOsrmDTO {

	/** Puntos de la ruta, cada uno como [longitud, latitud]. */
	private List<double[]> coordinates;

	/** Constructor por defecto. Crea una instancia vacía de GeometriaOsrmDTO. */
	public GeometriaOsrmDTO() {}

	/**
	 * Constructor con coordinates.
	 *
	 * @param coordinates Puntos de la ruta, cada uno como [longitud, latitud].
	 */
	public GeometriaOsrmDTO(List<double[]> coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Obtiene: puntos de la ruta, cada uno como [longitud, latitud].
	 *
	 * @return Puntos de la ruta, cada uno como [longitud, latitud]
	 */
	public List<double[]> getCoordinates() {
		return coordinates;
	}

	/**
	 * Establece: puntos de la ruta, cada uno como [longitud, latitud].
	 *
	 * @param coordinates Puntos de la ruta, cada uno como [longitud, latitud]
	 */
	public void setCoordinates(List<double[]> coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(coordinates);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos GeometriaOsrmDTO son iguales si
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
		GeometriaOsrmDTO other = (GeometriaOsrmDTO) obj;
		return Objects.equals(coordinates, other.coordinates);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "GeometriaOsrm ["
				+ "coordinates="
				+ coordinates
				+ "]";
	}
}
