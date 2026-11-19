package co.edu.unbosque.GreenBuilding.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase que representa a una persona invitada por correo a un evento o a una reserva. Se guarda para
 * avisarle si el plan se cancela.
 *
 * Esta entidad se almacena en la tabla "invitado" en la base de datos.
 */
@Entity
@Table(name = "invitado")
public class Invitado {

	/**
	 * Identificador único del invitado.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Identificador del evento al que fue invitado, o null si la invitación es a una reserva.
	 */
	private Long eventoId;

	/**
	 * Identificador de la reserva a la que fue invitado, o null si la invitación es a un evento.
	 */
	private Long reservaId;

	/**
	 * Correo electrónico del invitado.
	 */
	@Column(nullable = false)
	private String correo;

	/**
	 * Fecha y hora del último envío de la invitación.
	 */
	private LocalDateTime fechaEnvio;

	/**
	 * Constructor por defecto.
	 */
	public Invitado() {
	}

	/**
	 * Constructor con el evento o la reserva y el correo. La fecha de envío es la actual.
	 *
	 * @param eventoId Identificador del evento, o null
	 * @param reservaId Identificador de la reserva, o null
	 * @param correo Correo del invitado
	 */
	public Invitado(Long eventoId, Long reservaId, String correo) {
		this.eventoId = eventoId;
		this.reservaId = reservaId;
		this.correo = correo;
		this.fechaEnvio = LocalDateTime.now();
	}

	/**
	 * Obtiene el identificador del invitado.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del invitado.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del evento.
	 *
	 * @return El identificador del evento, o null
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
	 * Obtiene el identificador de la reserva.
	 *
	 * @return El identificador de la reserva, o null
	 */
	public Long getReservaId() {
		return reservaId;
	}

	/**
	 * Establece el identificador de la reserva.
	 *
	 * @param reservaId El identificador de la reserva
	 */
	public void setReservaId(Long reservaId) {
		this.reservaId = reservaId;
	}

	/**
	 * Obtiene el correo del invitado.
	 *
	 * @return El correo
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * Establece el correo del invitado.
	 *
	 * @param correo El nuevo correo
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * Obtiene la fecha del último envío.
	 *
	 * @return La fecha de envío
	 */
	public LocalDateTime getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * Establece la fecha del último envío.
	 *
	 * @param fechaEnvio La nueva fecha de envío
	 */
	public void setFechaEnvio(LocalDateTime fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Calcula el código hash para este invitado.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, eventoId, reservaId, correo);
	}

	/**
	 * Compara este invitado con otro objeto para determinar si son iguales.
	 *
	 * @param obj El objeto a comparar
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
		Invitado other = (Invitado) obj;
		return Objects.equals(id, other.id) && Objects.equals(eventoId, other.eventoId)
				&& Objects.equals(reservaId, other.reservaId) && Objects.equals(correo, other.correo);
	}

	/**
	 * Devuelve una representación en cadena de texto de este invitado.
	 *
	 * @return Una cadena que representa este invitado
	 */
	@Override
	public String toString() {
		return "Invitado [id=" + id + ", eventoId=" + eventoId + ", reservaId=" + reservaId + ", correo=" + correo
				+ "]";
	}
}
