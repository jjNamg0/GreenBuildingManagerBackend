package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/** DTO con un mensaje del chat de ayuda: quién lo dijo y qué dijo. */
public class MensajeChatDTO {

	/** Quién escribió el mensaje: "user" (el residente) o "assistant" (el asistente). */
	private String rol;

	/** Texto del mensaje. */
	private String contenido;

	/** Constructor por defecto. Crea una instancia vacía de MensajeChatDTO. */
	public MensajeChatDTO() {}

	/**
	 * Constructor con rol y contenido.
	 *
	 * @param rol Quién escribió el mensaje.
	 * @param contenido Texto del mensaje.
	 */
	public MensajeChatDTO(String rol, String contenido) {
		this.rol = rol;
		this.contenido = contenido;
	}

	/**
	 * Obtiene: quién escribió el mensaje.
	 *
	 * @return Quién escribió el mensaje
	 */
	public String getRol() {
		return rol;
	}

	/**
	 * Establece: quién escribió el mensaje.
	 *
	 * @param rol Quién escribió el mensaje
	 */
	public void setRol(String rol) {
		this.rol = rol;
	}

	/**
	 * Obtiene: texto del mensaje.
	 *
	 * @return Texto del mensaje
	 */
	public String getContenido() {
		return contenido;
	}

	/**
	 * Establece: texto del mensaje.
	 *
	 * @param contenido Texto del mensaje
	 */
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	/**
	 * Genera un código hash para este objeto basado en todos sus atributos.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(rol, contenido);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos MensajeChatDTO son iguales
	 * si todos sus atributos son iguales.
	 *
	 * @param obj Objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MensajeChatDTO other = (MensajeChatDTO) obj;
		return Objects.equals(rol, other.rol) && Objects.equals(contenido, other.contenido);
	}

	/**
	 * Devuelve una representación en texto del objeto.
	 *
	 * @return Cadena con el rol y el tamaño del contenido
	 */
	@Override
	public String toString() {
		return "MensajeChatDTO [rol=" + rol + ", contenido=" + contenido + "]";
	}
}
