package co.edu.unbosque.GreenBuilding.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el componente JwtUtil.
 *
 * <p>Verifica que el token JWT guarde el correo y el rol del usuario y que se valide correctamente.
 *
 * @author Juan Arango
 * @version 1.0
 */
class JwtUtilTest {

	/** Componente que se prueba. */
	private JwtUtil jwtUtil;

	/**
	 * Crea el componente y le asigna la clave secreta que normalmente viene de las propiedades.
	 */
	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil();
		ReflectionTestUtils.setField(jwtUtil, "secret", "claveDePruebaParaLosTokensDeGreenBuilding2026");
	}

	/**
	 * Prueba que el rol del usuario quede dentro del token y se pueda leer.
	 */
	@Test
	void testRoleIsIncludedInJwtToken() {
		Usuario usuario = new Usuario("admin@correo.com", "clave", Role.ADMIN);
		String token = jwtUtil.generateToken(usuario);
		assertEquals("ADMIN", jwtUtil.extractRole(token), "El rol debe ser ADMIN");
		assertEquals("admin@correo.com", jwtUtil.extractUsername(token), "El correo debe ser el del usuario");
	}

	/**
	 * Prueba que un usuario que no es de la aplicación genere un token sin rol.
	 */
	@Test
	void testTokenSinRolParaOtroTipoDeUsuario() {
		UserDetails externo = User.withUsername("otro@correo.com").password("x").roles("USER").build();
		String token = jwtUtil.generateToken(externo);
		assertNull(jwtUtil.extractRole(token), "El token no debe tener rol");
	}

	/**
	 * Prueba que el token sea válido para su dueño y no para otro usuario.
	 */
	@Test
	void testValidateToken() {
		Usuario dueno = new Usuario("ana@correo.com", "clave", Role.USER);
		Usuario otro = new Usuario("luis@correo.com", "clave", Role.USER);
		String token = jwtUtil.generateToken(dueno);
		assertTrue(jwtUtil.validateToken(token, dueno), "El token debe ser válido para su dueño");
		assertFalse(jwtUtil.validateToken(token, otro), "El token no debe ser válido para otro usuario");
	}
}
