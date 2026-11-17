package co.edu.unbosque.GreenBuilding.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase que representa un usuario del sistema Green Building (residente o administrador).
 * Implementa UserDetails para integrarse con Spring Security.
 *
 * Esta entidad se almacena en la tabla "usuario" en la base de datos.
 */
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

	/**
	 * Número de versión para la serialización.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador único del usuario.
	 * Se genera automáticamente con estrategia de identidad.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Nombre del usuario.
	 */
	private String nombre;

	/**
	 * Apellido del usuario.
	 */
	private String apellido;

	/**
	 * Correo electrónico del usuario, que también es su nombre de usuario.
	 */
	@Column(unique = true)
	private String username;

	/**
	 * Teléfono de contacto del usuario.
	 */
	private String telefono;

	/**
	 * Contraseña del usuario (almacenada con encriptación).
	 */
	private String password;

	/**
	 * Rol del usuario en el sistema (USER o ADMIN).
	 */
	@Enumerated(EnumType.STRING)
	private Role role;

	/**
	 * Indica si el usuario ya verificó su correo con el código enviado.
	 */
	private boolean verificado;

	/**
	 * Indica si la cuenta está activa. Un administrador puede desactivarla.
	 */
	private boolean activo;

	/**
	 * Indica si la cuenta del usuario no ha expirado.
	 */
	private boolean accountNonExpired;

	/**
	 * Indica si las credenciales del usuario no han expirado.
	 */
	private boolean credentialsNonExpired;

	/**
	 * Identificador del conjunto residencial al que pertenece o en el que se registró el usuario.
	 */
	private Long conjuntoId;

	/**
	 * Identificador de la unidad (apartamento o casa) asignada por el administrador.
	 */
	private Long unidadId;

	/**
	 * Unidad que el usuario escribió al registrarse, para que el administrador se la asigne.
	 */
	private String unidadSolicitada;

	/**
	 * Intereses del usuario separados por coma (por ejemplo "FIESTAS,PISCINA"), usados para las
	 * recomendaciones.
	 */
	private String interesesGuardados;

	/**
	 * Fecha y hora en que se registró el usuario.
	 */
	private LocalDateTime fechaRegistro;

	/**
	 * Constructor por defecto.
	 * Inicializa un usuario con valores predeterminados:
	 * - Cuenta activa y sin verificar
	 * - Cuenta y credenciales no expiradas
	 * - Rol de usuario normal (USER)
	 */
	public Usuario() {
		this.accountNonExpired = true;
		this.credentialsNonExpired = true;
		this.activo = true;
		this.verificado = false;
		this.role = Role.USER;
		this.interesesGuardados = "";
		this.fechaRegistro = LocalDateTime.now();
	}

	/**
	 * Constructor con nombre de usuario y contraseña.
	 *
	 * @param username Correo del usuario
	 * @param password Contraseña del usuario
	 */
	public Usuario(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	/**
	 * Constructor con nombre de usuario, contraseña y rol específico.
	 *
	 * @param username Correo del usuario
	 * @param password Contraseña del usuario
	 * @param rol Rol asignado al usuario
	 */
	public Usuario(String username, String password, Role rol) {
		this();
		this.username = username;
		this.password = password;
		this.role = rol;
	}

	/**
	 * Enumeración que define los roles disponibles en el sistema.
	 *
	 * USER: Residente con permisos básicos.
	 * ADMIN: Administrador con permisos completos.
	 */
	public enum Role {
		/** Residente con permisos básicos */
		USER,
		/** Administrador con permisos completos */
		ADMIN
	}

	/**
	 * Obtiene el nombre completo del usuario (nombre y apellido).
	 *
	 * @return Nombre completo
	 */
	public String getNombreCompleto() {
		return ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
	}

	/**
	 * Obtiene las autoridades (roles) asignadas al usuario.
	 * Implementación del método de la interfaz UserDetails.
	 *
	 * @return Colección de autoridades del usuario con el prefijo "ROLE_"
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	/**
	 * Verifica si la cuenta del usuario no ha expirado.
	 * Implementación del método de la interfaz UserDetails.
	 *
	 * @return true si la cuenta no ha expirado, false en caso contrario
	 */
	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * Verifica si la cuenta del usuario no está bloqueada.
	 * Una cuenta desactivada por el administrador se considera bloqueada.
	 *
	 * @return true si la cuenta está activa, false en caso contrario
	 */
	@Override
	public boolean isAccountNonLocked() {
		return activo;
	}

	/**
	 * Verifica si las credenciales del usuario no han expirado.
	 * Implementación del método de la interfaz UserDetails.
	 *
	 * @return true si las credenciales no han expirado, false en caso contrario
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * Verifica si la cuenta del usuario está habilitada.
	 * Una cuenta sin verificar el correo se considera deshabilitada.
	 *
	 * @return true si la cuenta está verificada, false en caso contrario
	 */
	@Override
	public boolean isEnabled() {
		return verificado;
	}

	/**
	 * Obtiene el identificador único del usuario.
	 *
	 * @return El ID del usuario
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único del usuario.
	 *
	 * @param id El nuevo ID a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre del usuario.
	 *
	 * @return El nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del usuario.
	 *
	 * @param nombre El nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el apellido del usuario.
	 *
	 * @return El apellido
	 */
	public String getApellido() {
		return apellido;
	}

	/**
	 * Establece el apellido del usuario.
	 *
	 * @param apellido El nuevo apellido
	 */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/**
	 * Obtiene el nombre de usuario (correo).
	 * Implementación del método de la interfaz UserDetails.
	 *
	 * @return El nombre de usuario
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el nombre de usuario (correo).
	 *
	 * @param username El nuevo nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el teléfono del usuario.
	 *
	 * @return El teléfono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece el teléfono del usuario.
	 *
	 * @param telefono El nuevo teléfono
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * Obtiene la contraseña del usuario.
	 * Implementación del método de la interfaz UserDetails.
	 *
	 * @return La contraseña del usuario
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * Establece la contraseña del usuario.
	 *
	 * @param password La nueva contraseña
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Obtiene el rol del usuario.
	 *
	 * @return El rol asignado al usuario
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Establece el rol del usuario.
	 *
	 * @param role El nuevo rol a asignar
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Indica si el usuario verificó su correo.
	 *
	 * @return true si está verificado
	 */
	public boolean isVerificado() {
		return verificado;
	}

	/**
	 * Establece si el usuario verificó su correo.
	 *
	 * @param verificado true si está verificado
	 */
	public void setVerificado(boolean verificado) {
		this.verificado = verificado;
	}

	/**
	 * Indica si la cuenta está activa.
	 *
	 * @return true si está activa
	 */
	public boolean isActivo() {
		return activo;
	}

	/**
	 * Establece si la cuenta está activa.
	 *
	 * @param activo true para activarla, false para desactivarla
	 */
	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/**
	 * Establece si la cuenta del usuario ha expirado o no.
	 *
	 * @param accountNonExpired true si la cuenta no ha expirado, false si ha expirado
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * Establece si las credenciales del usuario han expirado o no.
	 *
	 * @param credentialsNonExpired true si las credenciales no han expirado, false si han expirado
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * Obtiene el identificador del conjunto del usuario.
	 *
	 * @return El identificador del conjunto, o null si no tiene
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece el identificador del conjunto del usuario.
	 *
	 * @param conjuntoId El identificador del nuevo conjunto
	 */
	public void setConjuntoId(Long conjuntoId) {
		this.conjuntoId = conjuntoId;
	}

	/**
	 * Obtiene el identificador de la unidad asignada al usuario.
	 *
	 * @return El identificador de la unidad, o null si aún no tiene
	 */
	public Long getUnidadId() {
		return unidadId;
	}

	/**
	 * Establece el identificador de la unidad asignada al usuario.
	 *
	 * @param unidadId El identificador de la nueva unidad
	 */
	public void setUnidadId(Long unidadId) {
		this.unidadId = unidadId;
	}

	/**
	 * Obtiene la unidad que el usuario solicitó al registrarse.
	 *
	 * @return La unidad solicitada
	 */
	public String getUnidadSolicitada() {
		return unidadSolicitada;
	}

	/**
	 * Establece la unidad que el usuario solicitó al registrarse.
	 *
	 * @param unidadSolicitada La unidad solicitada
	 */
	public void setUnidadSolicitada(String unidadSolicitada) {
		this.unidadSolicitada = unidadSolicitada;
	}

	/**
	 * Obtiene los intereses del usuario tal como se guardan, separados por coma.
	 *
	 * @return Los intereses separados por coma
	 */
	public String getInteresesGuardados() {
		return interesesGuardados;
	}

	/**
	 * Establece los intereses del usuario, separados por coma.
	 *
	 * @param interesesGuardados Los nuevos intereses separados por coma
	 */
	public void setInteresesGuardados(String interesesGuardados) {
		this.interesesGuardados = interesesGuardados;
	}

	/**
	 * Convierte los intereses guardados en una lista.
	 *
	 * @return Lista de intereses, vacía si no tiene
	 */
	public List<String> interesesComoLista() {
		List<String> lista = new ArrayList<>();
		if (interesesGuardados == null || interesesGuardados.isBlank()) {
			return lista;
		}
		for (String interes : interesesGuardados.split(",")) {
			lista.add(interes);
		}
		return lista;
	}

	/**
	 * Obtiene la fecha de registro del usuario.
	 *
	 * @return La fecha de registro
	 */
	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * Establece la fecha de registro del usuario.
	 *
	 * @param fechaRegistro La fecha de registro
	 */
	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Calcula el código hash para este usuario.
	 * Se basa en los campos id y username.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, username);
	}

	/**
	 * Compara este usuario con otro objeto para determinar si son iguales.
	 * Dos usuarios son iguales si tienen el mismo id y username.
	 *
	 * @param obj El objeto a comparar con este usuario
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
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id) && Objects.equals(username, other.username);
	}

	/**
	 * Devuelve una representación en cadena de texto de este usuario.
	 * No incluye la contraseña.
	 *
	 * @return Una cadena que representa este usuario
	 */
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", username=" + username
				+ ", role=" + role + ", verificado=" + verificado + ", activo=" + activo + "]";
	}

}
