package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con los indicadores del panel de administración.
 */
public class ResumenDTO {

	/** Número de conjuntos. */
	private long totalConjuntos;

	/** Número de unidades. */
	private long totalUnidades;

	/** Número de residentes con unidad. */
	private long totalResidentes;

	/** Número de espacios comunes. */
	private long totalEspacios;

	/** Número de eventos programados desde hoy. */
	private long eventosProximos;

	/** Número de reservas por revisar. */
	private long reservasPendientes;

	/** Número de residentes activos que esperan unidad. */
	private long usuariosSinUnidad;

	/** Constructor por defecto. Crea una instancia vacía de ResumenDTO. */
	public ResumenDTO() {}

	/**
	 * Constructor con totalConjuntos, totalUnidades, totalResidentes, totalEspacios, eventosProximos, reservasPendientes, usuariosSinUnidad.
	 *
	 * @param totalConjuntos Número de conjuntos.
	 * @param totalUnidades Número de unidades.
	 * @param totalResidentes Número de residentes con unidad.
	 * @param totalEspacios Número de espacios comunes.
	 * @param eventosProximos Número de eventos programados desde hoy.
	 * @param reservasPendientes Número de reservas por revisar.
	 * @param usuariosSinUnidad Número de residentes activos que esperan unidad.
	 */
	public ResumenDTO(long totalConjuntos, long totalUnidades, long totalResidentes, long totalEspacios, long eventosProximos, long reservasPendientes, long usuariosSinUnidad) {
		this.totalConjuntos = totalConjuntos;
		this.totalUnidades = totalUnidades;
		this.totalResidentes = totalResidentes;
		this.totalEspacios = totalEspacios;
		this.eventosProximos = eventosProximos;
		this.reservasPendientes = reservasPendientes;
		this.usuariosSinUnidad = usuariosSinUnidad;
	}

	/**
	 * Obtiene: número de conjuntos.
	 *
	 * @return Número de conjuntos
	 */
	public long getTotalConjuntos() {
		return totalConjuntos;
	}

	/**
	 * Establece: número de conjuntos.
	 *
	 * @param totalConjuntos Número de conjuntos
	 */
	public void setTotalConjuntos(long totalConjuntos) {
		this.totalConjuntos = totalConjuntos;
	}

	/**
	 * Obtiene: número de unidades.
	 *
	 * @return Número de unidades
	 */
	public long getTotalUnidades() {
		return totalUnidades;
	}

	/**
	 * Establece: número de unidades.
	 *
	 * @param totalUnidades Número de unidades
	 */
	public void setTotalUnidades(long totalUnidades) {
		this.totalUnidades = totalUnidades;
	}

	/**
	 * Obtiene: número de residentes con unidad.
	 *
	 * @return Número de residentes con unidad
	 */
	public long getTotalResidentes() {
		return totalResidentes;
	}

	/**
	 * Establece: número de residentes con unidad.
	 *
	 * @param totalResidentes Número de residentes con unidad
	 */
	public void setTotalResidentes(long totalResidentes) {
		this.totalResidentes = totalResidentes;
	}

	/**
	 * Obtiene: número de espacios comunes.
	 *
	 * @return Número de espacios comunes
	 */
	public long getTotalEspacios() {
		return totalEspacios;
	}

	/**
	 * Establece: número de espacios comunes.
	 *
	 * @param totalEspacios Número de espacios comunes
	 */
	public void setTotalEspacios(long totalEspacios) {
		this.totalEspacios = totalEspacios;
	}

	/**
	 * Obtiene: número de eventos programados desde hoy.
	 *
	 * @return Número de eventos programados desde hoy
	 */
	public long getEventosProximos() {
		return eventosProximos;
	}

	/**
	 * Establece: número de eventos programados desde hoy.
	 *
	 * @param eventosProximos Número de eventos programados desde hoy
	 */
	public void setEventosProximos(long eventosProximos) {
		this.eventosProximos = eventosProximos;
	}

	/**
	 * Obtiene: número de reservas por revisar.
	 *
	 * @return Número de reservas por revisar
	 */
	public long getReservasPendientes() {
		return reservasPendientes;
	}

	/**
	 * Establece: número de reservas por revisar.
	 *
	 * @param reservasPendientes Número de reservas por revisar
	 */
	public void setReservasPendientes(long reservasPendientes) {
		this.reservasPendientes = reservasPendientes;
	}

	/**
	 * Obtiene: número de residentes activos que esperan unidad.
	 *
	 * @return Número de residentes activos que esperan unidad
	 */
	public long getUsuariosSinUnidad() {
		return usuariosSinUnidad;
	}

	/**
	 * Establece: número de residentes activos que esperan unidad.
	 *
	 * @param usuariosSinUnidad Número de residentes activos que esperan unidad
	 */
	public void setUsuariosSinUnidad(long usuariosSinUnidad) {
		this.usuariosSinUnidad = usuariosSinUnidad;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(totalConjuntos, totalUnidades, totalResidentes, totalEspacios, eventosProximos, reservasPendientes, usuariosSinUnidad);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos ResumenDTO son iguales si
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
		ResumenDTO other = (ResumenDTO) obj;
		return totalConjuntos == other.totalConjuntos
				&& totalUnidades == other.totalUnidades
				&& totalResidentes == other.totalResidentes
				&& totalEspacios == other.totalEspacios
				&& eventosProximos == other.eventosProximos
				&& reservasPendientes == other.reservasPendientes
				&& usuariosSinUnidad == other.usuariosSinUnidad;
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Resumen ["
				+ "totalConjuntos="
				+ totalConjuntos
				+ ", totalUnidades="
				+ totalUnidades
				+ ", totalResidentes="
				+ totalResidentes
				+ ", totalEspacios="
				+ totalEspacios
				+ ", eventosProximos="
				+ eventosProximos
				+ ", reservasPendientes="
				+ reservasPendientes
				+ ", usuariosSinUnidad="
				+ usuariosSinUnidad
				+ "]";
	}
}
