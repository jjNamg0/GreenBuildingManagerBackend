package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con una instrucción de la ruta.
 */
public class PasoRutaDTO {

	/** Instrucción en español. */
	private String instruccion;

	/** Distancia del paso en metros. */
	private double distanciaMetros;

	/** Duración del paso en segundos. */
	private double duracionSegundos;

	/** Constructor por defecto. Crea una instancia vacía de PasoRutaDTO. */
	public PasoRutaDTO() {}

	/**
	 * Constructor con instruccion, distanciaMetros, duracionSegundos.
	 *
	 * @param instruccion Instrucción en español.
	 * @param distanciaMetros Distancia del paso en metros.
	 * @param duracionSegundos Duración del paso en segundos.
	 */
	public PasoRutaDTO(String instruccion, double distanciaMetros, double duracionSegundos) {
		this.instruccion = instruccion;
		this.distanciaMetros = distanciaMetros;
		this.duracionSegundos = duracionSegundos;
	}

	/**
	 * Obtiene: instrucción en español.
	 *
	 * @return Instrucción en español
	 */
	public String getInstruccion() {
		return instruccion;
	}

	/**
	 * Establece: instrucción en español.
	 *
	 * @param instruccion Instrucción en español
	 */
	public void setInstruccion(String instruccion) {
		this.instruccion = instruccion;
	}

	/**
	 * Obtiene: distancia del paso en metros.
	 *
	 * @return Distancia del paso en metros
	 */
	public double getDistanciaMetros() {
		return distanciaMetros;
	}

	/**
	 * Establece: distancia del paso en metros.
	 *
	 * @param distanciaMetros Distancia del paso en metros
	 */
	public void setDistanciaMetros(double distanciaMetros) {
		this.distanciaMetros = distanciaMetros;
	}

	/**
	 * Obtiene: duración del paso en segundos.
	 *
	 * @return Duración del paso en segundos
	 */
	public double getDuracionSegundos() {
		return duracionSegundos;
	}

	/**
	 * Establece: duración del paso en segundos.
	 *
	 * @param duracionSegundos Duración del paso en segundos
	 */
	public void setDuracionSegundos(double duracionSegundos) {
		this.duracionSegundos = duracionSegundos;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(instruccion, distanciaMetros, duracionSegundos);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos PasoRutaDTO son iguales si
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
		PasoRutaDTO other = (PasoRutaDTO) obj;
		return Objects.equals(instruccion, other.instruccion)
				&& Double.doubleToLongBits(distanciaMetros) == Double.doubleToLongBits(other.distanciaMetros)
				&& Double.doubleToLongBits(duracionSegundos) == Double.doubleToLongBits(other.duracionSegundos);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "PasoRuta ["
				+ "instruccion="
				+ instruccion
				+ ", distanciaMetros="
				+ distanciaMetros
				+ ", duracionSegundos="
				+ duracionSegundos
				+ "]";
	}
}
