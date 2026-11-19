package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con una página de la auditoría: los registros de la página y los totales. Los nombres
 * de los campos son los que espera el front.
 */
public class PaginaAuditoriaDTO {

	/** Registros de la página. */
	private List<RegistroAuditoriaDTO> content;

	/** Total de registros que cumplen el filtro. */
	private long totalElements;

	/** Total de páginas. */
	private int totalPages;

	/** Número de la página, desde 0. */
	private int number;

	/** Tamaño de la página. */
	private int size;

	/** Constructor por defecto. Crea una instancia vacía de PaginaAuditoriaDTO. */
	public PaginaAuditoriaDTO() {}

	/**
	 * Constructor con content, totalElements, totalPages, number, size.
	 *
	 * @param content Registros de la página.
	 * @param totalElements Total de registros que cumplen el filtro.
	 * @param totalPages Total de páginas.
	 * @param number Número de la página, desde 0.
	 * @param size Tamaño de la página.
	 */
	public PaginaAuditoriaDTO(List<RegistroAuditoriaDTO> content, long totalElements, int totalPages, int number, int size) {
		this.content = content;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.number = number;
		this.size = size;
	}

	/**
	 * Obtiene: registros de la página.
	 *
	 * @return Registros de la página
	 */
	public List<RegistroAuditoriaDTO> getContent() {
		return content;
	}

	/**
	 * Establece: registros de la página.
	 *
	 * @param content Registros de la página
	 */
	public void setContent(List<RegistroAuditoriaDTO> content) {
		this.content = content;
	}

	/**
	 * Obtiene: total de registros que cumplen el filtro.
	 *
	 * @return Total de registros que cumplen el filtro
	 */
	public long getTotalElements() {
		return totalElements;
	}

	/**
	 * Establece: total de registros que cumplen el filtro.
	 *
	 * @param totalElements Total de registros que cumplen el filtro
	 */
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * Obtiene: total de páginas.
	 *
	 * @return Total de páginas
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * Establece: total de páginas.
	 *
	 * @param totalPages Total de páginas
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * Obtiene: número de la página, desde 0.
	 *
	 * @return Número de la página, desde 0
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Establece: número de la página, desde 0.
	 *
	 * @param number Número de la página, desde 0
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Obtiene: tamaño de la página.
	 *
	 * @return Tamaño de la página
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Establece: tamaño de la página.
	 *
	 * @param size Tamaño de la página
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(content, totalElements, totalPages, number, size);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos PaginaAuditoriaDTO son iguales si
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
		PaginaAuditoriaDTO other = (PaginaAuditoriaDTO) obj;
		return Objects.equals(content, other.content)
				&& totalElements == other.totalElements
				&& totalPages == other.totalPages
				&& number == other.number
				&& size == other.size;
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "PaginaAuditoria ["
				+ "content="
				+ content
				+ ", totalElements="
				+ totalElements
				+ ", totalPages="
				+ totalPages
				+ ", number="
				+ number
				+ ", size="
				+ size
				+ "]";
	}
}
