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
 * Clase que representa una unidad (apartamento o casa) dentro de un conjunto.
 *
 * Esta entidad se almacena en la tabla "unidad" en la base de datos.
 */
@Entity
@Table(name = "unidad")
public class Unidad {

	/**
	 * Identificador único de la unidad.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Identificador del conjunto al que pertenece la unidad.
	 */
	@Column(nullable = false)
	private Long conjuntoId;

	/**
	 * Tipo de unidad (apartamento o casa).
	 */
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	/**
	 * Agrupación de la unidad: torre o bloque para apartamentos, manzana o etapa para casas.
	 */
	private String agrupacion;

	/**
	 * Número de la unidad.
	 */
	private String numero;

	/**
	 * Constructor por defecto.
	 */
	public Unidad() {
		this.tipo = Tipo.APARTAMENTO;
	}

	/**
	 * Constructor con todos los datos de la unidad.
	 *
	 * @param conjuntoId Identificador del conjunto al que pertenece
	 * @param tipo Tipo de unidad
	 * @param agrupacion Torre, bloque, manzana o etapa
	 * @param numero Número de la unidad
	 */
	public Unidad(Long conjuntoId, Tipo tipo, String agrupacion, String numero) {
		this.conjuntoId = conjuntoId;
		this.tipo = tipo;
		this.agrupacion = agrupacion;
		this.numero = numero;
	}

	/**
	 * Enumeración de los tipos de unidad.
	 */
	public enum Tipo {
		/** Apartamento */
		APARTAMENTO,
		/** Casa */
		CASA
	}

	/**
	 * Obtiene la descripción legible de la unidad, por ejemplo "Torre 2 · Apto 301".
	 *
	 * @return La descripción de la unidad
	 */
	public String getDescripcion() {
		String prefijo = tipo == Tipo.CASA ? "Casa" : "Apto";
		return agrupacion + " · " + prefijo + " " + numero;
	}

	/**
	 * Obtiene el identificador de la unidad.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la unidad.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del conjunto de la unidad.
	 *
	 * @return El identificador del conjunto
	 */
	public Long getConjuntoId() {
		return conjuntoId;
	}

	/**
	 * Establece el identificador del conjunto de la unidad.
	 *
	 * @param conjuntoId El identificador del nuevo conjunto
	 */
	public void setConjuntoId(Long conjuntoId) {
		this.conjuntoId = conjuntoId;
	}

	/**
	 * Obtiene el tipo de unidad.
	 *
	 * @return El tipo
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece el tipo de unidad.
	 *
	 * @param tipo El nuevo tipo
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene la agrupación de la unidad.
	 *
	 * @return La agrupación
	 */
	public String getAgrupacion() {
		return agrupacion;
	}

	/**
	 * Establece la agrupación de la unidad.
	 *
	 * @param agrupacion La nueva agrupación
	 */
	public void setAgrupacion(String agrupacion) {
		this.agrupacion = agrupacion;
	}

	/**
	 * Obtiene el número de la unidad.
	 *
	 * @return El número
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * Establece el número de la unidad.
	 *
	 * @param numero El nuevo número
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * Calcula el código hash para esta unidad.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, agrupacion, numero);
	}

	/**
	 * Compara esta unidad con otro objeto para determinar si son iguales.
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
		Unidad other = (Unidad) obj;
		return Objects.equals(id, other.id) && Objects.equals(agrupacion, other.agrupacion)
				&& Objects.equals(numero, other.numero);
	}

	/**
	 * Devuelve una representación en cadena de texto de esta unidad.
	 *
	 * @return Una cadena que representa esta unidad
	 */
	@Override
	public String toString() {
		return "Unidad [id=" + id + ", tipo=" + tipo + ", agrupacion=" + agrupacion + ", numero=" + numero + "]";
	}

}
