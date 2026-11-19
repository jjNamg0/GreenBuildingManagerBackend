package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con la lista de correos a los que se envía una invitación.
 */
public class CorreosInvitacionDTO {

	/** Correos de los invitados. */
	private List<String> correos;

	/**
	 * Constructor por defecto. Crea una instancia vacía de CorreosInvitacionDTO.
	 */
	public CorreosInvitacionDTO() {
	}

	/**
	 * Constructor con correos.
	 *
	 * @param correos Correos de los invitados.
	 */
	public CorreosInvitacionDTO(List<String> correos) {
		this.correos = correos;
	}

	/**
	 * Obtiene: correos de los invitados.
	 *
	 * @return Correos de los invitados
	 */
	public List<String> getCorreos() {
		return correos;
	}

	/**
	 * Establece: correos de los invitados.
	 *
	 * @param correos Correos de los invitados
	 */
	public void setCorreos(List<String> correos) {
		this.correos = correos;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en
	 * colecciones basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(correos);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos
	 * CorreosInvitacionDTO son iguales si todos sus atributos son iguales.
	 *
	 * @param obj Objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CorreosInvitacionDTO other = (CorreosInvitacionDTO) obj;
		return Objects.equals(correos, other.correos);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para
	 * depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "CorreosInvitacion [" + "correos=" + correos + "]";
	}
}
