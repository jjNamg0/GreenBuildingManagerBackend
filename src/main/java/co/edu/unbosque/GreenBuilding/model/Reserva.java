package co.edu.unbosque.GreenBuilding.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase que representa la reserva de un espacio común hecha por un residente.
 *
 * Esta entidad se almacena en la tabla "reserva" en la base de datos.
 */
@Entity
@Table(name = "reserva")
public class Reserva {

	/**
	 * Identificador único de la reserva.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Identificador del espacio reservado.
	 */
	@Column(nullable = false)
	private Long espacioId;

	/**
	 * Identificador del residente que hizo la reserva.
	 */
	@Column(nullable = false)
	private Long usuarioId;

	/**
	 * Fecha de la reserva.
	 */
	private LocalDate fecha;

	/**
	 * Hora de inicio de la reserva.
	 */
	private LocalTime horaInicio;

	/**
	 * Hora de fin de la reserva.
	 */
	private LocalTime horaFin;

	/**
	 * Motivo de la reserva.
	 */
	@Column(length = 500)
	private String motivo;

	/**
	 * Número de personas que asistirán.
	 */
	private int asistentes;

	/**
	 * Estado de la reserva.
	 */
	@Enumerated(EnumType.STRING)
	private Estado estado;

	/**
	 * Observación del administrador (por ejemplo, el motivo de un rechazo).
	 */
	@Column(length = 500)
	private String observacion;

	/**
	 * Fecha y hora en que se creó la reserva.
	 */
	private LocalDateTime fechaCreacion;

	/**
	 * Código del enlace público de la invitación.
	 */
	@Column(unique = true, length = 40)
	private String codigoInvitacion;

	/**
	 * Constructor por defecto. La reserva empieza en estado PENDIENTE.
	 */
	public Reserva() {
		this.estado = Estado.PENDIENTE;
		this.fechaCreacion = LocalDateTime.now();
	}

	/**
	 * Enumeración de los estados de una reserva.
	 */
	public enum Estado {
		/** Esperando revisión del administrador */
		PENDIENTE,
		/** Aprobada por el administrador */
		APROBADA,
		/** Rechazada por el administrador */
		RECHAZADA,
		/** Cancelada por el residente */
		CANCELADA
	}

	/**
	 * Indica si la reserva ocupa el espacio (pendiente o aprobada).
	 *
	 * @return true si la reserva está activa
	 */
	public boolean estaActiva() {
		return estado == Estado.PENDIENTE || estado == Estado.APROBADA;
	}

	/**
	 * Indica si la reserva se cruza con un rango de horas del mismo día.
	 *
	 * @param inicio Hora de inicio del rango
	 * @param fin Hora de fin del rango
	 * @return true si hay cruce
	 */
	public boolean seCruzaCon(LocalTime inicio, LocalTime fin) {
		return horaInicio.isBefore(fin) && inicio.isBefore(horaFin);
	}

	/**
	 * Obtiene la fecha y hora en que empieza la reserva.
	 *
	 * @return Fecha y hora de inicio
	 */
	public LocalDateTime getInicio() {
		return LocalDateTime.of(fecha, horaInicio);
	}

	/**
	 * Obtiene el identificador de la reserva.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la reserva.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del espacio reservado.
	 *
	 * @return El identificador del espacio
	 */
	public Long getEspacioId() {
		return espacioId;
	}

	/**
	 * Establece el identificador del espacio reservado.
	 *
	 * @param espacioId El identificador del nuevo espacio
	 */
	public void setEspacioId(Long espacioId) {
		this.espacioId = espacioId;
	}

	/**
	 * Obtiene el identificador del residente que hizo la reserva.
	 *
	 * @return El identificador del usuario
	 */
	public Long getUsuarioId() {
		return usuarioId;
	}

	/**
	 * Establece el identificador del residente que hizo la reserva.
	 *
	 * @param usuarioId El identificador del usuario
	 */
	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	/**
	 * Obtiene la fecha de la reserva.
	 *
	 * @return La fecha
	 */
	public LocalDate getFecha() {
		return fecha;
	}

	/**
	 * Establece la fecha de la reserva.
	 *
	 * @param fecha La nueva fecha
	 */
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene la hora de inicio.
	 *
	 * @return La hora de inicio
	 */
	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece la hora de inicio.
	 *
	 * @param horaInicio La nueva hora de inicio
	 */
	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene la hora de fin.
	 *
	 * @return La hora de fin
	 */
	public LocalTime getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece la hora de fin.
	 *
	 * @param horaFin La nueva hora de fin
	 */
	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene el motivo de la reserva.
	 *
	 * @return El motivo
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Establece el motivo de la reserva.
	 *
	 * @param motivo El nuevo motivo
	 */
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	/**
	 * Obtiene el número de asistentes.
	 *
	 * @return Los asistentes
	 */
	public int getAsistentes() {
		return asistentes;
	}

	/**
	 * Establece el número de asistentes.
	 *
	 * @param asistentes Los asistentes
	 */
	public void setAsistentes(int asistentes) {
		this.asistentes = asistentes;
	}

	/**
	 * Obtiene el estado de la reserva.
	 *
	 * @return El estado
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * Establece el estado de la reserva.
	 *
	 * @param estado El nuevo estado
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene la observación del administrador.
	 *
	 * @return La observación
	 */
	public String getObservacion() {
		return observacion;
	}

	/**
	 * Establece la observación del administrador.
	 *
	 * @param observacion La nueva observación
	 */
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	/**
	 * Obtiene la fecha de creación de la reserva.
	 *
	 * @return La fecha de creación
	 */
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * Establece la fecha de creación de la reserva.
	 *
	 * @param fechaCreacion La fecha de creación
	 */
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * Obtiene el código del enlace público de la invitación.
	 *
	 * @return El código, o null si aún no tiene
	 */
	public String getCodigoInvitacion() {
		return codigoInvitacion;
	}

	/**
	 * Establece el código del enlace público de la invitación.
	 *
	 * @param codigoInvitacion El nuevo código
	 */
	public void setCodigoInvitacion(String codigoInvitacion) {
		this.codigoInvitacion = codigoInvitacion;
	}

	/**
	 * Calcula el código hash para esta reserva.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, fecha, horaInicio, horaFin);
	}

	/**
	 * Compara esta reserva con otro objeto para determinar si son iguales.
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
		Reserva other = (Reserva) obj;
		return Objects.equals(id, other.id) && Objects.equals(fecha, other.fecha)
				&& Objects.equals(horaInicio, other.horaInicio) && Objects.equals(horaFin, other.horaFin);
	}

	/**
	 * Devuelve una representación en cadena de texto de esta reserva.
	 *
	 * @return Una cadena que representa esta reserva
	 */
	@Override
	public String toString() {
		return "Reserva [id=" + id + ", fecha=" + fecha + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin
				+ ", estado=" + estado + "]";
	}

}
