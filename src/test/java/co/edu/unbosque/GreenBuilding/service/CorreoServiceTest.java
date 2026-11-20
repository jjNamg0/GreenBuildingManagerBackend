package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio CorreoService.
 *
 * <p>Verifica el envío simulado cuando no hay servidor de correo y el envío real con un enviador
 * simulado.
 *
 * @author Juan Arango
 * @version 1.0
 */
class CorreoServiceTest {

	/** Enviador de correos simulado. */
	@Mock private JavaMailSender enviador;

	/** Servicio de plantillas simulado. */
	@Mock private PlantillaCorreoService plantillaServ;

	/** Servicio que se prueba. */
	@InjectMocks private CorreoService correoServ;

	/**
	 * Inicializa los objetos simulados y el remitente antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(correoServ, "remitente", "managergreenbuilding@gmail.com");
	}

	/**
	 * Prueba que sin servidor configurado el correo se simule y no se use el enviador.
	 */
	@Test
	void testEnviarHtmlSinServidorSimulaElEnvio() {
		ReflectionTestUtils.setField(correoServ, "servidor", "");

		assertFalse(correoServ.estaConfigurado(), "No debe estar configurado");
		assertTrue(correoServ.enviarHtml("ana@correo.com", "Asunto", "<p>Hola</p>"), "Debe simular el envío");
		verify(enviador, never()).send(any(MimeMessage.class));
	}

	/**
	 * Prueba que con servidor configurado el correo se envíe con el enviador.
	 */
	@Test
	void testEnviarHtmlConServidorUsaElEnviador() {
		ReflectionTestUtils.setField(correoServ, "servidor", "smtp.gmail.com");
		when(enviador.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

		assertTrue(correoServ.estaConfigurado(), "Debe estar configurado");
		assertTrue(correoServ.enviarHtml("ana@correo.com", "Asunto", "<p>Hola</p>"), "Debe enviar el correo");
		verify(enviador).send(any(MimeMessage.class));
	}

	/**
	 * Prueba que se devuelva false cuando el servidor de correo falla.
	 */
	@Test
	void testEnviarHtmlDevuelveFalseSiFallaElServidor() {
		ReflectionTestUtils.setField(correoServ, "servidor", "smtp.gmail.com");
		when(enviador.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
		doThrow(new MailSendException("sin conexión")).when(enviador).send(any(MimeMessage.class));

		assertFalse(correoServ.enviarHtml("ana@correo.com", "Asunto", "<p>Hola</p>"), "Debe devolver false");
	}
}
