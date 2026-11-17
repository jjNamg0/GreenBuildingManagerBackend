package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con un mensaje en el formato del modelo de IA (API compatible con OpenAI).
 */
public class MensajeIADTO {

	/** Quién escribe el mensaje: system, user o assistant. */
	private String role;

	/** Texto del mensaje. */
	private String content;

	/** Constructor por defecto. Crea una instancia vacía de MensajeIADTO. */
	public MensajeIADTO() {}

	/**
	 * Constructor con role, content.
	 *
	 * @param role Quién escribe el mensaje: system, user o assistant.
	 * @param content Texto del mensaje.
	 */
	public MensajeIADTO(String role, String content) {
		this.role = role;
		this.content = content;
	}

	/**
	 * Obtiene: quién escribe el mensaje: system, user o assistant.
	 *
	 * @return Quién escribe el mensaje: system, user o assistant
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Establece: quién escribe el mensaje: system, user o assistant.
	 *
	 * @param role Quién escribe el mensaje: system, user o assistant
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Obtiene: texto del mensaje.
	 *
	 * @return Texto del mensaje
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Establece: texto del mensaje.
	 *
	 * @param content Texto del mensaje
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(role, content);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos MensajeIADTO son iguales si
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
		MensajeIADTO other = (MensajeIADTO) obj;
		return Objects.equals(role, other.role)
				&& Objects.equals(content, other.content);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "MensajeIA ["
				+ "role="
				+ role
				+ ", content="
				+ content
				+ "]";
	}
}
