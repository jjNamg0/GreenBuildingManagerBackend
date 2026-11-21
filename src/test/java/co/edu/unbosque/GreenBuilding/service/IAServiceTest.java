package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import co.edu.unbosque.GreenBuilding.dto.ChatDTO;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio IAService.
 *
 * <p>Verifica las reglas del asistente antes de llamar a la API: mensaje vacío, largo máximo y
 * pausa entre preguntas. No hace llamadas reales a internet.
 *
 * @author Juan Arango
 * @version 1.0
 */
class IAServiceTest {

	/** Servicio que se prueba. */
	private IAService iaServ;

	/**
	 * Crea el servicio y le asigna los límites que normalmente vienen de las propiedades.
	 */
	@BeforeEach
	void setUp() {
		iaServ = new IAService();
		ReflectionTestUtils.setField(iaServ, "maxCaracteres", 20);
		ReflectionTestUtils.setField(iaServ, "maxHistorial", 6);
		ReflectionTestUtils.setField(iaServ, "pausaMs", 60000L);
		ReflectionTestUtils.setField(iaServ, "modelo", "modelo-de-prueba");
	}

	/**
	 * Prueba que un mensaje vacío devuelva 1.
	 */
	@Test
	void testValidarMensajeVacio() {
		assertEquals(1, iaServ.validar("ana@correo.com", new ChatDTO("   ", null)), "Debe devolver 1");
	}

	/**
	 * Prueba que un mensaje más largo que el máximo devuelva 2.
	 */
	@Test
	void testValidarMensajeMuyLargo() {
		ChatDTO chat = new ChatDTO("Este mensaje tiene más de veinte letras", null);
		assertEquals(2, iaServ.validar("ana@correo.com", chat), "Debe devolver 2");
	}

	/**
	 * Prueba que la primera pregunta se acepte y la segunda inmediata devuelva 3.
	 */
	@Test
	void testValidarPausaEntrePreguntas() {
		ChatDTO chat = new ChatDTO("¿Cómo reservo?", new ArrayList<>());
		assertEquals(0, iaServ.validar("ana@correo.com", chat), "La primera pregunta debe aceptarse");
		assertEquals(3, iaServ.validar("ana@correo.com", chat), "La segunda inmediata debe devolver 3");
		assertEquals(0, iaServ.validar("luis@correo.com", chat), "Otro usuario no debe esperar");
	}

	/**
	 * Prueba que sin API key el asistente no responda.
	 */
	@Test
	void testResponderSinApiKey() {
		ReflectionTestUtils.setField(iaServ, "apiKey", "");
		assertNull(iaServ.responder(new ChatDTO("Hola", null)), "Sin API key debe devolver null");
	}

	/**
	 * Prueba que si el servidor de IA no responde se devuelva null.
	 */
	@Test
	void testResponderSinServidor() {
		ReflectionTestUtils.setField(iaServ, "apiKey", "llave-de-prueba");
		ReflectionTestUtils.setField(iaServ, "url", "http://127.0.0.1:1/v1/chat/completions");
		assertNull(iaServ.responder(new ChatDTO("Hola", null)), "Sin servidor debe devolver null");
	}
}
