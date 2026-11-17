package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO para las operaciones con el código enviado al correo: verificar la cuenta, reenviar el
 * código, pedir y restablecer la contraseña.
 */
public class CodigoDTO {

	/** Correo del usuario. */
	private String username;

	/** Código de 6 dígitos enviado al correo. */
	private String codigo;

	/** Nueva contraseña (solo para restablecer). */
	private String nuevaPassword;

	/** Constructor por defecto. Crea una instancia vacía de CodigoDTO. */
	public CodigoDTO() {}

	/**
	 * Constructor con username, codigo.
	 *
	 * @param username Correo del usuario.
	 * @param codigo Código de 6 dígitos enviado al correo.
	 */
	public CodigoDTO(String username, String codigo) {
		this.username = username;
		this.codigo = codigo;
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
	 * Obtiene: código de 6 dígitos enviado al correo.
	 *
	 * @return Código de 6 dígitos enviado al correo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Establece: código de 6 dígitos enviado al correo.
	 *
	 * @param codigo Código de 6 dígitos enviado al correo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Obtiene: nueva contraseña (solo para restablecer).
	 *
	 * @return Nueva contraseña (solo para restablecer)
	 */
	public String getNuevaPassword() {
		return nuevaPassword;
	}

	/**
	 * Establece: nueva contraseña (solo para restablecer).
	 *
	 * @param nuevaPassword Nueva contraseña (solo para restablecer)
	 */
	public void setNuevaPassword(String nuevaPassword) {
		this.nuevaPassword = nuevaPassword;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(username, codigo, nuevaPassword);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos CodigoDTO son iguales si
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
		CodigoDTO other = (CodigoDTO) obj;
		return Objects.equals(username, other.username)
				&& Objects.equals(codigo, other.codigo)
				&& Objects.equals(nuevaPassword, other.nuevaPassword);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Codigo ["
				+ "username="
				+ username
				+ "]";
	}
}
