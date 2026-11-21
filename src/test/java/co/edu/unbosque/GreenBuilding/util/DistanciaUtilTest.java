package co.edu.unbosque.GreenBuilding.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la utilidad DistanciaUtil.
 *
 * <p>Verifica el cálculo de distancias entre coordenadas y el redondeo a un decimal.
 *
 * @author Juan Arango
 * @version 1.0
 */
class DistanciaUtilTest {

	/**
	 * Prueba que la distancia entre un punto y sí mismo sea cero.
	 */
	@Test
	void testKilometrosEntreElMismoPuntoEsCero() {
		assertEquals(0.0, DistanciaUtil.kilometros(4.65, -74.05, 4.65, -74.05), 0.0001, "Debe ser cero");
	}

	/**
	 * Prueba que la distancia entre Bogotá y Chía esté cerca de los 28 kilómetros.
	 */
	@Test
	void testKilometrosEntreBogotaYChia() {
		double distancia = DistanciaUtil.kilometros(4.6097, -74.0817, 4.8619, -74.0328);
		assertTrue(distancia > 25 && distancia < 32, "La distancia debe estar entre 25 y 32 km");
	}

	/**
	 * Prueba que redondear deje un solo decimal.
	 */
	@Test
	void testRedondearDejaUnDecimal() {
		assertEquals(12.3, DistanciaUtil.redondear(12.34), 0.0001, "12.34 debe quedar en 12.3");
		assertEquals(12.4, DistanciaUtil.redondear(12.35), 0.0001, "12.35 debe quedar en 12.4");
	}
}
