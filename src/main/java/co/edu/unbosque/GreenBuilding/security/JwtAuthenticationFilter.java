package co.edu.unbosque.GreenBuilding.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por cada solicitud HTTP.
 * Este filtro extrae el token JWT del encabezado de autorización, lo valida y
 * establece la autenticación en el contexto de seguridad de Spring si el token
 * es válido y la cuenta está verificada y activa.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/** Utilidad para operaciones con tokens JWT. */
	private final JwtUtil jwtUtil;

	/** Servicio para cargar detalles de usuario. */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor que inicializa el filtro con las dependencias necesarias.
	 *
	 * @param jwtUtil            Utilidad para operaciones con tokens JWT
	 * @param userDetailsService Servicio para cargar detalles de usuario
	 */
	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Método principal del filtro que procesa cada solicitud HTTP. Extrae y valida
	 * el token JWT, y establece la autenticación si el token es válido.
	 *
	 * @param request     Solicitud HTTP
	 * @param response    Respuesta HTTP
	 * @param filterChain Cadena de filtros
	 * @throws ServletException Si ocurre un error en el servlet
	 * @throws IOException      Si ocurre un error de entrada/salida
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			try {
				username = jwtUtil.extractUsername(jwt);
			} catch (Exception e) {
				logger.debug("Token rechazado: " + e.getMessage());
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

				if (jwtUtil.validateToken(jwt, userDetails) && userDetails.isEnabled()
						&& userDetails.isAccountNonLocked()) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			} catch (UsernameNotFoundException e) {
				logger.debug("El usuario del token ya no existe: " + username);
			}
		}

		filterChain.doFilter(request, response);
	}
}
