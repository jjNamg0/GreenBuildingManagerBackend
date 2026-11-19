package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con un evento recomendado, su puntaje y el motivo de la recomendación.
 */
public class RecomendacionEventoDTO {

	/** Evento recomendado. */
	private EventoDTO evento;

	/** Puntaje de 0 a 100. */
	private int puntaje;

	/** Motivo de la recomendación. */
	private String motivo;

	/** Constructor por defecto. Crea una instancia vacía de RecomendacionEventoDTO. */
	public RecomendacionEventoDTO() {}

	/**
	 * Constructor con evento, puntaje, motivo.
	 *
	 * @param evento Evento recomendado.
	 * @param puntaje Puntaje de 0 a 100.
	 * @param motivo Motivo de la recomendación.
	 */
	public RecomendacionEventoDTO(EventoDTO evento, int puntaje, String motivo) {
		this.evento = evento;
		this.puntaje = puntaje;
		this.motivo = motivo;
	}

	/**
	 * Obtiene: evento recomendado.
	 *
	 * @return Evento recomendado
	 */
	public EventoDTO getEvento() {
		return evento;
	}

	/**
	 * Establece: evento recomendado.
	 *
	 * @param evento Evento recomendado
	 */
	public void setEvento(EventoDTO evento) {
		this.evento = evento;
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
		return Objects.hash(evento, puntaje, motivo);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RecomendacionEventoDTO son iguales si
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
		RecomendacionEventoDTO other = (RecomendacionEventoDTO) obj;
		return Objects.equals(evento, other.evento)
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
		return "RecomendacionEvento ["
				+ "evento="
				+ evento
				+ ", puntaje="
				+ puntaje
				+ ", motivo="
				+ motivo
				+ "]";
	}
}
