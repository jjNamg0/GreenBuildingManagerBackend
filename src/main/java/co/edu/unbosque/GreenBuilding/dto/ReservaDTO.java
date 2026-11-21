package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Reserva.Estado;
import java.util.Objects;

/**
 * DTO para reservas de espacios. Las fechas viajan como AAAA-MM-DD y las horas como HH:mm.
 */
public class ReservaDTO {

	/** Identificador único de la reserva. */
	private Long id;

	/** Identificador del espacio reservado. */
	private Long espacioId;

	/** Nombre del espacio reservado. */
	private String espacioNombre;

	/** Nombre del conjunto del espacio. */
	private String conjuntoNombre;

	/** Identificador del residente. */
	private Long usuarioId;

	/** Nombre completo del residente. */
	private String usuarioNombre;

	/** Unidad del residente. */
	private String unidadDescripcion;

	/** Fecha de la reserva (AAAA-MM-DD). */
	private String fecha;

	/** Hora de inicio (HH:mm). */
	private String horaInicio;

	/** Hora de fin (HH:mm). */
	private String horaFin;

	/** Motivo de la reserva. */
	private String motivo;

	/** Número de personas que asistirán. */
	private Integer asistentes;

	/** Estado de la reserva. */
	private Estado estado;

	/** Observación del administrador. */
	private String observacion;

	/** Fecha y hora en que se creó la reserva. */
	private String fechaCreacion;

	/** Constructor por defecto. Crea una instancia vacía de ReservaDTO. */
	public ReservaDTO() {}

	/**
	 * Constructor con espacioId, fecha, horaInicio, horaFin, motivo, asistentes.
	 *
	 * @param espacioId Identificador del espacio reservado.
	 * @param fecha Fecha de la reserva (AAAA-MM-DD).
	 * @param horaInicio Hora de inicio (HH:mm).
	 * @param horaFin Hora de fin (HH:mm).
	 * @param motivo Motivo de la reserva.
	 * @param asistentes Número de personas que asistirán.
	 */
	public ReservaDTO(Long espacioId, String fecha, String horaInicio, String horaFin, String motivo, Integer asistentes) {
		this.espacioId = espacioId;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.motivo = motivo;
		this.asistentes = asistentes;
	}

	/**
	 * Obtiene: identificador único de la reserva.
	 *
	 * @return Identificador único de la reserva
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único de la reserva.
	 *
	 * @param id Identificador único de la reserva
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene: identificador del espacio reservado.
	 *
	 * @return Identificador del espacio reservado
	 */
	public Long getEspacioId() {
		return espacioId;
	}

	/**
	 * Establece: identificador del espacio reservado.
	 *
	 * @param espacioId Identificador del espacio reservado
	 */
	public void setEspacioId(Long espacioId) {
		this.espacioId = espacioId;
	}

	/**
	 * Obtiene: nombre del espacio reservado.
	 *
	 * @return Nombre del espacio reservado
	 */
	public String getEspacioNombre() {
		return espacioNombre;
	}

	/**
	 * Establece: nombre del espacio reservado.
	 *
	 * @param espacioNombre Nombre del espacio reservado
	 */
	public void setEspacioNombre(String espacioNombre) {
		this.espacioNombre = espacioNombre;
	}

	/**
	 * Obtiene: nombre del conjunto del espacio.
	 *
	 * @return Nombre del conjunto del espacio
	 */
	public String getConjuntoNombre() {
		return conjuntoNombre;
	}

	/**
	 * Establece: nombre del conjunto del espacio.
	 *
	 * @param conjuntoNombre Nombre del conjunto del espacio
	 */
	public void setConjuntoNombre(String conjuntoNombre) {
		this.conjuntoNombre = conjuntoNombre;
	}

	/**
	 * Obtiene: identificador del residente.
	 *
	 * @return Identificador del residente
	 */
	public Long getUsuarioId() {
		return usuarioId;
	}

	/**
	 * Establece: identificador del residente.
	 *
	 * @param usuarioId Identificador del residente
	 */
	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	/**
	 * Obtiene: nombre completo del residente.
	 *
	 * @return Nombre completo del residente
	 */
	public String getUsuarioNombre() {
		return usuarioNombre;
	}

	/**
	 * Establece: nombre completo del residente.
	 *
	 * @param usuarioNombre Nombre completo del residente
	 */
	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
	}

	/**
	 * Obtiene: unidad del residente.
	 *
	 * @return Unidad del residente
	 */
	public String getUnidadDescripcion() {
		return unidadDescripcion;
	}

	/**
	 * Establece: unidad del residente.
	 *
	 * @param unidadDescripcion Unidad del residente
	 */
	public void setUnidadDescripcion(String unidadDescripcion) {
		this.unidadDescripcion = unidadDescripcion;
	}

	/**
	 * Obtiene: fecha de la reserva (AAAA-MM-DD).
	 *
	 * @return Fecha de la reserva (AAAA-MM-DD)
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * Establece: fecha de la reserva (AAAA-MM-DD).
	 *
	 * @param fecha Fecha de la reserva (AAAA-MM-DD)
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene: hora de inicio (HH:mm).
	 *
	 * @return Hora de inicio (HH:mm)
	 */
	public String getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece: hora de inicio (HH:mm).
	 *
	 * @param horaInicio Hora de inicio (HH:mm)
	 */
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene: hora de fin (HH:mm).
	 *
	 * @return Hora de fin (HH:mm)
	 */
	public String getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece: hora de fin (HH:mm).
	 *
	 * @param horaFin Hora de fin (HH:mm)
	 */
	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene: motivo de la reserva.
	 *
	 * @return Motivo de la reserva
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Establece: motivo de la reserva.
	 *
	 * @param motivo Motivo de la reserva
	 */
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	/**
	 * Obtiene: número de personas que asistirán.
	 *
	 * @return Número de personas que asistirán
	 */
	public Integer getAsistentes() {
		return asistentes;
	}

	/**
	 * Establece: número de personas que asistirán.
	 *
	 * @param asistentes Número de personas que asistirán
	 */
	public void setAsistentes(Integer asistentes) {
		this.asistentes = asistentes;
	}

	/**
	 * Obtiene: estado de la reserva.
	 *
	 * @return Estado de la reserva
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * Establece: estado de la reserva.
	 *
	 * @param estado Estado de la reserva
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene: observación del administrador.
	 *
	 * @return Observación del administrador
	 */
	public String getObservacion() {
		return observacion;
	}

	/**
	 * Establece: observación del administrador.
	 *
	 * @param observacion Observación del administrador
	 */
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	/**
	 * Obtiene: fecha y hora en que se creó la reserva.
	 *
	 * @return Fecha y hora en que se creó la reserva
	 */
	public String getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * Establece: fecha y hora en que se creó la reserva.
	 *
	 * @param fechaCreacion Fecha y hora en que se creó la reserva
	 */
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, espacioId, espacioNombre, conjuntoNombre, usuarioId, usuarioNombre, unidadDescripcion, fecha, horaInicio, horaFin, motivo, asistentes, estado, observacion, fechaCreacion);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos ReservaDTO son iguales si
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
		ReservaDTO other = (ReservaDTO) obj;
		return mismosDatosPrincipales(other) && mismosDatosAdicionales(other);
	}

	/**
	 * Compara la primera mitad de los atributos de este objeto con los de otro.
	 *
	 * @param other Objeto a comparar
	 * @return true si esos atributos son iguales, false en caso contrario
	 */
	private boolean mismosDatosPrincipales(ReservaDTO other) {
		return Objects.equals(id, other.id)
				&& Objects.equals(espacioId, other.espacioId)
				&& Objects.equals(espacioNombre, other.espacioNombre)
				&& Objects.equals(conjuntoNombre, other.conjuntoNombre)
				&& Objects.equals(usuarioId, other.usuarioId)
				&& Objects.equals(usuarioNombre, other.usuarioNombre)
				&& Objects.equals(unidadDescripcion, other.unidadDescripcion)
				&& Objects.equals(fecha, other.fecha);
	}

	/**
	 * Compara la segunda mitad de los atributos de este objeto con los de otro.
	 *
	 * @param other Objeto a comparar
	 * @return true si esos atributos son iguales, false en caso contrario
	 */
	private boolean mismosDatosAdicionales(ReservaDTO other) {
		return Objects.equals(horaInicio, other.horaInicio)
				&& Objects.equals(horaFin, other.horaFin)
				&& Objects.equals(motivo, other.motivo)
				&& Objects.equals(asistentes, other.asistentes)
				&& estado == other.estado
				&& Objects.equals(observacion, other.observacion)
				&& Objects.equals(fechaCreacion, other.fechaCreacion);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Reserva ["
				+ "id="
				+ id
				+ ", espacioId="
				+ espacioId
				+ ", espacioNombre="
				+ espacioNombre
				+ ", conjuntoNombre="
				+ conjuntoNombre
				+ ", usuarioId="
				+ usuarioId
				+ ", usuarioNombre="
				+ usuarioNombre
				+ ", unidadDescripcion="
				+ unidadDescripcion
				+ ", fecha="
				+ fecha
				+ ", horaInicio="
				+ horaInicio
				+ ", horaFin="
				+ horaFin
				+ ", motivo="
				+ motivo
				+ ", asistentes="
				+ asistentes
				+ ", estado="
				+ estado
				+ ", observacion="
				+ observacion
				+ ", fechaCreacion="
				+ fechaCreacion
				+ "]";
	}
}
