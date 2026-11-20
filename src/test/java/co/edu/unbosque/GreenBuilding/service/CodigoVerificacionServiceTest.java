package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion;
import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.CodigoVerificacionRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Clase de prueba para el servicio CodigoVerificacionService.
 *
 * <p>Verifica la generación de códigos de seis dígitos y cada resultado de la validación.
 *
 * @author Juan Arango
 * @version 1.0
 */
class CodigoVerificacionServiceTest {

	/** Correo del usuario de prueba. */
	private static final String CORREO = "ana@correo.com";

	/** Repositorio simulado de códigos. */
	@Mock private CodigoVerificacionRepository codigoRepo;

	/** Servicio de correo simulado. */
	@Mock private CorreoService correoServ;

	/** Servicio que se prueba. */
	@InjectMocks private CodigoVerificacionService codigoServ;

	/** Usuario de prueba. */
	private Usuario usuario;

	/**
	 * Inicializa los objetos simulados y el usuario antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		usuario = new Usuario(CORREO, "clave", Role.USER);
	}

	/**
	 * Prueba que se guarde un código de seis dígitos y se envíe por correo.
	 */
	@Test
	void testGenerarYEnviarGuardaCodigoDeSeisDigitos() {
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.empty());
		when(correoServ.enviarCodigo(eq(usuario), anyString(), eq(Proposito.VERIFICACION))).thenReturn(true);

		assertEquals(0, codigoServ.generarYEnviar(usuario, Proposito.VERIFICACION), "Debe devolver 0");

		ArgumentCaptor<CodigoVerificacion> captor = ArgumentCaptor.forClass(CodigoVerificacion.class);
		verify(codigoRepo).save(captor.capture());
		assertTrue(captor.getValue().getCodigo().matches("\\d{6}"), "El código debe tener 6 dígitos");
	}

	/**
	 * Prueba que no se envíe otro código si el anterior se pidió hace menos de un minuto.
	 */
	@Test
	void testGenerarYEnviarRechazaSiSePidioHacePoco() {
		CodigoVerificacion reciente =
				new CodigoVerificacion(CORREO, "123456", Proposito.VERIFICACION, LocalDateTime.now().plusMinutes(15));
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.of(reciente));

		assertEquals(1, codigoServ.generarYEnviar(usuario, Proposito.VERIFICACION), "Debe devolver 1");
		verify(codigoRepo, never()).save(any());
	}

	/**
	 * Prueba que se devuelva 2 cuando el correo no se pudo enviar.
	 */
	@Test
	void testGenerarYEnviarDevuelveDosSiFallaElCorreo() {
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.RESTABLECER))
				.thenReturn(Optional.empty());
		when(correoServ.enviarCodigo(any(), anyString(), any())).thenReturn(false);

		assertEquals(2, codigoServ.generarYEnviar(usuario, Proposito.RESTABLECER), "Debe devolver 2");
	}

	/**
	 * Prueba que un código correcto y vigente sea aceptado.
	 */
	@Test
	void testValidarCodigoCorrecto() {
		CodigoVerificacion codigo =
				new CodigoVerificacion(CORREO, "654321", Proposito.VERIFICACION, LocalDateTime.now().plusMinutes(10));
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.of(codigo));

		assertEquals(0, codigoServ.validar(CORREO, " 654321 ", Proposito.VERIFICACION), "Debe devolver 0");
	}

	/**
	 * Prueba que un código incorrecto sume un intento y devuelva 2.
	 */
	@Test
	void testValidarCodigoIncorrectoSumaIntento() {
		CodigoVerificacion codigo =
				new CodigoVerificacion(CORREO, "654321", Proposito.VERIFICACION, LocalDateTime.now().plusMinutes(10));
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.of(codigo));

		assertEquals(2, codigoServ.validar(CORREO, "000000", Proposito.VERIFICACION), "Debe devolver 2");
		assertEquals(1, codigo.getIntentos(), "Debe sumar un intento");
		verify(codigoRepo).save(codigo);
	}

	/**
	 * Prueba que un código vencido devuelva 3.
	 */
	@Test
	void testValidarCodigoVencido() {
		CodigoVerificacion codigo =
				new CodigoVerificacion(CORREO, "654321", Proposito.VERIFICACION, LocalDateTime.now().minusMinutes(1));
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.of(codigo));

		assertEquals(3, codigoServ.validar(CORREO, "654321", Proposito.VERIFICACION), "Debe devolver 3");
	}

	/**
	 * Prueba que un código con los intentos agotados devuelva 4.
	 */
	@Test
	void testValidarSinIntentos() {
		CodigoVerificacion codigo =
				new CodigoVerificacion(CORREO, "654321", Proposito.VERIFICACION, LocalDateTime.now().plusMinutes(10));
		codigo.setIntentos(CodigoVerificacionService.INTENTOS_MAXIMOS);
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.of(codigo));

		assertEquals(4, codigoServ.validar(CORREO, "654321", Proposito.VERIFICACION), "Debe devolver 4");
	}

	/**
	 * Prueba que se devuelva 5 cuando no hay un código activo.
	 */
	@Test
	void testValidarSinCodigo() {
		when(codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(CORREO, Proposito.VERIFICACION))
				.thenReturn(Optional.empty());

		assertEquals(5, codigoServ.validar(CORREO, "654321", Proposito.VERIFICACION), "Debe devolver 5");
	}
}
