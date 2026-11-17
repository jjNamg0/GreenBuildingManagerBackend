package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con el motivo para cancelar un evento.
 */
public class CancelacionDTO {

	/** Motivo de la cancelación. */
	private String motivo;

	/** Constructor por defecto. Crea una instancia vacía de CancelacionDTO. */
	public CancelacionDTO() {}

	/**
	 * Constructor con motivo.
	 *
	 * @param motivo Motivo de la cancelación.
	 */
	public CancelacionDTO(String motivo) {
		this.motivo = motivo;
	}

	/**
	 * Obtiene: motivo de la cancelación.
	 *
	 * @return Motivo de la cancelación
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Establece: motivo de la cancelación.
	 *
	 * @param motivo Motivo de la cancelación
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
		return Objects.hash(motivo);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos CancelacionDTO son iguales si
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
		CancelacionDTO other = (CancelacionDTO) obj;
		return Objects.equals(motivo, other.motivo);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Cancelacion ["
				+ "motivo="
				+ motivo
				+ "]";
	}
}
