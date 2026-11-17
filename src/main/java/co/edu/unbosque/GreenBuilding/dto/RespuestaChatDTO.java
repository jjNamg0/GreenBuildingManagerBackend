package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/** DTO con la respuesta del asistente del chat de ayuda. */
public class RespuestaChatDTO {

	/** Texto de la respuesta del asistente. */
	private String respuesta;

	/** Constructor por defecto. Crea una instancia vacía de RespuestaChatDTO. */
	public RespuestaChatDTO() {}

	/**
	 * Constructor con respuesta.
	 *
	 * @param respuesta Texto de la respuesta del asistente.
	 */
	public RespuestaChatDTO(String respuesta) {
		this.respuesta = respuesta;
	}

	/**
	 * Obtiene: texto de la respuesta del asistente.
	 *
	 * @return Texto de la respuesta del asistente
	 */
	public String getRespuesta() {
		return respuesta;
	}

	/**
	 * Establece: texto de la respuesta del asistente.
	 *
	 * @param respuesta Texto de la respuesta del asistente
	 */
	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	/**
	 * Genera un código hash para este objeto basado en todos sus atributos.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(respuesta);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RespuestaChatDTO son
	 * iguales si todos sus atributos son iguales.
	 *
	 * @param obj Objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RespuestaChatDTO other = (RespuestaChatDTO) obj;
		return Objects.equals(respuesta, other.respuesta);
	}

	/**
	 * Devuelve una representación en texto del objeto.
	 *
	 * @return Cadena con la respuesta
	 */
	@Override
	public String toString() {
		return "RespuestaChatDTO [respuesta=" + respuesta + "]";
	}
}
