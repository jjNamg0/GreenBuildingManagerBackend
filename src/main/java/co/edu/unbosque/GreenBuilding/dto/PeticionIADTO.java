package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con la petición que se envía al modelo de IA. Los nombres de los campos son los mismos
 * del JSON que espera la API para que Gson lo arme directamente.
 */
public class PeticionIADTO {

	/** Modelo que responde. */
	private String model;

	/** Instrucción, conversación anterior y pregunta. */
	private List<MensajeIADTO> messages;

	/** Qué tan variadas son las respuestas. */
	private double temperature;

	/** Largo máximo de la respuesta. */
	private int max_tokens;

	/** Indica si la respuesta llega por partes. */
	private boolean stream;

	/** Constructor por defecto. Crea una instancia vacía de PeticionIADTO. */
	public PeticionIADTO() {}

	/**
	 * Constructor con model, messages, temperature, max_tokens, stream.
	 *
	 * @param model Modelo que responde.
	 * @param messages Instrucción, conversación anterior y pregunta.
	 * @param temperature Qué tan variadas son las respuestas.
	 * @param max_tokens Largo máximo de la respuesta.
	 * @param stream Indica si la respuesta llega por partes.
	 */
	public PeticionIADTO(String model, List<MensajeIADTO> messages, double temperature, int max_tokens, boolean stream) {
		this.model = model;
		this.messages = messages;
		this.temperature = temperature;
		this.max_tokens = max_tokens;
		this.stream = stream;
	}

	/**
	 * Obtiene: modelo que responde.
	 *
	 * @return Modelo que responde
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Establece: modelo que responde.
	 *
	 * @param model Modelo que responde
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Obtiene: instrucción, conversación anterior y pregunta.
	 *
	 * @return Instrucción, conversación anterior y pregunta
	 */
	public List<MensajeIADTO> getMessages() {
		return messages;
	}

	/**
	 * Establece: instrucción, conversación anterior y pregunta.
	 *
	 * @param messages Instrucción, conversación anterior y pregunta
	 */
	public void setMessages(List<MensajeIADTO> messages) {
		this.messages = messages;
	}

	/**
	 * Obtiene: qué tan variadas son las respuestas.
	 *
	 * @return Qué tan variadas son las respuestas
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * Establece: qué tan variadas son las respuestas.
	 *
	 * @param temperature Qué tan variadas son las respuestas
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	/**
	 * Obtiene: largo máximo de la respuesta.
	 *
	 * @return Largo máximo de la respuesta
	 */
	public int getMax_tokens() {
		return max_tokens;
	}

	/**
	 * Establece: largo máximo de la respuesta.
	 *
	 * @param max_tokens Largo máximo de la respuesta
	 */
	public void setMax_tokens(int max_tokens) {
		this.max_tokens = max_tokens;
	}

	/**
	 * Obtiene: indica si la respuesta llega por partes.
	 *
	 * @return Indica si la respuesta llega por partes
	 */
	public boolean isStream() {
		return stream;
	}

	/**
	 * Establece: indica si la respuesta llega por partes.
	 *
	 * @param stream Indica si la respuesta llega por partes
	 */
	public void setStream(boolean stream) {
		this.stream = stream;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(model, messages, temperature, max_tokens, stream);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos PeticionIADTO son iguales si
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
		PeticionIADTO other = (PeticionIADTO) obj;
		return Objects.equals(model, other.model)
				&& Objects.equals(messages, other.messages)
				&& temperature == other.temperature
				&& max_tokens == other.max_tokens
				&& stream == other.stream;
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "PeticionIA ["
				+ "model="
				+ model
				+ ", messages="
				+ messages
				+ ", temperature="
				+ temperature
				+ ", max_tokens="
				+ max_tokens
				+ ", stream="
				+ stream
				+ "]";
	}
}
