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
 * Clase que representa un registro de auditoría: quién hizo qué, cuándo y desde dónde.
 *
 * Esta entidad se almacena en la tabla "registro_auditoria" en la base de datos.
 */
@Entity
@Table(name = "registro_auditoria")
public class RegistroAuditoria {

	/**
	 * Identificador único del registro.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Correo del usuario que hizo la acción.
	 */
	private String username;

	/**
	 * Nombre completo del usuario que hizo la acción.
	 */
	private String nombreUsuario;

	/**
	 * Rol del usuario que hizo la acción.
	 */
	private String role;

	/**
	 * Fecha y hora de la acción.
	 */
	private LocalDateTime fecha;

	/**
	 * Acción realizada (LOGIN, REGISTRO, CREAR, RESERVA, etc.).
	 */
	private String accion;

	/**
	 * Entidad afectada (Usuario, Conjunto, Reserva, etc.).
	 */
	private String entidad;

	/**
	 * Descripción de lo que se hizo.
	 */
	@Column(length = 1000)
	private String motivo;

	/**
	 * Latitud desde donde se hizo la acción, si el navegador la envió.
	 */
	private Double latitud;

	/**
	 * Longitud desde donde se hizo la acción, si el navegador la envió.
	 */
	private Double longitud;

	/**
	 * Dirección IP desde donde se hizo la acción.
	 */
	private String ip;

	/**
	 * Constructor por defecto. Toma la fecha y hora actuales.
	 */
	public RegistroAuditoria() {
		this.fecha = LocalDateTime.now();
	}

	/**
	 * Constructor con los datos de la acción.
	 *
	 * @param username Correo del usuario
	 * @param nombreUsuario Nombre del usuario
	 * @param role Rol del usuario
	 * @param accion Acción realizada
	 * @param entidad Entidad afectada
	 * @param motivo Descripción
	 */
	public RegistroAuditoria(String username, String nombreUsuario, String role, String accion, String entidad,
			String motivo) {
		this();
		this.username = username;
		this.nombreUsuario = nombreUsuario;
		this.role = role;
		this.accion = accion;
		this.entidad = entidad;
		this.motivo = motivo;
	}

	/**
	 * Obtiene el identificador del registro.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del registro.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el correo del usuario.
	 *
	 * @return El correo
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el correo del usuario.
	 *
	 * @param username El correo
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el nombre del usuario.
	 *
	 * @return El nombre
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	/**
	 * Establece el nombre del usuario.
	 *
	 * @param nombreUsuario El nombre
	 */
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	/**
	 * Obtiene el rol del usuario.
	 *
	 * @return El rol
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Establece el rol del usuario.
	 *
	 * @param role El rol
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Obtiene la fecha de la acción.
	 *
	 * @return La fecha
	 */
	public LocalDateTime getFecha() {
		return fecha;
	}

	/**
	 * Establece la fecha de la acción.
	 *
	 * @param fecha La fecha
	 */
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene la acción realizada.
	 *
	 * @return La acción
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * Establece la acción realizada.
	 *
	 * @param accion La acción
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * Obtiene la entidad afectada.
	 *
	 * @return La entidad
	 */
	public String getEntidad() {
		return entidad;
	}

	/**
	 * Establece la entidad afectada.
	 *
	 * @param entidad La entidad
	 */
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	/**
	 * Obtiene la descripción de la acción.
	 *
	 * @return El motivo
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Establece la descripción de la acción.
	 *
	 * @param motivo El motivo
	 */
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	/**
	 * Obtiene la latitud.
	 *
	 * @return La latitud, o null
	 */
	public Double getLatitud() {
		return latitud;
	}

	/**
	 * Establece la latitud.
	 *
	 * @param latitud La latitud
	 */
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	/**
	 * Obtiene la longitud.
	 *
	 * @return La longitud, o null
	 */
	public Double getLongitud() {
		return longitud;
	}

	/**
	 * Establece la longitud.
	 *
	 * @param longitud La longitud
	 */
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	/**
	 * Obtiene la dirección IP.
	 *
	 * @return La IP
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Establece la dirección IP.
	 *
	 * @param ip La IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Calcula el código hash para este registro.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, username, fecha, accion);
	}

	/**
	 * Compara este registro con otro objeto para determinar si son iguales.
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
		RegistroAuditoria other = (RegistroAuditoria) obj;
		return Objects.equals(id, other.id) && Objects.equals(username, other.username)
				&& Objects.equals(fecha, other.fecha) && Objects.equals(accion, other.accion);
	}

	/**
	 * Devuelve una representación en cadena de texto de este registro.
	 *
	 * @return Una cadena que representa este registro
	 */
	@Override
	public String toString() {
		return "RegistroAuditoria [id=" + id + ", username=" + username + ", fecha=" + fecha + ", accion=" + accion
				+ "]";
	}

}
