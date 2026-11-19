package co.edu.unbosque.GreenBuilding.security;

import co.edu.unbosque.GreenBuilding.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Clase de utilidad para la gestión de tokens JWT (JSON Web Token). Proporciona métodos para
 * generar, validar y extraer información de tokens JWT.
 */
@Component
public class JwtUtil {

	/** Tiempo de validez del token en milisegundos (24 horas). */
	private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

	/** Clave secreta utilizada para firmar los tokens JWT. Se obtiene del archivo de propiedades. */
	@Value("${jwt.secret:defaultSecretKeyWhichShouldBeAtLeast32CharactersLong}")
	private String secret;

	/** Constructor por defecto. */
	public JwtUtil() {}

	/**
	 * Obtiene la clave de firma para los tokens JWT.
	 *
	 * @return Clave de firma generada a partir del secreto
	 */
	private Key getSigningKey() {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Extrae el nombre de usuario del token JWT.
	 *
	 * @param token Token JWT
	 * @return Nombre de usuario contenido en el token
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae la fecha de expiración del token JWT.
	 *
	 * @param token Token JWT
	 * @return Fecha de expiración del token
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extrae el rol del usuario del token JWT.
	 *
	 * @param token Token JWT
	 * @return Rol del usuario contenido en el token
	 */
	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	/**
	 * Extrae una reclamación específica del token JWT.
	 *
	 * @param <T> Tipo de la reclamación a extraer
	 * @param token Token JWT
	 * @param claimsResolver Función para resolver la reclamación
	 * @return Valor de la reclamación
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extrae todas las reclamaciones del token JWT.
	 *
	 * @param token Token JWT
	 * @return Todas las reclamaciones contenidas en el token
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Verifica si el token JWT ha expirado.
	 *
	 * @param token Token JWT
	 * @return true si el token ha expirado, false en caso contrario
	 */
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Genera un token JWT para un usuario. Incluye el rol del usuario en las reclamaciones.
	 *
	 * @param userDetails Detalles del usuario para el que se genera el token
	 * @return Token JWT generado
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		if (userDetails instanceof Usuario) {
			Usuario usuario = (Usuario) userDetails;
			claims.put("role", usuario.getRole().name());
		}
		return createToken(claims, userDetails.getUsername());
	}

	/**
	 * Crea un token JWT con las reclamaciones y el sujeto especificados.
	 *
	 * @param claims Reclamaciones a incluir en el token
	 * @param subject Sujeto del token (nombre de usuario)
	 * @return Token JWT creado
	 */
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Valida un token JWT para un usuario específico.
	 *
	 * @param token Token JWT a validar
	 * @param userDetails Detalles del usuario para validar el token
	 * @return true si el token es válido para el usuario, false en caso contrario
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
