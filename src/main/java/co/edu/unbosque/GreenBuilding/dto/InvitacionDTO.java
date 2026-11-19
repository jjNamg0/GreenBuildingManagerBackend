package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Evento.Plantilla;
import java.util.Objects;

/**
 * DTO con lo que ve un invitado al abrir el enlace: el plan, el lugar y las coordenadas del mapa.
 */
public class InvitacionDTO {

	/** Tipo de invitación: EVENTO o RESERVA. */
	private String tipo;

	/** Título del plan. */
	private String titulo;

	/** Descripción del plan. */
	private String descripcion;

	/** Quién invita: el conjunto o el residente que hizo la reserva. */
	private String anfitrion;

	/** Fecha del plan en formato AAAA-MM-DD. */
	private String fecha;

	/** Hora de inicio en formato HH:mm. */
	private String horaInicio;

	/** Hora de fin en formato HH:mm. */
	private String horaFin;

	/** Plantilla visual de la invitación. */
	private Plantilla plantilla;

	/** Estado del plan: VIGENTE, CANCELADO o FINALIZADO. */
	private String estado;

	/** Motivo de la cancelación, si el plan fue cancelado. */
	private String motivoCancelacion;

	/** Identificador del conjunto donde es el plan. */
	private Long conjuntoId;

	/** Nombre del conjunto. */
	private String conjuntoNombre;

	/** Dirección del conjunto. */
	private String direccion;

	/** Municipio del conjunto. */
	private String municipio;

	/** Latitud del conjunto. */
	private Double latitud;

	/** Longitud del conjunto. */
	private Double longitud;

	/** Espacio común donde es el plan, si tiene. */
	private String espacioNombre;

	/** Constructor por defecto. Crea una instancia vacía de InvitacionDTO. */
	public InvitacionDTO() {}

	/**
	 * Obtiene: tipo de invitación: EVENTO o RESERVA.
	 *
	 * @return Tipo de invitación: EVENTO o RESERVA
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Establece: tipo de invitación: EVENTO o RESERVA.
	 *
	 * @param tipo Tipo de invitación: EVENTO o RESERVA
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene: título del plan.
	 *
	 * @return Título del plan
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Establece: título del plan.
	 *
	 * @param titulo Título del plan
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Obtiene: descripción del plan.
	 *
	 * @return Descripción del plan
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece: descripción del plan.
	 *
	 * @param descripcion Descripción del plan
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene: quién invita: el conjunto o el residente que hizo la reserva.
	 *
	 * @return Quién invita: el conjunto o el residente que hizo la reserva
	 */
	public String getAnfitrion() {
		return anfitrion;
	}

	/**
	 * Establece: quién invita: el conjunto o el residente que hizo la reserva.
	 *
	 * @param anfitrion Quién invita: el conjunto o el residente que hizo la reserva
	 */
	public void setAnfitrion(String anfitrion) {
		this.anfitrion = anfitrion;
	}

	/**
	 * Obtiene: fecha del plan en formato AAAA-MM-DD.
	 *
	 * @return Fecha del plan en formato AAAA-MM-DD
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * Establece: fecha del plan en formato AAAA-MM-DD.
	 *
	 * @param fecha Fecha del plan en formato AAAA-MM-DD
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene: hora de inicio en formato HH:mm.
	 *
	 * @return Hora de inicio en formato HH:mm
	 */
	public String getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Establece: hora de inicio en formato HH:mm.
	 *
	 * @param horaInicio Hora de inicio en formato HH:mm
	 */
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * Obtiene: hora de fin en formato HH:mm.
	 *
	 * @return Hora de fin en formato HH:mm
	 */
	public String getHoraFin() {
		return horaFin;
	}

	/**
	 * Establece: hora de fin en formato HH:mm.
	 *
	 * @param horaFin Hora de fin en formato HH:mm
	 */
	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * Obtiene: plantilla visual de la invitación.
	 *
	 * @return Plantilla visual de la invitación
	 */
	public Plantilla getPlantilla() {
		return plantilla;
	}

	/**
	 * Establece: plantilla visual de la invitación.
	 *
	 * @param plantilla Plantilla visual de la invitación
	 */
	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * Obtiene: estado del plan: VIGENTE, CANCELADO o FINALIZADO.
	 *
	 * @return Estado del plan: VIGENTE, CANCELADO o FINALIZADO
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * Establece: estado del plan: VIGENTE, CANCELADO o FINALIZADO.
	 *
	 * @param estado Estado del plan: VIGENTE, CANCELADO o FINALIZADO
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene: motivo de la cancelación, si el plan fue cancelado.
	 *
	 * @return Motivo de la cancelación, si el plan fue cancelado
	 */
	public String getMotivoCancelacion() {
		return motivoCancelacion;
	}

	/**
	 * Establece: motivo de la cancelación, si el plan fue cancelado.
	 *
	 * @param motivoCancelacion Motivo de la cancelación, si el plan fue cancelado
	 */
	public void setMotivoCancelacion(String motivoCancelacion) {
		this.motivoCancelacion = motivoCancelacion;
	}

	/**
	 * Obtiene: identificador del conjunto donde es el plan.
	 *
	 * @return Identificador del conjunto donde es el plan
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece: identificador del conjunto donde es el plan.
	 *
	 * @param conjuntoId Identificador del conjunto donde es el plan
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
	 * Obtiene: dirección del conjunto.
	 *
	 * @return Dirección del conjunto
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * Establece: dirección del conjunto.
	 *
	 * @param direccion Dirección del conjunto
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * Obtiene: municipio del conjunto.
	 *
	 * @return Municipio del conjunto
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * Establece: municipio del conjunto.
	 *
	 * @param municipio Municipio del conjunto
	 */
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	/**
	 * Obtiene: latitud del conjunto.
	 *
	 * @return Latitud del conjunto
	 */
	public Double getLatitud() {
		return latitud;
	}

	/**
	 * Establece: latitud del conjunto.
	 *
	 * @param latitud Latitud del conjunto
	 */
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	/**
	 * Obtiene: longitud del conjunto.
	 *
	 * @return Longitud del conjunto
	 */
	public Double getLongitud() {
		return longitud;
	}

	/**
	 * Establece: longitud del conjunto.
	 *
	 * @param longitud Longitud del conjunto
	 */
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	/**
	 * Obtiene: espacio común donde es el plan, si tiene.
	 *
	 * @return Espacio común donde es el plan, si tiene
	 */
	public String getEspacioNombre() {
		return espacioNombre;
	}

	/**
	 * Establece: espacio común donde es el plan, si tiene.
	 *
	 * @param espacioNombre Espacio común donde es el plan, si tiene
	 */
	public void setEspacioNombre(String espacioNombre) {
		this.espacioNombre = espacioNombre;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(tipo, titulo, descripcion, anfitrion, fecha, horaInicio, horaFin, plantilla, estado, motivoCancelacion, conjuntoId, conjuntoNombre, direccion, municipio, latitud, longitud, espacioNombre);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos InvitacionDTO son iguales si
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
		InvitacionDTO other = (InvitacionDTO) obj;
		return Objects.equals(tipo, other.tipo)
				&& Objects.equals(titulo, other.titulo)
				&& Objects.equals(descripcion, other.descripcion)
				&& Objects.equals(anfitrion, other.anfitrion)
				&& Objects.equals(fecha, other.fecha)
				&& Objects.equals(horaInicio, other.horaInicio)
				&& Objects.equals(horaFin, other.horaFin)
				&& plantilla == other.plantilla
				&& Objects.equals(estado, other.estado)
				&& Objects.equals(motivoCancelacion, other.motivoCancelacion)
				&& Objects.equals(conjuntoId, other.conjuntoId)
				&& Objects.equals(conjuntoNombre, other.conjuntoNombre)
				&& Objects.equals(direccion, other.direccion)
				&& Objects.equals(municipio, other.municipio)
				&& Objects.equals(latitud, other.latitud)
				&& Objects.equals(longitud, other.longitud)
				&& Objects.equals(espacioNombre, other.espacioNombre);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Invitacion ["
				+ "tipo="
				+ tipo
				+ ", titulo="
				+ titulo
				+ ", descripcion="
				+ descripcion
				+ ", anfitrion="
				+ anfitrion
				+ ", fecha="
				+ fecha
				+ ", horaInicio="
				+ horaInicio
				+ ", horaFin="
				+ horaFin
				+ ", plantilla="
				+ plantilla
				+ ", estado="
				+ estado
				+ ", motivoCancelacion="
				+ motivoCancelacion
				+ ", conjuntoId="
				+ conjuntoId
				+ ", conjuntoNombre="
				+ conjuntoNombre
				+ ", direccion="
				+ direccion
				+ ", municipio="
				+ municipio
				+ ", latitud="
				+ latitud
				+ ", longitud="
				+ longitud
				+ ", espacioNombre="
				+ espacioNombre
				+ "]";
	}
}
