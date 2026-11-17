package co.edu.unbosque.GreenBuilding.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO con la respuesta del servidor de rutas OSRM. Los nombres de los campos son los mismos del
 * JSON de OSRM para que Gson lo convierta directamente.
 */
public class RespuestaOsrmDTO {

	/** Resultado de la consulta: "Ok" si encontró ruta. */
	private String code;

	/** Rutas encontradas; se usa la primera. */
	private List<RutaOsrmDTO> routes;

	/** Constructor por defecto. Crea una instancia vacía de RespuestaOsrmDTO. */
	public RespuestaOsrmDTO() {}

	/**
	 * Constructor con code, routes.
	 *
	 * @param code Resultado de la consulta: "Ok" si encontró ruta.
	 * @param routes Rutas encontradas; se usa la primera.
	 */
	public RespuestaOsrmDTO(String code, List<RutaOsrmDTO> routes) {
		this.code = code;
		this.routes = routes;
	}

	/**
	 * Obtiene: resultado de la consulta: "Ok" si encontró ruta.
	 *
	 * @return Resultado de la consulta: "Ok" si encontró ruta
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Establece: resultado de la consulta: "Ok" si encontró ruta.
	 *
	 * @param code Resultado de la consulta: "Ok" si encontró ruta
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Obtiene: rutas encontradas; se usa la primera.
	 *
	 * @return Rutas encontradas; se usa la primera
	 */
	public List<RutaOsrmDTO> getRoutes() {
		return routes;
	}

	/**
	 * Establece: rutas encontradas; se usa la primera.
	 *
	 * @param routes Rutas encontradas; se usa la primera
	 */
	public void setRoutes(List<RutaOsrmDTO> routes) {
		this.routes = routes;
	}

	/**
	 * Calcula el código hash para este objeto. Utilizado para almacenar objetos en colecciones
	 * basadas en hash.
	 *
	 * @return Código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(code, routes);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos RespuestaOsrmDTO son iguales si
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
		RespuestaOsrmDTO other = (RespuestaOsrmDTO) obj;
		return Objects.equals(code, other.code)
				&& Objects.equals(routes, other.routes);
	}

	/**
	 * Devuelve una representación en cadena de texto de este objeto. Útil para depuración y registro.
	 *
	 * @return Representación en cadena de texto del objeto
	 */
	@Override
	public String toString() {
		return "RespuestaOsrm ["
				+ "code="
				+ code
				+ ", routes="
				+ routes
				+ "]";
	}
}
