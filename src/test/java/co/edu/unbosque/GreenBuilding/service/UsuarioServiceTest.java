package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.CambioPasswordDTO;
import co.edu.unbosque.GreenBuilding.dto.CodigoDTO;
import co.edu.unbosque.GreenBuilding.dto.UsuarioDTO;
import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio UsuarioService.
 *
 * <p>Verifica el registro, la verificación de la cuenta, el cambio de contraseña, los intereses y
 * la gestión de usuarios por parte del administrador.
 *
 * @author Juan Arango
 * @version 1.0
 */
class UsuarioServiceTest {

	/** Correo de la residente de prueba. */
	private static final String CORREO = "ana@correo.com";

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de unidades. */
	@Mock private UnidadRepository unidadRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Repositorio simulado de asistencias. */
	@Mock private AsistenciaRepository asistenciaRepo;

	/** Servicio de códigos simulado. */
	@Mock private CodigoVerificacionService codigoServ;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio que se prueba. */
	@InjectMocks private UsuarioService usuarioServ;

	/** Codificador real de contraseñas. */
	private PasswordEncoder passwordEncoder;

	/**
	 * Inicializa los objetos simulados, un ModelMapper y un codificador BCrypt reales.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		passwordEncoder = new BCryptPasswordEncoder();
		ReflectionTestUtils.setField(usuarioServ, "modelMapper", new ModelMapper());
		ReflectionTestUtils.setField(usuarioServ, "passwordEncoder", passwordEncoder);
	}

	/**
	 * Crea los datos de registro de una residente del conjunto indicado.
	 *
	 * @param conjuntoId Id del conjunto
	 * @return DTO del registro
	 */
	private UsuarioDTO registro(Long conjuntoId) {
		UsuarioDTO dto = new UsuarioDTO(" Ana@Correo.com ", "Clave123*");
		dto.setNombre("Ana");
		dto.setApellido("Pérez");
		dto.setTelefono("3001234567");
		dto.setConjuntoId(conjuntoId);
		return dto;
	}

	/**
	 * Prueba que el registro guarde el correo en minúsculas, la contraseña cifrada y envíe el
	 * código.
	 */
	@Test
	void testCreateRegistraYEnviaCodigo() {
		Conjunto conjunto = new Conjunto();
		conjunto.setId(1L);
		conjunto.setNombre("Torres");
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.empty());
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));
		when(codigoServ.generarYEnviar(any(), eq(Proposito.VERIFICACION))).thenReturn(0);

		assertEquals(0, usuarioServ.create(registro(1L)), "Debe devolver 0");

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepo).save(captor.capture());
		assertEquals(CORREO, captor.getValue().getUsername(), "El correo debe quedar en minúsculas");
		assertNotEquals("Clave123*", captor.getValue().getPassword(), "La contraseña no debe quedar en texto plano");
		assertTrue(passwordEncoder.matches("Clave123*", captor.getValue().getPassword()), "Debe ser el hash BCrypt");
		assertEquals(Role.USER, captor.getValue().getRole(), "Debe registrarse como residente");
	}

	/**
	 * Prueba los códigos de correo repetido y conjunto inexistente.
	 */
	@Test
	void testCreateCodigosDeError() {
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(new Usuario()));
		assertEquals(1, usuarioServ.create(registro(1L)), "Un correo repetido debe devolver 1");

		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.empty());
		assertEquals(2, usuarioServ.create(registro(null)), "Sin conjunto debe devolver 2");
		verify(usuarioRepo, never()).save(any());
	}

	/**
	 * Prueba que el usuario devuelto nunca incluya la contraseña.
	 */
	@Test
	void testGetByUsernameNoDevuelveLaContrasena() {
		Usuario usuario = new Usuario(CORREO, passwordEncoder.encode("Clave123*"), Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));

		UsuarioDTO dto = usuarioServ.getByUsername(CORREO);
		assertEquals(CORREO, dto.getUsername(), "Debe devolver el correo");
		assertNull(dto.getPassword(), "No debe devolver la contraseña");
	}

	/**
	 * Prueba que verificar la cuenta con el código correcto la deje verificada.
	 */
	@Test
	void testVerificarCuenta() {
		Usuario usuario = new Usuario(CORREO, "hash", Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		when(codigoServ.validar(CORREO, "123456", Proposito.VERIFICACION)).thenReturn(0);

		assertEquals(0, usuarioServ.verificar(new CodigoDTO(CORREO, "123456")), "Debe devolver 0");
		assertTrue(usuario.isVerificado(), "La cuenta debe quedar verificada");
		assertEquals(1, usuarioServ.verificar(new CodigoDTO(CORREO, "123456")), "Verificar dos veces debe devolver 1");
	}

	/**
	 * Prueba que un código incorrecto devuelva el código de error del servicio de códigos.
	 */
	@Test
	void testVerificarCodigoIncorrecto() {
		Usuario usuario = new Usuario(CORREO, "hash", Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		when(codigoServ.validar(CORREO, "000000", Proposito.VERIFICACION)).thenReturn(2);

		assertEquals(2, usuarioServ.verificar(new CodigoDTO(CORREO, "000000")), "Debe devolver 2");
		assertFalse(usuario.isVerificado(), "La cuenta no debe quedar verificada");
	}

	/**
	 * Prueba los resultados de cambiar la contraseña.
	 */
	@Test
	void testChangePassword() {
		Usuario usuario = new Usuario(CORREO, passwordEncoder.encode("Clave123*"), Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));

		assertEquals(
				1,
				usuarioServ.changePassword(CORREO, new CambioPasswordDTO("Otra123*", "Nueva123*")),
				"Con la contraseña actual incorrecta debe devolver 1");
		assertEquals(
				2,
				usuarioServ.changePassword(CORREO, new CambioPasswordDTO("Clave123*", "Clave123*")),
				"Con la misma contraseña debe devolver 2");
		assertEquals(
				0,
				usuarioServ.changePassword(CORREO, new CambioPasswordDTO("Clave123*", "Nueva123*")),
				"Debe devolver 0");
		assertTrue(passwordEncoder.matches("Nueva123*", usuario.getPassword()), "Debe quedar la nueva contraseña");
	}

	/**
	 * Prueba que los intereses se guarden sin repetir y que uno inválido sea rechazado.
	 */
	@Test
	void testUpdateIntereses() {
		Usuario usuario = new Usuario(CORREO, "hash", Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		List<String> intereses = new ArrayList<>();
		intereses.add("deporte");
		intereses.add(" BBQ ");
		intereses.add("DEPORTE");

		assertEquals(0, usuarioServ.updateIntereses(CORREO, intereses), "Debe devolver 0");
		assertEquals("DEPORTE,BBQ", usuario.getInteresesGuardados(), "Deben quedar sin repetir");

		intereses.add("VIDEOJUEGOS");
		assertEquals(1, usuarioServ.updateIntereses(CORREO, intereses), "Un interés inválido debe devolver 1");
	}

	/**
	 * Prueba que el administrador asigne una unidad y que no se pueda asignar a otro administrador.
	 */
	@Test
	void testAsignarUnidad() {
		Usuario residente = new Usuario(CORREO, "hash", Role.USER);
		Usuario admin = new Usuario("admin@correo.com", "hash", Role.ADMIN);
		Unidad unidad = new Unidad(1L, Unidad.Tipo.APARTAMENTO, "Torre 1", "101");
		unidad.setId(5L);
		when(usuarioRepo.findById(10L)).thenReturn(Optional.of(residente));
		when(usuarioRepo.findById(11L)).thenReturn(Optional.of(admin));
		when(unidadRepo.findById(5L)).thenReturn(Optional.of(unidad));

		assertEquals(0, usuarioServ.asignarUnidad(10L, 5L), "Debe devolver 0");
		assertEquals(5L, residente.getUnidadId(), "Debe quedar con la unidad");
		assertEquals(1L, residente.getConjuntoId(), "Debe quedar en el conjunto de la unidad");
		assertEquals(3, usuarioServ.asignarUnidad(11L, 5L), "A un administrador debe devolver 3");
	}

	/**
	 * Prueba que no se pueda eliminar ni desactivar a un administrador.
	 */
	@Test
	void testAdministradorProtegido() {
		Usuario admin = new Usuario("admin@correo.com", "hash", Role.ADMIN);
		when(usuarioRepo.findById(11L)).thenReturn(Optional.of(admin));

		assertEquals(2, usuarioServ.deleteById(11L), "Eliminar un administrador debe devolver 2");
		assertEquals(3, usuarioServ.cambiarEstado(11L, false), "Desactivar un administrador debe devolver 3");
		verify(usuarioRepo, never()).delete(any());
	}

	/**
	 * Prueba que validar credenciales acepte la contraseña correcta y rechace la incorrecta.
	 */
	@Test
	void testValidateCredentials() {
		Usuario usuario = new Usuario(CORREO, passwordEncoder.encode("Clave123*"), Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		when(usuarioRepo.findByUsername("nadie@correo.com")).thenReturn(Optional.empty());

		assertEquals(0, usuarioServ.validateCredentials(CORREO, "Clave123*"), "Debe devolver 0");
		assertEquals(1, usuarioServ.validateCredentials(CORREO, "mala"), "Una clave incorrecta debe devolver 1");
		assertEquals(1, usuarioServ.validateCredentials("nadie@correo.com", "x"), "Un usuario inexistente debe devolver 1");
	}

	/**
	 * Prueba que el quinto intento fallido seguido bloquee el inicio de sesión y reinicie el conteo.
	 */
	@Test
	void testRegistrarIntentoFallidoBloquea() {
		Usuario usuario = new Usuario(CORREO, "hash", Role.USER);
		usuario.setIntentosFallidos(UsuarioService.MAX_INTENTOS_LOGIN - 2);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		when(usuarioRepo.findByUsername("nadie@correo.com")).thenReturn(Optional.empty());

		assertEquals(0, usuarioServ.registrarIntentoFallido(CORREO), "El cuarto intento solo debe sumarse");
		assertEquals(4, usuario.getIntentosFallidos(), "Debe llevar 4 intentos");
		assertNull(usuario.getBloqueadoHasta(), "Aún no debe estar bloqueado");

		assertEquals(1, usuarioServ.registrarIntentoFallido(CORREO), "El quinto intento debe devolver 1");
		assertEquals(0, usuario.getIntentosFallidos(), "El conteo debe volver a cero");
		assertTrue(usuario.getBloqueadoHasta().isAfter(LocalDateTime.now()), "Debe quedar bloqueado");
		assertEquals(2, usuarioServ.registrarIntentoFallido("nadie@correo.com"), "Un usuario inexistente debe devolver 2");
	}

	/**
	 * Prueba que el bloqueo solo cuente mientras no haya pasado su hora de fin.
	 */
	@Test
	void testLoginBloqueado() {
		Usuario usuario = new Usuario(CORREO, "hash", Role.USER);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		when(usuarioRepo.findByUsername("nadie@correo.com")).thenReturn(Optional.empty());

		assertFalse(usuarioServ.loginBloqueado(CORREO), "Sin bloqueo debe devolver false");
		usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(10));
		assertTrue(usuarioServ.loginBloqueado(CORREO), "Con un bloqueo vigente debe devolver true");
		usuario.setBloqueadoHasta(LocalDateTime.now().minusMinutes(1));
		assertFalse(usuarioServ.loginBloqueado(CORREO), "Un bloqueo vencido no debe contar");
		assertFalse(usuarioServ.loginBloqueado("nadie@correo.com"), "Un usuario inexistente no está bloqueado");
	}

	/**
	 * Prueba que un inicio de sesión exitoso borre los intentos y el bloqueo.
	 */
	@Test
	void testReiniciarIntentos() {
		Usuario usuario = new Usuario(CORREO, "hash", Role.USER);
		usuario.setIntentosFallidos(3);
		usuario.setBloqueadoHasta(LocalDateTime.now().minusMinutes(1));
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(usuario));
		when(usuarioRepo.findByUsername("nadie@correo.com")).thenReturn(Optional.empty());

		assertEquals(0, usuarioServ.reiniciarIntentos(CORREO), "Debe devolver 0");
		assertEquals(0, usuario.getIntentosFallidos(), "Debe borrar los intentos");
		assertNull(usuario.getBloqueadoHasta(), "Debe quitar el bloqueo");
		verify(usuarioRepo).save(usuario);
		assertEquals(1, usuarioServ.reiniciarIntentos("nadie@correo.com"), "Un usuario inexistente debe devolver 1");
	}
}
