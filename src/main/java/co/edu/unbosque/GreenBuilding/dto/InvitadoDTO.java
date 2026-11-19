package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con un correo invitado y la fecha en que se le envió la invitación.
 */
public class InvitadoDTO {

	/** Correo del invitado. */
	private String correo;

	/** Fecha y hora del último envío. */
	private String fechaEnvio;

	/** Constructor por defecto. Crea una instancia vacía de InvitadoDTO. */
	public InvitadoDTO() {
	}

	/**
	 * Constructor con correo, fechaEnvio.
	 *
	 * @param correo     Correo del invitado.
	 * @param fechaEnvio Fecha y hora del último envío.
	 */
	public InvitadoDTO(String correo, String fechaEnvio) {
		this.correo = correo;
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Obtiene: correo del invitado.
	 *
	 * @return Correo del invitado
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * Establece: correo del invitado.
	 *
	 * @param correo Correo del invitado
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * Obtiene: fecha y hora del último envío.
	 *
	 * @return Fecha y hora del último envío
	 */
	public String getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * Establece: fecha y hora del último envío.
	 *
	 * @param fechaEnvio Fecha y hora del último envío
	 */
	public void setFechaEnvio(String fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en
	 * colecciones basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(correo, fechaEnvio);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos
	 * InvitadoDTO son iguales si todos sus atributos son iguales.
	 *
	 * @param obj Objeto a comparar
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
		InvitadoDTO other = (InvitadoDTO) obj;
		return Objects.equals(correo, other.correo) && Objects.equals(fechaEnvio, other.fechaEnvio);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para
	 * depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Invitado [" + "correo=" + correo + ", fechaEnvio=" + fechaEnvio + "]";
	}
}
