package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con un conjunto cercano a la ubicación del visitante.
 */
public class RecomendacionConjuntoDTO {

	/** Conjunto cercano. */
	private ConjuntoDTO conjunto;

	/** Distancia en kilómetros. */
	private double distanciaKm;

	/** Motivo de la recomendación. */
	private String motivo;

	/** Constructor por defecto. Crea una instancia vacía de RecomendacionConjuntoDTO. */
	public RecomendacionConjuntoDTO() {}

	/**
	 * Constructor con conjunto, distanciaKm, motivo.
	 *
	 * @param conjunto Conjunto cercano.
	 * @param distanciaKm Distancia en kilómetros.
	 * @param motivo Motivo de la recomendación.
	 */
	public RecomendacionConjuntoDTO(ConjuntoDTO conjunto, double distanciaKm, String motivo) {
		this.conjunto = conjunto;
		this.distanciaKm = distanciaKm;
		this.motivo = motivo;
	}

	/**
	 * Obtiene: conjunto cercano.
	 *
	 * @return Conjunto cercano
	 */
	public ConjuntoDTO getConjunto() {
		return conjunto;
	}

	/**
	 * Establece: conjunto cercano.
	 *
	 * @param conjunto Conjunto cercano
	 */
	public void setConjunto(ConjuntoDTO conjunto) {
		this.conjunto = conjunto;
	}

	/**
	 * Obtiene: distancia en kilómetros.
	 *
	 * @return Distancia en kilómetros
	 */
	public double getDistanciaKm() {
		return distanciaKm;
	}

	/**
	 * Establece: distancia en kilómetros.
	 *
	 * @param distanciaKm Distancia en kilómetros
	 */
	public void setDistanciaKm(double distanciaKm) {
		this.distanciaKm = distanciaKm;
	}

	/**
	 * Obtiene: motivo de la recomendación.
	 *
	 * @return Motivo de la recomendación
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Establece: motivo de la recomendación.
	 *
	 * @param motivo Motivo de la recomendación
	 */
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(conjunto, distanciaKm, motivo);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RecomendacionConjuntoDTO son iguales si
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
		RecomendacionConjuntoDTO other = (RecomendacionConjuntoDTO) obj;
		return Objects.equals(conjunto, other.conjunto)
				&& Double.doubleToLongBits(distanciaKm) == Double.doubleToLongBits(other.distanciaKm)
				&& Objects.equals(motivo, other.motivo);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "RecomendacionConjunto ["
				+ "conjunto="
				+ conjunto
				+ ", distanciaKm="
				+ distanciaKm
				+ ", motivo="
				+ motivo
				+ "]";
	}
}
