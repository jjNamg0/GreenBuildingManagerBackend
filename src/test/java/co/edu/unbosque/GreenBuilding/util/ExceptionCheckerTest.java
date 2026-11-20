package co.edu.unbosque.GreenBuilding.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.DateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmailNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la utilidad ExceptionChecker.
 *
 * <p>Verifica que cada validación acepte los datos correctos y lance su excepción con los
 * incorrectos.
 *
 * @author Juan Arango
 * @version 1.0
 */
class ExceptionCheckerTest {

	/**
	 * Prueba que la contraseña exija mayúscula, minúscula, número, símbolo y 8 caracteres.
	 */
	@Test
	void testCheckPassword() {
		assertDoesNotThrow(() -> ExceptionChecker.checkPassword("Clave123*"), "Debe aceptar la clave");
		assertThrows(
				PasswordNotValidException.class,
				() -> ExceptionChecker.checkPassword("clave123"),
				"Debe rechazar una clave sin mayúscula ni símbolo");
	}

	/**
	 * Prueba que el correo tenga el formato usuario@dominio.
	 */
	@Test
	void testCheckEmail() {
		assertDoesNotThrow(() -> ExceptionChecker.checkEmail("ana@correo.com"), "Debe aceptar el correo");
		assertThrows(
				EmailNotValidException.class,
				() -> ExceptionChecker.checkEmail("ana.correo.com"),
				"Debe rechazar un correo sin @");
	}

	/**
	 * Prueba que un campo vacío o con solo espacios sea rechazado.
	 */
	@Test
	void testCheckEmpty() {
		assertThrows(
				EmptyFieldException.class,
				() -> ExceptionChecker.checkEmpty("Nombre", "   "),
				"Debe rechazar un campo vacío");
	}

	/**
	 * Prueba que los caracteres de etiquetas HTML sean rechazados.
	 */
	@Test
	void testCheckCharacters() {
		assertThrows(
				InvalidCharactersException.class,
				() -> ExceptionChecker.checkCharacters("<script>"),
				"Debe rechazar < y >");
	}

	/**
	 * Prueba que el rango de horas exija que el inicio sea antes del fin.
	 */
	@Test
	void testCheckTimeRange() {
		assertDoesNotThrow(() -> ExceptionChecker.checkTimeRange("08:00", "10:00"), "Debe aceptar el rango");
		assertThrows(
				TimeNotValidException.class,
				() -> ExceptionChecker.checkTimeRange("10:00", "08:00"),
				"Debe rechazar un inicio después del fin");
	}

	/**
	 * Prueba que la fecha tenga el formato AAAA-MM-DD y exista.
	 */
	@Test
	void testCheckDate() {
		assertDoesNotThrow(() -> ExceptionChecker.checkDate("2026-12-24"), "Debe aceptar la fecha");
		assertThrows(
				DateNotValidException.class,
				() -> ExceptionChecker.checkDate("2026-02-30"),
				"Debe rechazar una fecha que no existe");
	}

	/**
	 * Prueba que un número obligatorio deba ser mayor que cero.
	 */
	@Test
	void testCheckPositive() {
		assertThrows(
				NumberNotValidException.class,
				() -> ExceptionChecker.checkPositive("Capacidad", 0),
				"Debe rechazar el cero");
	}

	/**
	 * Prueba que las coordenadas fuera del rango de la Tierra sean rechazadas.
	 */
	@Test
	void testCheckCoordinates() {
		assertDoesNotThrow(() -> ExceptionChecker.checkCoordinates(4.7, -74.05), "Debe aceptar Bogotá");
		assertThrows(
				CoordinateNotValidException.class,
				() -> ExceptionChecker.checkCoordinates(4.7, 44205.94),
				"Debe rechazar una longitud mayor a 180");
	}

	/**
	 * Prueba que la lista de correos de una invitación no esté vacía ni tenga correos inválidos.
	 */
	@Test
	void testCheckEmails() {
		List<String> correos = new ArrayList<>();
		assertThrows(
				EmailNotValidException.class,
				() -> ExceptionChecker.checkEmails(correos),
				"Debe rechazar una lista vacía");
		correos.add("bien@correo.com");
		correos.add("mal");
		assertThrows(
				EmailNotValidException.class,
				() -> ExceptionChecker.checkEmails(correos),
				"Debe rechazar la lista si un correo es inválido");
	}
}
