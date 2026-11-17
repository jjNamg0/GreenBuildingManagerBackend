package co.edu.unbosque.GreenBuilding.model;

import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase que representa un espacio común de un conjunto (salón social, piscina, BBQ, etc.).
 *
 * Esta entidad se almacena en la tabla "espacio" en la base de datos.
 */
@Entity
@Table(name = "espacio")
public class Espacio {

	/**
	 * Identificador único del espacio.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Identificador del conjunto al que pertenece el espacio.
	 */
	@Column(nullable = false)
	private Long conjuntoId;

	/**
	 * Nombre del espacio.
	 */
	private String nombre;

	/**
	 * Tipo de espacio.
	 */
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	/**
	 * Descripción del espacio.
	 */
	@Column(length = 2000)
	private String descripcion;

	/**
	 * Capacidad máxima de personas.
	 */
	private int capacidad;

	/**
	 * Hora en que abre el espacio.
	 */
	private LocalTime horaApertura;

	/**
	 * Hora en que cierra el espacio.
	 */
	private LocalTime horaCierre;

	/**
	 * Indica si el espacio necesita reserva o es de uso libre.
	 */
	private boolean requiereReserva;

	/**
	 * Indica si el espacio está disponible (no está en mantenimiento).
	 */
	private boolean disponible;

	/**
	 * URL de la imagen del espacio.
	 */
	@Column(length = 1000)
	private String imagenUrl;

	/**
	 * Constructor por defecto.
	 */
	public Espacio() {
		this.tipo = Tipo.OTRO;
		this.disponible = true;
		this.descripcion = "";
	}

	/**
	 * Constructor con los datos principales del espacio.
	 *
	 * @param conjuntoId Identificador del conjunto al que pertenece
	 * @param nombre Nombre del espacio
	 * @param tipo Tipo de espacio
	 * @param capacidad Capacidad máxima
	 * @param horaApertura Hora de apertura
	 * @param horaCierre Hora de cierre
	 * @param requiereReserva true si necesita reserva
	 */
	public Espacio(Long conjuntoId, String nombre, Tipo tipo, int capacidad, LocalTime horaApertura,
			LocalTime horaCierre, boolean requiereReserva) {
		this();
		this.conjuntoId = conjuntoId;
		this.nombre = nombre;
		this.tipo = tipo;
		this.capacidad = capacidad;
		this.horaApertura = horaApertura;
		this.horaCierre = horaCierre;
		this.requiereReserva = requiereReserva;
	}

	/**
	 * Enumeración de los tipos de espacio común.
	 */
	public enum Tipo {
		/** Salón social */
		SALON_SOCIAL,
		/** Parque */
		PARQUE,
		/** Piscina */
		PISCINA,
		/** Gimnasio */
		GIMNASIO,
		/** Zona de asados */
		BBQ,
		/** Cancha deportiva */
		CANCHA,
		/** Zona infantil */
		ZONA_INFANTIL,
		/** Zona húmeda (sauna, turco, jacuzzi) */
		ZONA_HUMEDA,
		/** Otro tipo de espacio */
		OTRO
	}

	/**
	 * Obtiene el identificador del espacio.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del espacio.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del conjunto del espacio.
	 *
	 * @return El identificador del conjunto
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece el identificador del conjunto del espacio.
	 *
	 * @param conjuntoId El identificador del nuevo conjunto
	 */
	public void setConjuntoId(Long conjuntoId) {
		this.conjuntoId = conjuntoId;
	}

	/**
	 * Obtiene el nombre del espacio.
	 *
	 * @return El nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del espacio.
	 *
	 * @param nombre El nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el tipo de espacio.
	 *
	 * @return El tipo
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece el tipo de espacio.
	 *
	 * @param tipo El nuevo tipo
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene la descripción del espacio.
	 *
	 * @return La descripción
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece la descripción del espacio.
	 *
	 * @param descripcion La nueva descripción
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene la capacidad del espacio.
	 *
	 * @return La capacidad
	 */
	public int getCapacidad() {
		return capacidad;
	}

	/**
	 * Establece la capacidad del espacio.
	 *
	 * @param capacidad La nueva capacidad
	 */
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	/**
	 * Obtiene la hora de apertura.
	 *
	 * @return La hora de apertura
	 */
	public LocalTime getHoraApertura() {
		return horaApertura;
	}

	/**
	 * Establece la hora de apertura.
	 *
	 * @param horaApertura La nueva hora de apertura
	 */
	public void setHoraApertura(LocalTime horaApertura) {
		this.horaApertura = horaApertura;
	}

	/**
	 * Obtiene la hora de cierre.
	 *
	 * @return La hora de cierre
	 */
	public LocalTime getHoraCierre() {
		return horaCierre;
	}

	/**
	 * Establece la hora de cierre.
	 *
	 * @param horaCierre La nueva hora de cierre
	 */
	public void setHoraCierre(LocalTime horaCierre) {
		this.horaCierre = horaCierre;
	}

	/**
	 * Indica si el espacio necesita reserva.
	 *
	 * @return true si necesita reserva
	 */
	public boolean isRequiereReserva() {
		return requiereReserva;
	}

	/**
	 * Establece si el espacio necesita reserva.
	 *
	 * @param requiereReserva true si necesita reserva
	 */
	public void setRequiereReserva(boolean requiereReserva) {
		this.requiereReserva = requiereReserva;
	}

	/**
	 * Indica si el espacio está disponible.
	 *
	 * @return true si está disponible
	 */
	public boolean isDisponible() {
		return disponible;
	}

	/**
	 * Establece si el espacio está disponible.
	 *
	 * @param disponible true si está disponible
	 */
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	/**
	 * Obtiene la URL de la imagen del espacio.
	 *
	 * @return La URL de la imagen
	 */
	public String getImagenUrl() {
		return imagenUrl;
	}

	/**
	 * Establece la URL de la imagen del espacio.
	 *
	 * @param imagenUrl La nueva URL
	 */
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	/**
	 * Calcula el código hash para este espacio.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, nombre);
	}

	/**
	 * Compara este espacio con otro objeto para determinar si son iguales.
	 *
	 * @param obj El objeto a comparar
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
		Espacio other = (Espacio) obj;
		return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
	}

	/**
	 * Devuelve una representación en cadena de texto de este espacio.
	 *
	 * @return Una cadena que representa este espacio
	 */
	@Override
	public String toString() {
		return "Espacio [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", capacidad=" + capacidad + "]";
	}

}
