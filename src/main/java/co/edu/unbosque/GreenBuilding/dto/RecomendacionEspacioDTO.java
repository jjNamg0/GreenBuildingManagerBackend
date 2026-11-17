package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con un espacio recomendado, su puntaje y el motivo de la recomendación.
 */
public class RecomendacionEspacioDTO {

	/** Espacio recomendado. */
	private EspacioDTO espacio;

	/** Puntaje de 0 a 100. */
	private int puntaje;

	/** Motivo de la recomendación. */
	private String motivo;

	/** Constructor por defecto. Crea una instancia vacía de RecomendacionEspacioDTO. */
	public RecomendacionEspacioDTO() {}

	/**
	 * Constructor con espacio, puntaje, motivo.
	 *
	 * @param espacio Espacio recomendado.
	 * @param puntaje Puntaje de 0 a 100.
	 * @param motivo Motivo de la recomendación.
	 */
	public RecomendacionEspacioDTO(EspacioDTO espacio, int puntaje, String motivo) {
		this.espacio = espacio;
		this.puntaje = puntaje;
		this.motivo = motivo;
	}

	/**
	 * Obtiene: espacio recomendado.
	 *
	 * @return Espacio recomendado
	 */
	public EspacioDTO getEspacio() {
		return espacio;
	}

	/**
	 * Establece: espacio recomendado.
	 *
	 * @param espacio Espacio recomendado
	 */
	public void setEspacio(EspacioDTO espacio) {
		this.espacio = espacio;
	}

	/**
	 * Obtiene: puntaje de 0 a 100.
	 *
	 * @return Puntaje de 0 a 100
	 */
	public int getPuntaje() {
		return puntaje;
	}

	/**
	 * Establece: puntaje de 0 a 100.
	 *
	 * @param puntaje Puntaje de 0 a 100
	 */
	public void setPuntaje(int puntaje) {
		this.puntaje = puntaje;
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
		return Objects.hash(espacio, puntaje, motivo);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RecomendacionEspacioDTO son iguales si
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
		RecomendacionEspacioDTO other = (RecomendacionEspacioDTO) obj;
		return Objects.equals(espacio, other.espacio)
				&& puntaje == other.puntaje
				&& Objects.equals(motivo, other.motivo);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "RecomendacionEspacio ["
				+ "espacio="
				+ espacio
				+ ", puntaje="
				+ puntaje
				+ ", motivo="
				+ motivo
				+ "]";
	}
}
