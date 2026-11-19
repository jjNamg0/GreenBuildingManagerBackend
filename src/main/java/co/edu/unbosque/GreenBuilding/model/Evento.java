package co.edu.unbosque.GreenBuilding.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * Clase que representa un evento de un conjunto (fiesta, asamblea, plan infantil, etc.).
 *
 * Esta entidad se almacena en la tabla "evento" en la base de datos.
 */
@Entity
@Table(name = "evento")
public class Evento {

	/**
	 * Identificador único del evento.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Identificador del conjunto donde se realiza el evento.
	 */
	@Column(nullable = false)
	private Long conjuntoId;

	/**
	 * Identificador del espacio donde se realiza el evento (opcional).
	 */
	private Long espacioId;

	/**
	 * Título del evento.
	 */
	private String titulo;

	/**
	 * Descripción del evento.
	 */
	@Column(length = 2000)
	private String descripcion;

	/**
	 * Tipo de evento.
	 */
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	/**
	 * Fecha del evento.
	 */
	private LocalDate fecha;

	/**
	 * Hora de inicio del evento.
	 */
	private LocalTime horaInicio;

	/**
	 * Hora de fin del evento.
	 */
	private LocalTime horaFin;

	/**
	 * Indica si el evento es un plan para niños.
	 */
	private boolean paraNinos;

	/**
	 * Cupo máximo de asistentes (null si no tiene límite).
	 */
	private Integer cupoMaximo;

	/**
	 * Estado guardado del evento.
	 */
	@Enumerated(EnumType.STRING)
	private Estado estado;

	/**
	 * Motivo de la cancelación, si el evento fue cancelado.
	 */
	@Column(length = 500)
	private String motivoCancelacion;

	/**
	 * Plantilla del correo de invitación.
	 */
	@Enumerated(EnumType.STRING)
	private Plantilla plantilla;

	/**
	 * Código del enlace público de la invitación.
	 */
	@Column(unique = true, length = 40)
	private String codigoInvitacion;

	/**
	 * Constructor por defecto. El evento empieza en estado PROGRAMADO.
	 */
	public Evento() {
		this.tipo = Tipo.OTRO;
		this.estado = Estado.PROGRAMADO;
		this.plantilla = Plantilla.CLASICO;
	}

	/**
	 * Enumeración de los tipos de evento.
	 */
	public enum Tipo {
		/** Fiesta */
		FIESTA,
		/** Plan infantil */
		INFANTIL,
		/** Evento deportivo */
		DEPORTIVO,
		/** Evento cultural */
		CULTURAL,
		/** Asamblea de copropietarios */
		ASAMBLEA,
		/** Otro tipo de evento */
		OTRO
	}

	/**
	 * Enumeración de los estados de un evento.
	 */
	public enum Estado {
		/** Evento por realizarse */
		PROGRAMADO,
		/** Evento cancelado por el administrador */
		CANCELADO,
		/** Evento que ya terminó */
		FINALIZADO
	}

	/**
	 * Enumeración de las plantillas del correo de invitación.
	 */
	public enum Plantilla {
		/** Plantilla verde clásica */
		CLASICO,
		/** Plantilla oscura y dorada */
		ELEGANTE,
		/** Plantilla naranja de fiesta */
		FIESTA,
		/** Plantilla azul infantil */
		INFANTIL,
		/** Plantilla verde deportiva */
		DEPORTIVO,
		/** Plantilla gris de asamblea */
		ASAMBLEA
	}

	/**
	 * Obtiene el estado real del evento: un evento programado cuya hora de fin ya pasó se considera
	 * FINALIZADO.
	 *
	 * @return El estado actual
	 */
	public Estado getEstadoActual() {
		if (estado == Estado.PROGRAMADO && LocalDateTime.of(fecha, horaFin).isBefore(LocalDateTime.now())) {
			return Estado.FINALIZADO;
		}
		return estado;
	}

	/**
	 * Indica si el evento se cruza con un rango de horas del mismo día.
	 *
	 * @param inicio Hora de inicio del rango
	 * @param fin Hora de fin del rango
	 * @return true si hay cruce
	 */
	public boolean seCruzaCon(LocalTime inicio, LocalTime fin) {
		return horaInicio.isBefore(fin) && inicio.isBefore(horaFin);
	}

	/**
	 * Obtiene el identificador del evento.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del evento.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del conjunto del evento.
	 *
	 * @return El identificador del conjunto
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece el identificador del conjunto del evento.
	 *
	 * @param conjuntoId El identificador del nuevo conjunto
	 */
	public void setConjuntoId(Long conjuntoId) {
		this.conjuntoId = conjuntoId;
	}

	/**
	 * Obtiene el identificador del espacio del evento.
	 *
	 * @return El identificador del espacio, o null si no tiene
	 */
	public Long getEspacioId() {
		return espacioId;
	}

	/**
	 * Establece el identificador del espacio del evento.
	 *
	 * @param espacioId El identificador del nuevo espacio
	 */
	public void setEspacioId(Long espacioId) {
		this.espacioId = espacioId;
	}

	/**
	 * Obtiene el título del evento.
	 *
	 * @return El título
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Establece el título del evento.
	 *
	 * @param titulo El nuevo título
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Obtiene la descripción del evento.
	 *
	 * @return La descripción
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece la descripción del evento.
	 *
	 * @param descripcion La nueva descripción
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene el tipo de evento.
	 *
	 * @return El tipo
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece el tipo de evento.
	 *
	 * @param tipo El nuevo tipo
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene la fecha del evento.
	 *
	 * @return La fecha
	 */
	public LocalDate getFecha() {
		return fecha;
	}

	/**
	 * Establece la fecha del evento.
	 *
	 * @param fecha La nueva fecha
	 */
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene la hora de inicio.
	 *
	 * @return La hora de inicio
	 */
	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece la hora de inicio.
	 *
	 * @param horaInicio La nueva hora de inicio
	 */
	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene la hora de fin.
	 *
	 * @return La hora de fin
	 */
	public LocalTime getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece la hora de fin.
	 *
	 * @param horaFin La nueva hora de fin
	 */
	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Indica si el evento es para niños.
	 *
	 * @return true si es para niños
	 */
	public boolean isParaNinos() {
		return paraNinos;
	}

	/**
	 * Establece si el evento es para niños.
	 *
	 * @param paraNinos true si es para niños
	 */
	public void setParaNinos(boolean paraNinos) {
		this.paraNinos = paraNinos;
	}

	/**
	 * Obtiene el cupo máximo.
	 *
	 * @return El cupo máximo, o null si no tiene límite
	 */
	public Integer getCupoMaximo() {
		return cupoMaximo;
	}

	/**
	 * Establece el cupo máximo.
	 *
	 * @param cupoMaximo El nuevo cupo máximo
	 */
	public void setCupoMaximo(Integer cupoMaximo) {
		this.cupoMaximo = cupoMaximo;
	}

	/**
	 * Obtiene el estado guardado del evento.
	 *
	 * @return El estado
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * Establece el estado del evento.
	 *
	 * @param estado El nuevo estado
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene el motivo de la cancelación.
	 *
	 * @return El motivo de la cancelación
	 */
	public String getMotivoCancelacion() {
		return motivoCancelacion;
	}

	/**
	 * Establece el motivo de la cancelación.
	 *
	 * @param motivoCancelacion El motivo
	 */
	public void setMotivoCancelacion(String motivoCancelacion) {
		this.motivoCancelacion = motivoCancelacion;
	}

	/**
	 * Obtiene la plantilla de la invitación.
	 *
	 * @return La plantilla
	 */
	public Plantilla getPlantilla() {
		return plantilla;
	}

	/**
	 * Establece la plantilla de la invitación.
	 *
	 * @param plantilla La nueva plantilla
	 */
	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * Obtiene el código del enlace público de la invitación.
	 *
	 * @return El código, o null si aún no tiene
	 */
	public String getCodigoInvitacion() {
		return codigoInvitacion;
	}

	/**
	 * Establece el código del enlace público de la invitación.
	 *
	 * @param codigoInvitacion El nuevo código
	 */
	public void setCodigoInvitacion(String codigoInvitacion) {
		this.codigoInvitacion = codigoInvitacion;
	}

	/**
	 * Calcula el código hash para este evento.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, titulo, fecha);
	}

	/**
	 * Compara este evento con otro objeto para determinar si son iguales.
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
		Evento other = (Evento) obj;
		return Objects.equals(id, other.id) && Objects.equals(titulo, other.titulo)
				&& Objects.equals(fecha, other.fecha);
	}

	/**
	 * Devuelve una representación en cadena de texto de este evento.
	 *
	 * @return Una cadena que representa este evento
	 */
	@Override
	public String toString() {
		return "Evento [id=" + id + ", titulo=" + titulo + ", tipo=" + tipo + ", fecha=" + fecha + ", estado="
				+ estado + "]";
	}

}
