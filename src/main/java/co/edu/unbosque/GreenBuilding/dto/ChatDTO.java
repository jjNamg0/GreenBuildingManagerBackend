package co.edu.unbosque.GreenBuilding.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** DTO con lo que el front envía al chat de ayuda: la pregunta y la conversación anterior. */
public class ChatDTO {

	/** Pregunta nueva del usuario. */
	private String mensaje;

	/** Mensajes anteriores de la conversación, del más viejo al más nuevo. */
	private List<MensajeChatDTO> historial = new ArrayList<>();

	/** Constructor por defecto. Crea una instancia vacía de ChatDTO. */
	public ChatDTO() {}

	/**
	 * Constructor con mensaje e historial.
	 *
	 * @param mensaje Pregunta nueva del usuario.
	 * @param historial Mensajes anteriores de la conversación.
	 */
	public ChatDTO(String mensaje, List<MensajeChatDTO> historial) {
		this.mensaje = mensaje;
		if (historial == null) {
			this.historial = null;
		} else {
			this.historial = new ArrayList<>(historial);
		}
	}

	/**
	 * Obtiene: pregunta nueva del usuario.
	 *
	 * @return Pregunta nueva del usuario
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Establece: pregunta nueva del usuario.
	 *
	 * @param mensaje Pregunta nueva del usuario
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	/**
	 * Obtiene: mensajes anteriores de la conversación.
	 *
	 * @return Mensajes anteriores de la conversación
	 */
	public List<MensajeChatDTO> getHistorial() {
		return historial;
	}

	/**
	 * Establece: mensajes anteriores de la conversación.
	 *
	 * @param historial Mensajes anteriores de la conversación
	 */
	public void setHistorial(List<MensajeChatDTO> historial) {
		if (historial == null) {
			this.historial = null;
		} else {
			this.historial = new ArrayList<>(historial);
		}
	}

	/**
	 * Genera un código hash para este objeto basado en todos sus atributos.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(mensaje, historial);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos ChatDTO son iguales si todos
	 * sus atributos son iguales.
	 *
	 * @param obj Objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ChatDTO other = (ChatDTO) obj;
		return Objects.equals(mensaje, other.mensaje) && Objects.equals(historial, other.historial);
	}

	/**
	 * Devuelve una representación en texto del objeto.
	 *
	 * @return Cadena con el mensaje y el historial
	 */
	@Override
	public String toString() {
		return "ChatDTO [mensaje=" + mensaje + ", historial=" + historial + "]";
	}
}
