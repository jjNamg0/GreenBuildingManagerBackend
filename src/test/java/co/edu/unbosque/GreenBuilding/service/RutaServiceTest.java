package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio RutaService.
 *
 * <p>Verifica los modos de transporte aceptados y que una falla del servidor de rutas no rompa la
 * aplicación. No hace llamadas reales a internet.
 *
 * @author Juan Arango
 * @version 1.0
 */
class RutaServiceTest {

	/** Servicio que se prueba. */
	private RutaService rutaServ;

	/**
	 * Crea el servicio antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		rutaServ = new RutaService();
	}

	/**
	 * Prueba que solo se acepten los modos CARRO, CAMINANDO y BICICLETA.
	 */
	@Test
	void testEsModoValido() {
		assertTrue(rutaServ.esModoValido("CARRO"), "CARRO debe ser válido");
		assertTrue(rutaServ.esModoValido("CAMINANDO"), "CAMINANDO debe ser válido");
		assertTrue(rutaServ.esModoValido("BICICLETA"), "BICICLETA debe ser válido");
		assertFalse(rutaServ.esModoValido("AVION"), "AVION no debe ser válido");
		assertFalse(rutaServ.esModoValido(null), "Un modo nulo no debe ser válido");
	}

	/**
	 * Prueba que si el servidor de rutas no responde se devuelva null.
	 */
	@Test
	void testCalcularSinServidor() {
		ReflectionTestUtils.setField(rutaServ, "urlBase", "http://127.0.0.1:1");
		ReflectionTestUtils.setField(rutaServ, "apiKey", "");
		assertNull(rutaServ.calcular(4.65, -74.05, 4.86, -74.03, "CARRO"), "Sin servidor debe devolver null");
	}
}
