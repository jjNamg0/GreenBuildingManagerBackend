package co.edu.unbosque.GreenBuilding;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Inicializador de Servlet para desplegar la aplicación como archivo WAR en un servidor de
 * aplicaciones externo.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Configura la aplicación especificando la clase principal de la aplicación.
	 *
	 * @param application El SpringApplicationBuilder a configurar
	 * @return El SpringApplicationBuilder configurado
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GreenBuildingApplication.class);
	}
}
