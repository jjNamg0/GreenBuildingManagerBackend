package co.edu.unbosque.GreenBuilding.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase que representa la asistencia confirmada de un residente a un evento.
 *
 * Esta entidad se almacena en la tabla "asistencia" en la base de datos.
 */
@Entity
@Table(name = "asistencia")
public class Asistencia {

	/**
	 * Identificador único de la asistencia.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Identificador del evento al que asiste el residente.
	 */
	@Column(nullable = false)
	private Long eventoId;

	/**
	 * Identificador del residente que confirmó asistencia.
	 */
	@Column(nullable = false)
	private Long usuarioId;

	/**
	 * Constructor por defecto.
	 */
	public Asistencia() {
	}

	/**
	 * Constructor con el evento y el residente.
	 *
	 * @param eventoId Identificador del evento
	 * @param usuarioId Identificador del residente
	 */
	public Asistencia(Long eventoId, Long usuarioId) {
		this.eventoId = eventoId;
		this.usuarioId = usuarioId;
	}

	/**
	 * Obtiene el identificador de la asistencia.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la asistencia.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del evento.
	 *
	 * @return El identificador del evento
	 */
	public Long getEventoId() {
		return eventoId;
	}

	/**
	 * Establece el identificador del evento.
	 *
	 * @param eventoId El identificador del evento
	 */
	public void setEventoId(Long eventoId) {
		this.eventoId = eventoId;
	}

	/**
	 * Obtiene el identificador del residente.
	 *
	 * @return El identificador del residente
	 */
	public Long getUsuarioId() {
		return usuarioId;
	}

	/**
	 * Establece el identificador del residente.
	 *
	 * @param usuarioId El identificador del residente
	 */
	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	/**
	 * Calcula el código hash para esta asistencia.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, eventoId, usuarioId);
	}

	/**
	 * Compara esta asistencia con otro objeto para determinar si son iguales.
	 *
	 * @param obj El objeto a comparar con esta asistencia
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
		Asistencia other = (Asistencia) obj;
		return Objects.equals(id, other.id) && Objects.equals(eventoId, other.eventoId)
				&& Objects.equals(usuarioId, other.usuarioId);
	}

	/**
	 * Devuelve una representación en cadena de texto de esta asistencia.
	 *
	 * @return Una cadena que representa esta asistencia
	 */
	@Override
	public String toString() {
		return "Asistencia [id=" + id + ", eventoId=" + eventoId + ", usuarioId=" + usuarioId + "]";
	}
}
