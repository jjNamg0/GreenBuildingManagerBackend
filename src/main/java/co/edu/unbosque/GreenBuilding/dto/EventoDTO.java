package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Evento.Estado;
import co.edu.unbosque.GreenBuilding.model.Evento.Plantilla;
import co.edu.unbosque.GreenBuilding.model.Evento.Tipo;
import java.util.Objects;

/**
 * DTO para eventos de un conjunto. Incluye el total de asistentes y si el usuario actual asistirá.
 */
public class EventoDTO {

	/** Identificador único del evento. */
	private Long id;

	/** Identificador del conjunto. */
	private Long conjuntoId;

	/** Nombre del conjunto. */
	private String conjuntoNombre;

	/** Identificador del espacio (opcional). */
	private Long espacioId;

	/** Nombre del espacio (opcional). */
	private String espacioNombre;

	/** Título del evento. */
	private String titulo;

	/** Descripción del evento. */
	private String descripcion;

	/** Tipo de evento. */
	private Tipo tipo;

	/** Fecha del evento (AAAA-MM-DD). */
	private String fecha;

	/** Hora de inicio (HH:mm). */
	private String horaInicio;

	/** Hora de fin (HH:mm). */
	private String horaFin;

	/** Indica si es un plan para niños. */
	private boolean paraNinos;

	/** Cupo máximo de asistentes (opcional). */
	private Integer cupoMaximo;

	/** Número de residentes inscritos. */
	private int totalAsistentes;

	/** Indica si el usuario actual está inscrito. */
	private boolean asistire;

	/** Estado del evento. */
	private Estado estado;

	/** Motivo de la cancelación. */
	private String motivoCancelacion;

	/** Plantilla del correo de invitación. */
	private Plantilla plantilla;

	/** Constructor por defecto. Crea una instancia vacía de EventoDTO. */
	public EventoDTO() {}

	/**
	 * Constructor con conjuntoId, titulo, tipo, fecha, horaInicio, horaFin.
	 *
	 * @param conjuntoId Identificador del conjunto.
	 * @param titulo Título del evento.
	 * @param tipo Tipo de evento.
	 * @param fecha Fecha del evento (AAAA-MM-DD).
	 * @param horaInicio Hora de inicio (HH:mm).
	 * @param horaFin Hora de fin (HH:mm).
	 */
	public EventoDTO(Long conjuntoId, String titulo, Tipo tipo, String fecha, String horaInicio, String horaFin) {
		this.conjuntoId = conjuntoId;
		this.titulo = titulo;
		this.tipo = tipo;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene: identificador único del evento.
	 *
	 * @return Identificador único del evento
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único del evento.
	 *
	 * @param id Identificador único del evento
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
	 * Obtiene: nombre del conjunto.
	 *
	 * @return Nombre del conjunto
	 */
	public String getConjuntoNombre() {
		return conjuntoNombre;
	}

	/**
	 * Establece: nombre del conjunto.
	 *
	 * @param conjuntoNombre Nombre del conjunto
	 */
	public void setConjuntoNombre(String conjuntoNombre) {
		this.conjuntoNombre = conjuntoNombre;
	}

	/**
	 * Obtiene: identificador del espacio (opcional).
	 *
	 * @return Identificador del espacio (opcional)
	 */
	public Long getEspacioId() {
		return espacioId;
	}

	/**
	 * Establece: identificador del espacio (opcional).
	 *
	 * @param espacioId Identificador del espacio (opcional)
	 */
	public void setEspacioId(Long espacioId) {
		this.espacioId = espacioId;
	}

	/**
	 * Obtiene: nombre del espacio (opcional).
	 *
	 * @return Nombre del espacio (opcional)
	 */
	public String getEspacioNombre() {
		return espacioNombre;
	}

	/**
	 * Establece: nombre del espacio (opcional).
	 *
	 * @param espacioNombre Nombre del espacio (opcional)
	 */
	public void setEspacioNombre(String espacioNombre) {
		this.espacioNombre = espacioNombre;
	}

	/**
	 * Obtiene: título del evento.
	 *
	 * @return Título del evento
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Establece: título del evento.
	 *
	 * @param titulo Título del evento
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Obtiene: descripción del evento.
	 *
	 * @return Descripción del evento
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece: descripción del evento.
	 *
	 * @param descripcion Descripción del evento
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene: tipo de evento.
	 *
	 * @return Tipo de evento
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece: tipo de evento.
	 *
	 * @param tipo Tipo de evento
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene: fecha del evento (AAAA-MM-DD).
	 *
	 * @return Fecha del evento (AAAA-MM-DD)
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * Establece: fecha del evento (AAAA-MM-DD).
	 *
	 * @param fecha Fecha del evento (AAAA-MM-DD)
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene: hora de inicio (HH:mm).
	 *
	 * @return Hora de inicio (HH:mm)
	 */
	public String getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece: hora de inicio (HH:mm).
	 *
	 * @param horaInicio Hora de inicio (HH:mm)
	 */
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene: hora de fin (HH:mm).
	 *
	 * @return Hora de fin (HH:mm)
	 */
	public String getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece: hora de fin (HH:mm).
	 *
	 * @param horaFin Hora de fin (HH:mm)
	 */
	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene: indica si es un plan para niños.
	 *
	 * @return Indica si es un plan para niños
	 */
	public boolean isParaNinos() {
		return paraNinos;
	}

	/**
	 * Establece: indica si es un plan para niños.
	 *
	 * @param paraNinos Indica si es un plan para niños
	 */
	public void setParaNinos(boolean paraNinos) {
		this.paraNinos = paraNinos;
	}

	/**
	 * Obtiene: cupo máximo de asistentes (opcional).
	 *
	 * @return Cupo máximo de asistentes (opcional)
	 */
	public Integer getCupoMaximo() {
		return cupoMaximo;
	}

	/**
	 * Establece: cupo máximo de asistentes (opcional).
	 *
	 * @param cupoMaximo Cupo máximo de asistentes (opcional)
	 */
	public void setCupoMaximo(Integer cupoMaximo) {
		this.cupoMaximo = cupoMaximo;
	}

	/**
	 * Obtiene: número de residentes inscritos.
	 *
	 * @return Número de residentes inscritos
	 */
	public int getTotalAsistentes() {
		return totalAsistentes;
	}

	/**
	 * Establece: número de residentes inscritos.
	 *
	 * @param totalAsistentes Número de residentes inscritos
	 */
	public void setTotalAsistentes(int totalAsistentes) {
		this.totalAsistentes = totalAsistentes;
	}

	/**
	 * Obtiene: indica si el usuario actual está inscrito.
	 *
	 * @return Indica si el usuario actual está inscrito
	 */
	public boolean isAsistire() {
		return asistire;
	}

	/**
	 * Establece: indica si el usuario actual está inscrito.
	 *
	 * @param asistire Indica si el usuario actual está inscrito
	 */
	public void setAsistire(boolean asistire) {
		this.asistire = asistire;
	}

	/**
	 * Obtiene: estado del evento.
	 *
	 * @return Estado del evento
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * Establece: estado del evento.
	 *
	 * @param estado Estado del evento
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene: motivo de la cancelación.
	 *
	 * @return Motivo de la cancelación
	 */
	public String getMotivoCancelacion() {
		return motivoCancelacion;
	}

	/**
	 * Establece: motivo de la cancelación.
	 *
	 * @param motivoCancelacion Motivo de la cancelación
	 */
	public void setMotivoCancelacion(String motivoCancelacion) {
		this.motivoCancelacion = motivoCancelacion;
	}

	/**
	 * Obtiene: plantilla del correo de invitación.
	 *
	 * @return Plantilla del correo de invitación
	 */
	public Plantilla getPlantilla() {
		return plantilla;
	}

	/**
	 * Establece: plantilla del correo de invitación.
	 *
	 * @param plantilla Plantilla del correo de invitación
	 */
	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, conjuntoId, conjuntoNombre, espacioId, espacioNombre, titulo, descripcion, tipo, fecha, horaInicio, horaFin, paraNinos, cupoMaximo, totalAsistentes, asistire, estado, motivoCancelacion, plantilla);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos EventoDTO son iguales si
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
		EventoDTO other = (EventoDTO) obj;
		return mismosDatosPrincipales(other) && mismosDatosAdicionales(other);
	}

	/**
	 * Compara la primera mitad de los atributos de este objeto con los de otro.
	 *
	 * @param other Objeto a comparar
	 * @return true si esos atributos son iguales, false en caso contrario
	 */
	private boolean mismosDatosPrincipales(EventoDTO other) {
		return Objects.equals(id, other.id)
				&& Objects.equals(conjuntoId, other.conjuntoId)
				&& Objects.equals(conjuntoNombre, other.conjuntoNombre)
				&& Objects.equals(espacioId, other.espacioId)
				&& Objects.equals(espacioNombre, other.espacioNombre)
				&& Objects.equals(titulo, other.titulo)
				&& Objects.equals(descripcion, other.descripcion)
				&& tipo == other.tipo
				&& Objects.equals(fecha, other.fecha);
	}

	/**
	 * Compara la segunda mitad de los atributos de este objeto con los de otro.
	 *
	 * @param other Objeto a comparar
	 * @return true si esos atributos son iguales, false en caso contrario
	 */
	private boolean mismosDatosAdicionales(EventoDTO other) {
		return Objects.equals(horaInicio, other.horaInicio)
				&& Objects.equals(horaFin, other.horaFin)
				&& paraNinos == other.paraNinos
				&& Objects.equals(cupoMaximo, other.cupoMaximo)
				&& totalAsistentes == other.totalAsistentes
				&& asistire == other.asistire
				&& estado == other.estado
				&& Objects.equals(motivoCancelacion, other.motivoCancelacion)
				&& plantilla == other.plantilla;
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Evento ["
				+ "id="
				+ id
				+ ", conjuntoId="
				+ conjuntoId
				+ ", conjuntoNombre="
				+ conjuntoNombre
				+ ", espacioId="
				+ espacioId
				+ ", espacioNombre="
				+ espacioNombre
				+ ", titulo="
				+ titulo
				+ ", descripcion="
				+ descripcion
				+ ", tipo="
				+ tipo
				+ ", fecha="
				+ fecha
				+ ", horaInicio="
				+ horaInicio
				+ ", horaFin="
				+ horaFin
				+ ", paraNinos="
				+ paraNinos
				+ ", cupoMaximo="
				+ cupoMaximo
				+ ", totalAsistentes="
				+ totalAsistentes
				+ ", asistire="
				+ asistire
				+ ", estado="
				+ estado
				+ ", motivoCancelacion="
				+ motivoCancelacion
				+ ", plantilla="
				+ plantilla
				+ "]";
	}
}
