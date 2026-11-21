package co.edu.unbosque.GreenBuilding.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la utilidad FechaUtil.
 *
 * <p>Verifica que las fechas y horas se conviertan a texto y se lean desde texto correctamente.
 *
 * @author Juan Arango
 * @version 1.0
 */
class FechaUtilTest {

	/**
	 * Prueba que la hora se escriba con el formato HH:mm y que null devuelva null.
	 */
	@Test
	void testHoraUsaFormatoDeVeinticuatroHoras() {
		assertEquals("07:05", FechaUtil.hora(LocalTime.of(7, 5)), "La hora debe ser 07:05");
		assertNull(FechaUtil.hora(null), "Una hora nula debe devolver null");
	}

	/**
	 * Prueba que la fecha se escriba con el formato AAAA-MM-DD.
	 */
	@Test
	void testFechaUsaFormatoIso() {
		assertEquals("2026-11-20", FechaUtil.fecha(LocalDate.of(2026, 11, 20)), "La fecha debe ser ISO");
		assertNull(FechaUtil.fecha(null), "Una fecha nula debe devolver null");
	}

	/**
	 * Prueba que la fecha y hora se escriban sin nanosegundos.
	 */
	@Test
	void testFechaHoraQuitaNanosegundos() {
		LocalDateTime momento = LocalDateTime.of(2026, 11, 20, 10, 30, 15, 999);
		assertEquals("2026-11-20T10:30:15", FechaUtil.fechaHora(momento), "No debe tener nanosegundos");
		assertNull(FechaUtil.fechaHora(null), "Un momento nulo debe devolver null");
	}

	/**
	 * Prueba que leerHora convierta un texto válido y devuelva null con un texto inválido.
	 */
	@Test
	void testLeerHoraConTextoValidoEInvalido() {
		assertEquals(LocalTime.of(18, 45), FechaUtil.leerHora("18:45"), "Debe leer 18:45");
		assertNull(FechaUtil.leerHora("25:99"), "Una hora inválida debe devolver null");
		assertNull(FechaUtil.leerHora(null), "Un texto nulo debe devolver null");
	}

	/**
	 * Prueba que leerFecha convierta un texto válido y devuelva null con un texto inválido.
	 */
	@Test
	void testLeerFechaConTextoValidoEInvalido() {
		assertEquals(LocalDate.of(2026, 2, 28), FechaUtil.leerFecha("2026-02-28"), "Debe leer la fecha");
		assertNull(FechaUtil.leerFecha("2026-02-30"), "Una fecha inexistente debe devolver null");
		assertNull(FechaUtil.leerFecha(null), "Un texto nulo debe devolver null");
	}
}
