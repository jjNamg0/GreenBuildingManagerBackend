package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Espacio.Tipo;
import java.util.Objects;

/**
 * DTO para espacios comunes. Las horas viajan como texto con formato HH:mm.
 */
public class EspacioDTO {

	/** Identificador único del espacio. */
	private Long id;

	/** Identificador del conjunto. */
	private Long conjuntoId;

	/** Nombre del espacio. */
	private String nombre;

	/** Tipo de espacio. */
	private Tipo tipo;

	/** Descripción del espacio. */
	private String descripcion;

	/** Capacidad máxima de personas. */
	private Integer capacidad;

	/** Hora de apertura (HH:mm). */
	private String horaApertura;

	/** Hora de cierre (HH:mm). */
	private String horaCierre;

	/** Indica si el espacio necesita reserva. */
	private boolean requiereReserva;

	/** Indica si el espacio está disponible. */
	private boolean disponible;

	/** URL de la imagen del espacio. */
	private String imagenUrl;

	/** Constructor por defecto. Crea una instancia vacía de EspacioDTO. */
	public EspacioDTO() {}

	/**
	 * Constructor con conjuntoId, nombre, tipo, capacidad, horaApertura, horaCierre.
	 *
	 * @param conjuntoId Identificador del conjunto.
	 * @param nombre Nombre del espacio.
	 * @param tipo Tipo de espacio.
	 * @param capacidad Capacidad máxima de personas.
	 * @param horaApertura Hora de apertura (HH:mm).
	 * @param horaCierre Hora de cierre (HH:mm).
	 */
	public EspacioDTO(Long conjuntoId, String nombre, Tipo tipo, Integer capacidad, String horaApertura, String horaCierre) {
		this.conjuntoId = conjuntoId;
		this.nombre = nombre;
		this.tipo = tipo;
		this.capacidad = capacidad;
		this.horaApertura = horaApertura;
		this.horaCierre = horaCierre;
	}

	/**
	 * Obtiene: identificador único del espacio.
	 *
	 * @return Identificador único del espacio
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único del espacio.
	 *
	 * @param id Identificador único del espacio
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene: identificador del conjunto.
	 *
	 * @return Identificador del conjunto
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece: identificador del conjunto.
	 *
	 * @param conjuntoId Identificador del conjunto
	 */
	public void setConjuntoId(Long conjuntoId) {
		this.conjuntoId = conjuntoId;
	}

	/**
	 * Obtiene: nombre del espacio.
	 *
	 * @return Nombre del espacio
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece: nombre del espacio.
	 *
	 * @param nombre Nombre del espacio
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene: tipo de espacio.
	 *
	 * @return Tipo de espacio
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece: tipo de espacio.
	 *
	 * @param tipo Tipo de espacio
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene: descripción del espacio.
	 *
	 * @return Descripción del espacio
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece: descripción del espacio.
	 *
	 * @param descripcion Descripción del espacio
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene: capacidad máxima de personas.
	 *
	 * @return Capacidad máxima de personas
	 */
	public Integer getCapacidad() {
		return capacidad;
	}

	/**
	 * Establece: capacidad máxima de personas.
	 *
	 * @param capacidad Capacidad máxima de personas
	 */
	public void setCapacidad(Integer capacidad) {
		this.capacidad = capacidad;
	}

	/**
	 * Obtiene: hora de apertura (HH:mm).
	 *
	 * @return Hora de apertura (HH:mm)
	 */
	public String getHoraApertura() {
		return horaApertura;
	}

	/**
	 * Establece: hora de apertura (HH:mm).
	 *
	 * @param horaApertura Hora de apertura (HH:mm)
	 */
	public void setHoraApertura(String horaApertura) {
		this.horaApertura = horaApertura;
	}

	/**
	 * Obtiene: hora de cierre (HH:mm).
	 *
	 * @return Hora de cierre (HH:mm)
	 */
	public String getHoraCierre() {
		return horaCierre;
	}

	/**
	 * Establece: hora de cierre (HH:mm).
	 *
	 * @param horaCierre Hora de cierre (HH:mm)
	 */
	public void setHoraCierre(String horaCierre) {
		this.horaCierre = horaCierre;
	}

	/**
	 * Obtiene: indica si el espacio necesita reserva.
	 *
	 * @return Indica si el espacio necesita reserva
	 */
	public boolean isRequiereReserva() {
		return requiereReserva;
	}

	/**
	 * Establece: indica si el espacio necesita reserva.
	 *
	 * @param requiereReserva Indica si el espacio necesita reserva
	 */
	public void setRequiereReserva(boolean requiereReserva) {
		this.requiereReserva = requiereReserva;
	}

	/**
	 * Obtiene: indica si el espacio está disponible.
	 *
	 * @return Indica si el espacio está disponible
	 */
	public boolean isDisponible() {
		return disponible;
	}

	/**
	 * Establece: indica si el espacio está disponible.
	 *
	 * @param disponible Indica si el espacio está disponible
	 */
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	/**
	 * Obtiene: uRL de la imagen del espacio.
	 *
	 * @return URL de la imagen del espacio
	 */
	public String getImagenUrl() {
		return imagenUrl;
	}

	/**
	 * Establece: uRL de la imagen del espacio.
	 *
	 * @param imagenUrl URL de la imagen del espacio
	 */
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, conjuntoId, nombre, tipo, descripcion, capacidad, horaApertura, horaCierre, requiereReserva, disponible, imagenUrl);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos EspacioDTO son iguales si
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
		EspacioDTO other = (EspacioDTO) obj;
		return Objects.equals(id, other.id)
				&& Objects.equals(conjuntoId, other.conjuntoId)
				&& Objects.equals(nombre, other.nombre)
				&& tipo == other.tipo
				&& Objects.equals(descripcion, other.descripcion)
				&& Objects.equals(capacidad, other.capacidad)
				&& Objects.equals(horaApertura, other.horaApertura)
				&& Objects.equals(horaCierre, other.horaCierre)
				&& requiereReserva == other.requiereReserva
				&& disponible == other.disponible
				&& Objects.equals(imagenUrl, other.imagenUrl);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Espacio ["
				+ "id="
				+ id
				+ ", conjuntoId="
				+ conjuntoId
				+ ", nombre="
				+ nombre
				+ ", tipo="
				+ tipo
				+ ", descripcion="
				+ descripcion
				+ ", capacidad="
				+ capacidad
				+ ", horaApertura="
				+ horaApertura
				+ ", horaCierre="
				+ horaCierre
				+ ", requiereReserva="
				+ requiereReserva
				+ ", disponible="
				+ disponible
				+ ", imagenUrl="
				+ imagenUrl
				+ "]";
	}
}
