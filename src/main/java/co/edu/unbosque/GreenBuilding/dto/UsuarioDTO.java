package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase de Objeto de Transferencia de Datos (DTO) para usuarios. Utilizada para transferir datos de
 * usuario entre capas de la aplicación: registro, inicio de sesión, perfil y gestión de residentes.
 */
public class UsuarioDTO {

	/** Identificador único del usuario. */
	private Long id;

	/** Nombre del usuario. */
	private String nombre;

	/** Apellido del usuario. */
	private String apellido;

	/** Correo del usuario, usado como nombre de usuario. */
	private String username;

	/** Teléfono de contacto. */
	private String telefono;

	/** Contraseña del usuario. Solo se recibe, nunca se envía en las respuestas. */
	private String password;

	/** Rol del usuario en el sistema (USER o ADMIN). */
	private Role role;

	/** Indica si el usuario verificó su correo. */
	private boolean verificado;

	/** Indica si la cuenta está activa. */
	private boolean activo;

	/** Identificador del conjunto del usuario. */
	private Long conjuntoId;

	/** Nombre del conjunto del usuario. */
	private String conjuntoNombre;

	/** Identificador de la unidad asignada. */
	private Long unidadId;

	/** Descripción de la unidad asignada, por ejemplo "Torre 2 · Apto 301". */
	private String unidadDescripcion;

	/** Unidad que el usuario escribió al registrarse. */
	private String unidadSolicitada;

	/** Intereses del usuario para las recomendaciones. */
	private List<String> intereses;

	/** Constructor por defecto. Crea una instancia vacía de UsuarioDTO. */
	public UsuarioDTO() {}

	/**
	 * Constructor con username, password.
	 *
	 * @param username Correo del usuario, usado como nombre de usuario.
	 * @param password Contraseña del usuario. Solo se recibe, nunca se envía en las respuestas.
	 */
	public UsuarioDTO(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Constructor con username, password, role.
	 *
	 * @param username Correo del usuario, usado como nombre de usuario.
	 * @param password Contraseña del usuario. Solo se recibe, nunca se envía en las respuestas.
	 * @param role Rol del usuario en el sistema (USER o ADMIN).
	 */
	public UsuarioDTO(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	/**
	 * Obtiene: identificador único del usuario.
	 *
	 * @return Identificador único del usuario
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único del usuario.
	 *
	 * @param id Identificador único del usuario
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene: nombre del usuario.
	 *
	 * @return Nombre del usuario
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece: nombre del usuario.
	 *
	 * @param nombre Nombre del usuario
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene: apellido del usuario.
	 *
	 * @return Apellido del usuario
	 */
	public String getApellido() {
		return apellido;
	}

	/**
	 * Establece: apellido del usuario.
	 *
	 * @param apellido Apellido del usuario
	 */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/**
	 * Obtiene: correo del usuario, usado como nombre de usuario.
	 *
	 * @return Correo del usuario, usado como nombre de usuario
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece: correo del usuario, usado como nombre de usuario.
	 *
	 * @param username Correo del usuario, usado como nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene: teléfono de contacto.
	 *
	 * @return Teléfono de contacto
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece: teléfono de contacto.
	 *
	 * @param telefono Teléfono de contacto
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * Obtiene: contraseña del usuario. Solo se recibe, nunca se envía en las respuestas.
	 *
	 * @return Contraseña del usuario. Solo se recibe, nunca se envía en las respuestas
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Establece: contraseña del usuario. Solo se recibe, nunca se envía en las respuestas.
	 *
	 * @param password Contraseña del usuario. Solo se recibe, nunca se envía en las respuestas
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Obtiene: rol del usuario en el sistema (USER o ADMIN).
	 *
	 * @return Rol del usuario en el sistema (USER o ADMIN)
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Establece: rol del usuario en el sistema (USER o ADMIN).
	 *
	 * @param role Rol del usuario en el sistema (USER o ADMIN)
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Obtiene: indica si el usuario verificó su correo.
	 *
	 * @return Indica si el usuario verificó su correo
	 */
	public boolean isVerificado() {
		return verificado;
	}

	/**
	 * Establece: indica si el usuario verificó su correo.
	 *
	 * @param verificado Indica si el usuario verificó su correo
	 */
	public void setVerificado(boolean verificado) {
		this.verificado = verificado;
	}

	/**
	 * Obtiene: indica si la cuenta está activa.
	 *
	 * @return Indica si la cuenta está activa
	 */
	public boolean isActivo() {
		return activo;
	}

	/**
	 * Establece: indica si la cuenta está activa.
	 *
	 * @param activo Indica si la cuenta está activa
	 */
	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/**
	 * Obtiene: identificador del conjunto del usuario.
	 *
	 * @return Identificador del conjunto del usuario
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece: identificador del conjunto del usuario.
	 *
	 * @param conjuntoId Identificador del conjunto del usuario
	 */
	public void setConjuntoId(Long conjuntoId) {
		this.conjuntoId = conjuntoId;
	}

	/**
	 * Obtiene: nombre del conjunto del usuario.
	 *
	 * @return Nombre del conjunto del usuario
	 */
	public String getConjuntoNombre() {
		return conjuntoNombre;
	}

	/**
	 * Establece: nombre del conjunto del usuario.
	 *
	 * @param conjuntoNombre Nombre del conjunto del usuario
	 */
	public void setConjuntoNombre(String conjuntoNombre) {
		this.conjuntoNombre = conjuntoNombre;
	}

	/**
	 * Obtiene: identificador de la unidad asignada.
	 *
	 * @return Identificador de la unidad asignada
	 */
	public Long getUnidadId() {
		return unidadId;
	}

	/**
	 * Establece: identificador de la unidad asignada.
	 *
	 * @param unidadId Identificador de la unidad asignada
	 */
	public void setUnidadId(Long unidadId) {
		this.unidadId = unidadId;
	}

	/**
	 * Obtiene: descripción de la unidad asignada, por ejemplo "Torre 2 · Apto 301".
	 *
	 * @return Descripción de la unidad asignada, por ejemplo "Torre 2 · Apto 301"
	 */
	public String getUnidadDescripcion() {
		return unidadDescripcion;
	}

	/**
	 * Establece: descripción de la unidad asignada, por ejemplo "Torre 2 · Apto 301".
	 *
	 * @param unidadDescripcion Descripción de la unidad asignada, por ejemplo "Torre 2 · Apto 301"
	 */
	public void setUnidadDescripcion(String unidadDescripcion) {
		this.unidadDescripcion = unidadDescripcion;
	}

	/**
	 * Obtiene: unidad que el usuario escribió al registrarse.
	 *
	 * @return Unidad que el usuario escribió al registrarse
	 */
	public String getUnidadSolicitada() {
		return unidadSolicitada;
	}

	/**
	 * Establece: unidad que el usuario escribió al registrarse.
	 *
	 * @param unidadSolicitada Unidad que el usuario escribió al registrarse
	 */
	public void setUnidadSolicitada(String unidadSolicitada) {
		this.unidadSolicitada = unidadSolicitada;
	}

	/**
	 * Obtiene: intereses del usuario para las recomendaciones.
	 *
	 * @return Intereses del usuario para las recomendaciones
	 */
	public List<String> getIntereses() {
		return intereses;
	}

	/**
	 * Establece: intereses del usuario para las recomendaciones.
	 *
	 * @param intereses Intereses del usuario para las recomendaciones
	 */
	public void setIntereses(List<String> intereses) {
		if (intereses == null) {
			this.intereses = null;
		} else {
			this.intereses = new ArrayList<>(intereses);
		}
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, nombre, apellido, username, telefono, password, role, verificado, activo, conjuntoId, conjuntoNombre, unidadId, unidadDescripcion, unidadSolicitada, intereses);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos UsuarioDTO son iguales si
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
		UsuarioDTO other = (UsuarioDTO) obj;
		return mismosDatosPrincipales(other) && mismosDatosAdicionales(other);
	}

	/**
	 * Compara la primera mitad de los atributos de este objeto con los de otro.
	 *
	 * @param other Objeto a comparar
	 * @return true si esos atributos son iguales, false en caso contrario
	 */
	private boolean mismosDatosPrincipales(UsuarioDTO other) {
		return Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre)
				&& Objects.equals(apellido, other.apellido)
				&& Objects.equals(username, other.username)
				&& Objects.equals(telefono, other.telefono)
				&& Objects.equals(password, other.password)
				&& role == other.role
				&& verificado == other.verificado;
	}

	/**
	 * Compara la segunda mitad de los atributos de este objeto con los de otro.
	 *
	 * @param other Objeto a comparar
	 * @return true si esos atributos son iguales, false en caso contrario
	 */
	private boolean mismosDatosAdicionales(UsuarioDTO other) {
		return activo == other.activo
				&& Objects.equals(conjuntoId, other.conjuntoId)
				&& Objects.equals(conjuntoNombre, other.conjuntoNombre)
				&& Objects.equals(unidadId, other.unidadId)
				&& Objects.equals(unidadDescripcion, other.unidadDescripcion)
				&& Objects.equals(unidadSolicitada, other.unidadSolicitada)
				&& Objects.equals(intereses, other.intereses);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Usuario ["
				+ "id="
				+ id
				+ ", nombre="
				+ nombre
				+ ", apellido="
				+ apellido
				+ ", username="
				+ username
				+ ", telefono="
				+ telefono
				+ ", role="
				+ role
				+ ", verificado="
				+ verificado
				+ ", activo="
				+ activo
				+ ", conjuntoId="
				+ conjuntoId
				+ ", conjuntoNombre="
				+ conjuntoNombre
				+ ", unidadId="
				+ unidadId
				+ ", unidadDescripcion="
				+ unidadDescripcion
				+ ", unidadSolicitada="
				+ unidadSolicitada
				+ ", intereses="
				+ intereses
				+ "]";
	}
}
