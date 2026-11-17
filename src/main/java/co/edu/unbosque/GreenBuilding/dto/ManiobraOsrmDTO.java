package co.edu.unbosque.GreenBuilding.dto;

import java.util.Objects;

/**
 * DTO con la maniobra de un paso de OSRM.
 */
public class ManiobraOsrmDTO {

	/** Tipo de maniobra, por ejemplo turn, depart o arrive. */
	private String type;

	/** Dirección del giro, por ejemplo left o right. */
	private String modifier;

	/** Salida que se toma en una glorieta. */
	private Integer exit;

	/** Constructor por defecto. Crea una instancia vacía de ManiobraOsrmDTO. */
	public ManiobraOsrmDTO() {}

	/**
	 * Constructor con type, modifier, exit.
	 *
	 * @param type Tipo de maniobra, por ejemplo turn, depart o arrive.
	 * @param modifier Dirección del giro, por ejemplo left o right.
	 * @param exit Salida que se toma en una glorieta.
	 */
	public ManiobraOsrmDTO(String type, String modifier, Integer exit) {
		this.type = type;
		this.modifier = modifier;
		this.exit = exit;
	}

	/**
	 * Obtiene: tipo de maniobra, por ejemplo turn, depart o arrive.
	 *
	 * @return Tipo de maniobra, por ejemplo turn, depart o arrive
	 */
	public String getType() {
		return type;
	}

	/**
	 * Establece: tipo de maniobra, por ejemplo turn, depart o arrive.
	 *
	 * @param type Tipo de maniobra, por ejemplo turn, depart o arrive
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Obtiene: dirección del giro, por ejemplo left o right.
	 *
	 * @return Dirección del giro, por ejemplo left o right
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * Establece: dirección del giro, por ejemplo left o right.
	 *
	 * @param modifier Dirección del giro, por ejemplo left o right
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * Obtiene: salida que se toma en una glorieta.
	 *
	 * @return Salida que se toma en una glorieta
	 */
	public Integer getExit() {
		return exit;
	}

	/**
	 * Establece: salida que se toma en una glorieta.
	 *
	 * @param exit Salida que se toma en una glorieta
	 */
	public void setExit(Integer exit) {
		this.exit = exit;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(type, modifier, exit);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos ManiobraOsrmDTO son iguales si
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
		ManiobraOsrmDTO other = (ManiobraOsrmDTO) obj;
		return Objects.equals(type, other.type)
				&& Objects.equals(modifier, other.modifier)
				&& Objects.equals(exit, other.exit);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "ManiobraOsrm ["
				+ "type="
				+ type
				+ ", modifier="
				+ modifier
				+ ", exit="
				+ exit
				+ "]";
	}
}
