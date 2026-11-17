package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO para que un usuario cambie su contraseña desde el perfil.
 */
public class CambioPasswordDTO {

	/** Contraseña actual del usuario. */
	private String passwordActual;

	/** Nueva contraseña. */
	private String passwordNueva;

	/** Constructor por defecto. Crea una instancia vacía de CambioPasswordDTO. */
	public CambioPasswordDTO() {}

	/**
	 * Constructor con passwordActual, passwordNueva.
	 *
	 * @param passwordActual Contraseña actual del usuario.
	 * @param passwordNueva Nueva contraseña.
	 */
	public CambioPasswordDTO(String passwordActual, String passwordNueva) {
		this.passwordActual = passwordActual;
		this.passwordNueva = passwordNueva;
	}

	/**
	 * Obtiene: contraseña actual del usuario.
	 *
	 * @return Contraseña actual del usuario
	 */
	public String getPasswordActual() {
		return passwordActual;
	}

	/**
	 * Establece: contraseña actual del usuario.
	 *
	 * @param passwordActual Contraseña actual del usuario
	 */
	public void setPasswordActual(String passwordActual) {
		this.passwordActual = passwordActual;
	}

	/**
	 * Obtiene: nueva contraseña.
	 *
	 * @return Nueva contraseña
	 */
	public String getPasswordNueva() {
		return passwordNueva;
	}

	/**
	 * Establece: nueva contraseña.
	 *
	 * @param passwordNueva Nueva contraseña
	 */
	public void setPasswordNueva(String passwordNueva) {
		this.passwordNueva = passwordNueva;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(passwordActual, passwordNueva);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos CambioPasswordDTO son iguales si
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
		CambioPasswordDTO other = (CambioPasswordDTO) obj;
		return Objects.equals(passwordActual, other.passwordActual)
				&& Objects.equals(passwordNueva, other.passwordNueva);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "CambioPassword ["
				+ "]";
	}
}
