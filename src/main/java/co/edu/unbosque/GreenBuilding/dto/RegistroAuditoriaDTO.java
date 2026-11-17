package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO para los registros de auditoría que ve el administrador.
 */
public class RegistroAuditoriaDTO {

	/** Identificador único del registro. */
	private Long id;

	/** Correo del usuario. */
	private String username;

	/** Nombre del usuario. */
	private String nombreUsuario;

	/** Rol del usuario. */
	private String role;

	/** Fecha y hora de la acción (ISO). */
	private String fecha;

	/** Acción realizada. */
	private String accion;

	/** Entidad afectada. */
	private String entidad;

	/** Descripción de la acción. */
	private String motivo;

	/** Ubicación en texto, o "Sin ubicación". */
	private String ubicacion;

	/** Latitud de la acción. */
	private Double latitud;

	/** Longitud de la acción. */
	private Double longitud;

	/** Dirección IP. */
	private String ip;

	/** Constructor por defecto. Crea una instancia vacía de RegistroAuditoriaDTO. */
	public RegistroAuditoriaDTO() {}

	/**
	 * Obtiene: identificador único del registro.
	 *
	 * @return Identificador único del registro
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único del registro.
	 *
	 * @param id Identificador único del registro
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene: correo del usuario.
	 *
	 * @return Correo del usuario
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece: correo del usuario.
	 *
	 * @param username Correo del usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene: nombre del usuario.
	 *
	 * @return Nombre del usuario
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	/**
	 * Establece: nombre del usuario.
	 *
	 * @param nombreUsuario Nombre del usuario
	 */
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	/**
	 * Obtiene: rol del usuario.
	 *
	 * @return Rol del usuario
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Establece: rol del usuario.
	 *
	 * @param role Rol del usuario
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Obtiene: fecha y hora de la acción (ISO).
	 *
	 * @return Fecha y hora de la acción (ISO)
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * Establece: fecha y hora de la acción (ISO).
	 *
	 * @param fecha Fecha y hora de la acción (ISO)
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene: acción realizada.
	 *
	 * @return Acción realizada
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * Establece: acción realizada.
	 *
	 * @param accion Acción realizada
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * Obtiene: entidad afectada.
	 *
	 * @return Entidad afectada
	 */
	public String getEntidad() {
		return entidad;
	}

	/**
	 * Establece: entidad afectada.
	 *
	 * @param entidad Entidad afectada
	 */
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	/**
	 * Obtiene: descripción de la acción.
	 *
	 * @return Descripción de la acción
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Establece: descripción de la acción.
	 *
	 * @param motivo Descripción de la acción
	 */
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	/**
	 * Obtiene: ubicación en texto, o "Sin ubicación".
	 *
	 * @return Ubicación en texto, o "Sin ubicación"
	 */
	public String getUbicacion() {
		return ubicacion;
	}

	/**
	 * Establece: ubicación en texto, o "Sin ubicación".
	 *
	 * @param ubicacion Ubicación en texto, o "Sin ubicación"
	 */
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	/**
	 * Obtiene: latitud de la acción.
	 *
	 * @return Latitud de la acción
	 */
	public Double getLatitud() {
		return latitud;
	}

	/**
	 * Establece: latitud de la acción.
	 *
	 * @param latitud Latitud de la acción
	 */
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	/**
	 * Obtiene: longitud de la acción.
	 *
	 * @return Longitud de la acción
	 */
	public Double getLongitud() {
		return longitud;
	}

	/**
	 * Establece: longitud de la acción.
	 *
	 * @param longitud Longitud de la acción
	 */
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	/**
	 * Obtiene: dirección IP.
	 *
	 * @return Dirección IP
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Establece: dirección IP.
	 *
	 * @param ip Dirección IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, username, nombreUsuario, role, fecha, accion, entidad, motivo, ubicacion, latitud, longitud, ip);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RegistroAuditoriaDTO son iguales si
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
		RegistroAuditoriaDTO other = (RegistroAuditoriaDTO) obj;
		return Objects.equals(id, other.id)
				&& Objects.equals(username, other.username)
				&& Objects.equals(nombreUsuario, other.nombreUsuario)
				&& Objects.equals(role, other.role)
				&& Objects.equals(fecha, other.fecha)
				&& Objects.equals(accion, other.accion)
				&& Objects.equals(entidad, other.entidad)
				&& Objects.equals(motivo, other.motivo)
				&& Objects.equals(ubicacion, other.ubicacion)
				&& Objects.equals(latitud, other.latitud)
				&& Objects.equals(longitud, other.longitud)
				&& Objects.equals(ip, other.ip);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "RegistroAuditoria ["
				+ "id="
				+ id
				+ ", username="
				+ username
				+ ", nombreUsuario="
				+ nombreUsuario
				+ ", role="
				+ role
				+ ", fecha="
				+ fecha
				+ ", accion="
				+ accion
				+ ", entidad="
				+ entidad
				+ ", motivo="
				+ motivo
				+ ", ubicacion="
				+ ubicacion
				+ ", latitud="
				+ latitud
				+ ", longitud="
				+ longitud
				+ ", ip="
				+ ip
				+ "]";
	}
}
