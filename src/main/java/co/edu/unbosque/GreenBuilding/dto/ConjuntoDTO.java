package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Conjunto.Tipo;
import java.util.Objects;

/**
 * DTO para conjuntos residenciales. Incluye el total de unidades y espacios del conjunto.
 */
public class ConjuntoDTO {

	/** Identificador único del conjunto. */
	private Long id;

	/** Nombre del conjunto. */
	private String nombre;

	/** Tipo de conjunto (APARTAMENTOS o CASAS). */
	private Tipo tipo;

	/** Dirección del conjunto. */
	private String direccion;

	/** Municipio de Cundinamarca. */
	private String municipio;

	/** Latitud de la ubicación. */
	private Double latitud;

	/** Longitud de la ubicación. */
	private Double longitud;

	/** Descripción del conjunto. */
	private String descripcion;

	/** URL de la imagen del conjunto. */
	private String imagenUrl;

	/** Número de unidades registradas en el conjunto. */
	private long totalUnidades;

	/** Número de espacios comunes del conjunto. */
	private long totalEspacios;

	/** Constructor por defecto. Crea una instancia vacía de ConjuntoDTO. */
	public ConjuntoDTO() {}

	/**
	 * Constructor con nombre, tipo, direccion, municipio, latitud, longitud.
	 *
	 * @param nombre Nombre del conjunto.
	 * @param tipo Tipo de conjunto (APARTAMENTOS o CASAS).
	 * @param direccion Dirección del conjunto.
	 * @param municipio Municipio de Cundinamarca.
	 * @param latitud Latitud de la ubicación.
	 * @param longitud Longitud de la ubicación.
	 */
	public ConjuntoDTO(String nombre, Tipo tipo, String direccion, String municipio, Double latitud, Double longitud) {
		this.nombre = nombre;
		this.tipo = tipo;
		this.direccion = direccion;
		this.municipio = municipio;
		this.latitud = latitud;
		this.longitud = longitud;
	}

	/**
	 * Obtiene: identificador único del conjunto.
	 *
	 * @return Identificador único del conjunto
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único del conjunto.
	 *
	 * @param id Identificador único del conjunto
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene: nombre del conjunto.
	 *
	 * @return Nombre del conjunto
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece: nombre del conjunto.
	 *
	 * @param nombre Nombre del conjunto
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene: tipo de conjunto (APARTAMENTOS o CASAS).
	 *
	 * @return Tipo de conjunto (APARTAMENTOS o CASAS)
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece: tipo de conjunto (APARTAMENTOS o CASAS).
	 *
	 * @param tipo Tipo de conjunto (APARTAMENTOS o CASAS)
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
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
	 * Obtiene: municipio de Cundinamarca.
	 *
	 * @return Municipio de Cundinamarca
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * Establece: municipio de Cundinamarca.
	 *
	 * @param municipio Municipio de Cundinamarca
	 */
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	/**
	 * Obtiene: latitud de la ubicación.
	 *
	 * @return Latitud de la ubicación
	 */
	public Double getLatitud() {
		return latitud;
	}

	/**
	 * Establece: latitud de la ubicación.
	 *
	 * @param latitud Latitud de la ubicación
	 */
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	/**
	 * Obtiene: longitud de la ubicación.
	 *
	 * @return Longitud de la ubicación
	 */
	public Double getLongitud() {
		return longitud;
	}

	/**
	 * Establece: longitud de la ubicación.
	 *
	 * @param longitud Longitud de la ubicación
	 */
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	/**
	 * Obtiene: descripción del conjunto.
	 *
	 * @return Descripción del conjunto
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece: descripción del conjunto.
	 *
	 * @param descripcion Descripción del conjunto
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene: uRL de la imagen del conjunto.
	 *
	 * @return URL de la imagen del conjunto
	 */
	public String getImagenUrl() {
		return imagenUrl;
	}

	/**
	 * Establece: uRL de la imagen del conjunto.
	 *
	 * @param imagenUrl URL de la imagen del conjunto
	 */
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	/**
	 * Obtiene: número de unidades registradas en el conjunto.
	 *
	 * @return Número de unidades registradas en el conjunto
	 */
	public long getTotalUnidades() {
		return totalUnidades;
	}

	/**
	 * Establece: número de unidades registradas en el conjunto.
	 *
	 * @param totalUnidades Número de unidades registradas en el conjunto
	 */
	public void setTotalUnidades(long totalUnidades) {
		this.totalUnidades = totalUnidades;
	}

	/**
	 * Obtiene: número de espacios comunes del conjunto.
	 *
	 * @return Número de espacios comunes del conjunto
	 */
	public long getTotalEspacios() {
		return totalEspacios;
	}

	/**
	 * Establece: número de espacios comunes del conjunto.
	 *
	 * @param totalEspacios Número de espacios comunes del conjunto
	 */
	public void setTotalEspacios(long totalEspacios) {
		this.totalEspacios = totalEspacios;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, nombre, tipo, direccion, municipio, latitud, longitud, descripcion, imagenUrl, totalUnidades, totalEspacios);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos ConjuntoDTO son iguales si
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
		ConjuntoDTO other = (ConjuntoDTO) obj;
		return Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre)
				&& tipo == other.tipo
				&& Objects.equals(direccion, other.direccion)
				&& Objects.equals(municipio, other.municipio)
				&& Objects.equals(latitud, other.latitud)
				&& Objects.equals(longitud, other.longitud)
				&& Objects.equals(descripcion, other.descripcion)
				&& Objects.equals(imagenUrl, other.imagenUrl)
				&& totalUnidades == other.totalUnidades
				&& totalEspacios == other.totalEspacios;
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Conjunto ["
				+ "id="
				+ id
				+ ", nombre="
				+ nombre
				+ ", tipo="
				+ tipo
				+ ", direccion="
				+ direccion
				+ ", municipio="
				+ municipio
				+ ", latitud="
				+ latitud
				+ ", longitud="
				+ longitud
				+ ", descripcion="
				+ descripcion
				+ ", imagenUrl="
				+ imagenUrl
				+ ", totalUnidades="
				+ totalUnidades
				+ ", totalEspacios="
				+ totalEspacios
				+ "]";
	}
}
