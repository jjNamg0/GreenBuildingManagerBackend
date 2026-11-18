package co.edu.unbosque.GreenBuilding.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para la aplicación. Define la cadena de filtros,
 * las reglas de acceso por rol, el manejo de errores 401 y 403, y los beans de
 * autenticación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/** Rol de administrador. */
	private static final String ADMIN = "ADMIN";

	/** Rol de residente. */
	private static final String USER = "USER";

	/** Filtro de autenticación JWT. */
	private final JwtAuthenticationFilter jwtAuthFilter;

	/** Servicio para cargar detalles de usuario. */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor que inicializa la configuración con las dependencias necesarias.
	 *
	 * @param jwtAuthFilter      Filtro de autenticación JWT
	 * @param userDetailsService Servicio para cargar detalles de usuario
	 */
	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Configura la cadena de filtros de seguridad. Deshabilita CSRF, habilita CORS
	 * con la configuración de los controladores, define las reglas por rol,
	 * responde 401 cuando falta el token o venció y 403 cuando el rol no tiene
	 * permiso.
	 *
	 * @param http Objeto HttpSecurity para configurar la seguridad
	 * @return Cadena de filtros de seguridad configurada
	 * @throws Exception Si ocurre un error durante la configuración
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers("/auth/**", "/conjunto/getall", "/ruta/**", "/recomendacion/getconjuntoscercanos",
						"/error")
				.permitAll().requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
				.requestMatchers("/user/getall", "/user/count", "/user/exists/**", "/user/getbyid/**",
						"/user/updatejson", "/user/asignarunidad", "/user/cambiarestado", "/user/deletebyid/**")
				.hasRole(ADMIN).requestMatchers("/user/updateintereses").hasRole(USER)
				.requestMatchers("/conjunto/createjson", "/conjunto/updatejson", "/conjunto/deletebyid/**")
				.hasRole(ADMIN).requestMatchers("/unidad/**").hasRole(ADMIN)
				.requestMatchers("/espacio/getall", "/espacio/createjson", "/espacio/updatejson",
						"/espacio/deletebyid/**")
				.hasRole(ADMIN)
				.requestMatchers("/reserva/getall", "/reserva/count", "/reserva/getbyid/**", "/reserva/aprobar/**",
						"/reserva/rechazar/**")
				.hasRole(ADMIN)
				.requestMatchers("/reserva/getmine", "/reserva/getdisponibilidad", "/reserva/createjson",
						"/reserva/cancelar/**")
				.hasRole(USER)
				.requestMatchers("/evento/getall", "/evento/count", "/evento/createjson", "/evento/updatejson",
						"/evento/cancelar/**", "/evento/deletebyid/**", "/evento/enviarinvitacion/**",
						"/evento/vistaprevia")
				.hasRole(ADMIN).requestMatchers("/evento/asistir/**", "/evento/noasistir/**").hasRole(USER)
				.requestMatchers("/recomendacion/**").hasRole(USER).requestMatchers("/auditoria/**", "/resumen/**")
				.hasRole(ADMIN).anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(errores -> errores.authenticationEntryPoint((request, response, e) -> {
					response.setStatus(401);
					response.setContentType("text/plain;charset=UTF-8");
					response.getOutputStream().write("Tu sesión expiró. Inicia sesión de nuevo.".getBytes());
				}).accessDeniedHandler((request, response, e) -> {
					response.setStatus(403);
					response.setContentType("text/plain;charset=UTF-8");
					response.getOutputStream().write("No tienes permisos para realizar esta acción.".getBytes());
				})).authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Configura el proveedor de autenticación. Utiliza DaoAuthenticationProvider
	 * con el servicio de detalles de usuario y el codificador de contraseñas.
	 *
	 * @return Proveedor de autenticación configurado
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * Configura el gestor de autenticación.
	 *
	 * @param config Configuración de autenticación
	 * @return Gestor de autenticación configurado
	 * @throws Exception Si ocurre un error durante la configuración
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Configura el codificador de contraseñas. Utiliza BCrypt para codificar
	 * contraseñas de forma segura.
	 *
	 * @return Codificador de contraseñas configurado
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
