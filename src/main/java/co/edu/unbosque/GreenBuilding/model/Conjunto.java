package co.edu.unbosque.GreenBuilding.model;

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
 * Clase que representa un conjunto residencial de Cundinamarca.
 *
 * Esta entidad se almacena en la tabla "conjunto" en la base de datos.
 */
@Entity
@Table(name = "conjunto")
public class Conjunto {

	/**
	 * Identificador único del conjunto.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Nombre del conjunto.
	 */
	private String nombre;

	/**
	 * Tipo de conjunto (apartamentos o casas).
	 */
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	/**
	 * Dirección del conjunto.
	 */
	private String direccion;

	/**
	 * Municipio de Cundinamarca donde queda el conjunto.
	 */
	private String municipio;

	/**
	 * Latitud de la ubicación del conjunto.
	 */
	private double latitud;

	/**
	 * Longitud de la ubicación del conjunto.
	 */
	private double longitud;

	/**
	 * Descripción del conjunto.
	 */
	@Column(length = 2000)
	private String descripcion;

	/**
	 * URL de la imagen del conjunto.
	 */
	@Column(length = 1000)
	private String imagenUrl;

	/**
	 * Constructor por defecto.
	 */
	public Conjunto() {
		this.tipo = Tipo.APARTAMENTOS;
	}

	/**
	 * Constructor con los datos principales del conjunto.
	 *
	 * @param nombre Nombre del conjunto
	 * @param tipo Tipo de conjunto
	 * @param direccion Dirección
	 * @param municipio Municipio
	 * @param latitud Latitud
	 * @param longitud Longitud
	 * @param descripcion Descripción
	 */
	public Conjunto(String nombre, Tipo tipo, String direccion, String municipio, double latitud, double longitud,
			String descripcion) {
		this.nombre = nombre;
		this.tipo = tipo;
		this.direccion = direccion;
		this.municipio = municipio;
		this.latitud = latitud;
		this.longitud = longitud;
		this.descripcion = descripcion;
	}

	/**
	 * Enumeración de los tipos de conjunto.
	 */
	public enum Tipo {
		/** Conjunto de apartamentos */
		APARTAMENTOS,
		/** Conjunto de casas */
		CASAS
	}

	/**
	 * Obtiene el identificador del conjunto.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del conjunto.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre del conjunto.
	 *
	 * @return El nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del conjunto.
	 *
	 * @param nombre El nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el tipo de conjunto.
	 *
	 * @return El tipo
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece el tipo de conjunto.
	 *
	 * @param tipo El nuevo tipo
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene la dirección del conjunto.
	 *
	 * @return La dirección
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * Establece la dirección del conjunto.
	 *
	 * @param direccion La nueva dirección
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * Obtiene el municipio del conjunto.
	 *
	 * @return El municipio
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * Establece el municipio del conjunto.
	 *
	 * @param municipio El nuevo municipio
	 */
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	/**
	 * Obtiene la latitud del conjunto.
	 *
	 * @return La latitud
	 */
	public double getLatitud() {
		return latitud;
	}

	/**
	 * Establece la latitud del conjunto.
	 *
	 * @param latitud La nueva latitud
	 */
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	/**
	 * Obtiene la longitud del conjunto.
	 *
	 * @return La longitud
	 */
	public double getLongitud() {
		return longitud;
	}

	/**
	 * Establece la longitud del conjunto.
	 *
	 * @param longitud La nueva longitud
	 */
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	/**
	 * Obtiene la descripción del conjunto.
	 *
	 * @return La descripción
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece la descripción del conjunto.
	 *
	 * @param descripcion La nueva descripción
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene la URL de la imagen del conjunto.
	 *
	 * @return La URL de la imagen
	 */
	public String getImagenUrl() {
		return imagenUrl;
	}

	/**
	 * Establece la URL de la imagen del conjunto.
	 *
	 * @param imagenUrl La nueva URL
	 */
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	/**
	 * Calcula el código hash para este conjunto.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, nombre, municipio);
	}

	/**
	 * Compara este conjunto con otro objeto para determinar si son iguales.
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
		Conjunto other = (Conjunto) obj;
		return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(municipio, other.municipio);
	}

	/**
	 * Devuelve una representación en cadena de texto de este conjunto.
	 *
	 * @return Una cadena que representa este conjunto
	 */
	@Override
	public String toString() {
		return "Conjunto [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", municipio=" + municipio + "]";
	}

}
