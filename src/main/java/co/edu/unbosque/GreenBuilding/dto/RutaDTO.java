package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con la ruta calculada por OSRM: distancia, duración, puntos [lat, lng] y pasos.
 */
public class RutaDTO {

	/** Distancia total en metros. */
	private double distanciaMetros;

	/** Duración total en segundos. */
	private double duracionSegundos;

	/** Puntos de la ruta en formato [latitud, longitud]. */
	private List<double[]> puntos;

	/** Instrucciones paso a paso. */
	private List<PasoRutaDTO> pasos;

	/** Constructor por defecto. Crea una instancia vacía de RutaDTO. */
	public RutaDTO() {}

	/**
	 * Constructor con distanciaMetros, duracionSegundos, puntos, pasos.
	 *
	 * @param distanciaMetros Distancia total en metros.
	 * @param duracionSegundos Duración total en segundos.
	 * @param puntos Puntos de la ruta en formato [latitud, longitud].
	 * @param pasos Instrucciones paso a paso.
	 */
	public RutaDTO(double distanciaMetros, double duracionSegundos, List<double[]> puntos, List<PasoRutaDTO> pasos) {
		this.distanciaMetros = distanciaMetros;
		this.duracionSegundos = duracionSegundos;
		this.puntos = puntos;
		this.pasos = pasos;
	}

	/**
	 * Obtiene: distancia total en metros.
	 *
	 * @return Distancia total en metros
	 */
	public double getDistanciaMetros() {
		return distanciaMetros;
	}

	/**
	 * Establece: distancia total en metros.
	 *
	 * @param distanciaMetros Distancia total en metros
	 */
	public void setDistanciaMetros(double distanciaMetros) {
		this.distanciaMetros = distanciaMetros;
	}

	/**
	 * Obtiene: duración total en segundos.
	 *
	 * @return Duración total en segundos
	 */
	public double getDuracionSegundos() {
		return duracionSegundos;
	}

	/**
	 * Establece: duración total en segundos.
	 *
	 * @param duracionSegundos Duración total en segundos
	 */
	public void setDuracionSegundos(double duracionSegundos) {
		this.duracionSegundos = duracionSegundos;
	}

	/**
	 * Obtiene: puntos de la ruta en formato [latitud, longitud].
	 *
	 * @return Puntos de la ruta en formato [latitud, longitud]
	 */
	public List<double[]> getPuntos() {
		return puntos;
	}

	/**
	 * Establece: puntos de la ruta en formato [latitud, longitud].
	 *
	 * @param puntos Puntos de la ruta en formato [latitud, longitud]
	 */
	public void setPuntos(List<double[]> puntos) {
		this.puntos = puntos;
	}

	/**
	 * Obtiene: instrucciones paso a paso.
	 *
	 * @return Instrucciones paso a paso
	 */
	public List<PasoRutaDTO> getPasos() {
		return pasos;
	}

	/**
	 * Establece: instrucciones paso a paso.
	 *
	 * @param pasos Instrucciones paso a paso
	 */
	public void setPasos(List<PasoRutaDTO> pasos) {
		this.pasos = pasos;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(distanciaMetros, duracionSegundos, puntos, pasos);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RutaDTO son iguales si
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
		RutaDTO other = (RutaDTO) obj;
		return distanciaMetros == other.distanciaMetros
				&& duracionSegundos == other.duracionSegundos
				&& Objects.equals(puntos, other.puntos)
				&& Objects.equals(pasos, other.pasos);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Ruta ["
				+ "distanciaMetros="
				+ distanciaMetros
				+ ", duracionSegundos="
				+ duracionSegundos
				+ ", puntos="
				+ puntos
				+ ", pasos="
				+ pasos
				+ "]";
	}
}
