package co.edu.unbosque.GreenBuilding.dto;

import co.edu.unbosque.GreenBuilding.model.Unidad.Tipo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO para unidades (apartamentos o casas) de un conjunto.
 */
public class UnidadDTO {

	/** Identificador único de la unidad. */
	private Long id;

	/** Identificador del conjunto. */
	private Long conjuntoId;

	/** Tipo de unidad (APARTAMENTO o CASA). */
	private Tipo tipo;

	/** Torre, bloque, manzana o etapa. */
	private String agrupacion;

	/** Número de la unidad. */
	private String numero;

	/** Descripción legible de la unidad. */
	private String descripcion;

	/** Nombres de los residentes asignados. */
	private List<String> residentes;

	/** Constructor por defecto. Crea una instancia vacía de UnidadDTO. */
	public UnidadDTO() {}

	/**
	 * Constructor con conjuntoId, tipo, agrupacion, numero.
	 *
	 * @param conjuntoId Identificador del conjunto.
	 * @param tipo Tipo de unidad (APARTAMENTO o CASA).
	 * @param agrupacion Torre, bloque, manzana o etapa.
	 * @param numero Número de la unidad.
	 */
	public UnidadDTO(Long conjuntoId, Tipo tipo, String agrupacion, String numero) {
		this.conjuntoId = conjuntoId;
		this.tipo = tipo;
		this.agrupacion = agrupacion;
		this.numero = numero;
	}

	/**
	 * Obtiene: identificador único de la unidad.
	 *
	 * @return Identificador único de la unidad
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece: identificador único de la unidad.
	 *
	 * @param id Identificador único de la unidad
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
	 * Obtiene: tipo de unidad (APARTAMENTO o CASA).
	 *
	 * @return Tipo de unidad (APARTAMENTO o CASA)
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * Establece: tipo de unidad (APARTAMENTO o CASA).
	 *
	 * @param tipo Tipo de unidad (APARTAMENTO o CASA)
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene: torre, bloque, manzana o etapa.
	 *
	 * @return Torre, bloque, manzana o etapa
	 */
	public String getAgrupacion() {
		return agrupacion;
	}

	/**
	 * Establece: torre, bloque, manzana o etapa.
	 *
	 * @param agrupacion Torre, bloque, manzana o etapa
	 */
	public void setAgrupacion(String agrupacion) {
		this.agrupacion = agrupacion;
	}

	/**
	 * Obtiene: número de la unidad.
	 *
	 * @return Número de la unidad
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * Establece: número de la unidad.
	 *
	 * @param numero Número de la unidad
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * Obtiene: descripción legible de la unidad.
	 *
	 * @return Descripción legible de la unidad
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece: descripción legible de la unidad.
	 *
	 * @param descripcion Descripción legible de la unidad
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene: nombres de los residentes asignados.
	 *
	 * @return Nombres de los residentes asignados
	 */
	public List<String> getResidentes() {
		return residentes;
	}

	/**
	 * Establece: nombres de los residentes asignados.
	 *
	 * @param residentes Nombres de los residentes asignados
	 */
	public void setResidentes(List<String> residentes) {
		if (residentes == null) {
			this.residentes = null;
		} else {
			this.residentes = new ArrayList<>(residentes);
		}
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, conjuntoId, tipo, agrupacion, numero, descripcion, residentes);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos UnidadDTO son iguales si
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
		UnidadDTO other = (UnidadDTO) obj;
		return Objects.equals(id, other.id)
				&& Objects.equals(conjuntoId, other.conjuntoId)
				&& tipo == other.tipo
				&& Objects.equals(agrupacion, other.agrupacion)
				&& Objects.equals(numero, other.numero)
				&& Objects.equals(descripcion, other.descripcion)
				&& Objects.equals(residentes, other.residentes);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "Unidad ["
				+ "id="
				+ id
				+ ", conjuntoId="
				+ conjuntoId
				+ ", tipo="
				+ tipo
				+ ", agrupacion="
				+ agrupacion
				+ ", numero="
				+ numero
				+ ", descripcion="
				+ descripcion
				+ ", residentes="
				+ residentes
				+ "]";
	}
}
