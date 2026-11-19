package co.edu.unbosque.GreenBuilding.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI (Swagger) para documentar la API. Define la
 * información general y el esquema de seguridad JWT.
 */
@Configuration
public class OpenApiConfig {

	/** Constructor por defecto. */
	public OpenApiConfig() {
	}

	/**
	 * Crea la configuración de OpenAPI con la descripción de la API y el esquema
	 * bearerAuth.
	 *
	 * @return Configuración de OpenAPI
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		String mainDescription = "<h2>API de Green Building Manager</h2><p>Gestión de conjuntos residenciales de"
				+ " Cundinamarca: unidades, espacios comunes, reservas, eventos, recomendaciones, rutas y"
				+ " auditoría.</p><h3>Flujo básico de uso:</h3><ol>    <li>Regístrate con"
				+ " <code>/auth/register</code> y verifica el correo con <code>/auth/verify</code></li>"
				+ "    <li>Inicia sesión con <code>/auth/login</code> para obtener un token JWT</li>   "
				+ " <li>Haz clic en <strong>Authorize</strong> y escribe el token</li></ol><h3>Roles de"
				+ " usuario:</h3><ul>    <li><strong>USER</strong>: residente; reserva espacios y se"
				+ " inscribe a eventos de su conjunto</li>    <li><strong>ADMIN</strong>: administra"
				+ " conjuntos, unidades, espacios, eventos, reservas y usuarios</li></ul><h3>Códigos de"
				+ " estado HTTP comunes:</h3><ul>    <li><strong>200/201/202</strong>: Operación"
				+ " exitosa</li>    <li><strong>204</strong>: Lista vacía</li>   "
				+ " <li><strong>400</strong>: Datos inválidos</li>    <li><strong>401</strong>: No"
				+ " autenticado (token inválido o expirado)</li>    <li><strong>403</strong>: Sin"
				+ " permisos</li>    <li><strong>404</strong>: Recurso no encontrado</li>   "
				+ " <li><strong>409</strong>: Conflicto (registro repetido u horario ocupado)</li></ul>";
		String securityDescription = "Autenticación mediante JWT (JSON Web Token)."
				+ "<p>Obtén el token en <code>/auth/login</code>, haz clic en Authorize y pégalo.</p>";
		Info info = new Info().title("API de Green Building Manager").version("1.0").description(mainDescription)
				.contact(new Contact().name("Equipo Green Building"));
		SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
				.bearerFormat("JWT").description(securityDescription);
		return new OpenAPI().info(info).components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
	}
}
