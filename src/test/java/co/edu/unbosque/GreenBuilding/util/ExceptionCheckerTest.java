package co.edu.unbosque.GreenBuilding.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.assertEquals;
=======
>>>>>>> 2b4822b4030f007735f2c8baacfd4f174537e49a
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.DateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmailNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
<<<<<<< HEAD
import co.edu.unbosque.GreenBuilding.exception.LengthNotValidException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PhoneNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import co.edu.unbosque.GreenBuilding.exception.UrlNotValidException;
=======
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
>>>>>>> 2b4822b4030f007735f2c8baacfd4f174537e49a
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
<<<<<<< HEAD
		assertDoesNotThrow(() -> ExceptionChecker.checkEmail("ana@gmail.com"), "Debe aceptar el correo");
=======
		assertDoesNotThrow(() -> ExceptionChecker.checkEmail("ana@correo.com"), "Debe aceptar el correo");
>>>>>>> 2b4822b4030f007735f2c8baacfd4f174537e49a
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
<<<<<<< HEAD
		correos.add("bien@gmail.com");
=======
		correos.add("bien@correo.com");
>>>>>>> 2b4822b4030f007735f2c8baacfd4f174537e49a
		correos.add("mal");
		assertThrows(
				EmailNotValidException.class,
				() -> ExceptionChecker.checkEmails(correos),
				"Debe rechazar la lista si un correo es inválido");
	}
<<<<<<< HEAD

	/**
	 * Prueba que solo se acepten correos de los dominios permitidos, sin importar mayúsculas.
	 */
	@Test
	void testCheckEmailDominio() {
		assertDoesNotThrow(() -> ExceptionChecker.checkEmail("Juan@Gmail.com"), "Debe aceptar Gmail");
		assertDoesNotThrow(
				() -> ExceptionChecker.checkEmail("jarango@unbosque.edu.co"), "Debe aceptar la universidad");
		EmailNotValidException error =
				assertThrows(
						EmailNotValidException.class,
						() -> ExceptionChecker.checkEmail("juan@skibidi.com"),
						"Debe rechazar un dominio desconocido");
		assertEquals(ExceptionChecker.MENSAJE_DOMINIO, error.getMessage(), "Debe explicar qué correos sirven");
		List<String> correos = new ArrayList<>();
		correos.add("ana@hotmail.com");
		correos.add("juan@skibidi.com");
		assertThrows(
				EmailNotValidException.class,
				() -> ExceptionChecker.checkEmails(correos),
				"Las invitaciones también deben rechazar un dominio desconocido");
	}

	/**
	 * Prueba que el correo no pase de 255 caracteres.
	 */
	@Test
	void testCheckEmailMuyLargo() {
		String largo = "a".repeat(250) + "@gmail.com";
		assertThrows(
				EmailNotValidException.class,
				() -> ExceptionChecker.checkEmail(largo),
				"Debe rechazar un correo de más de 255 caracteres");
	}

	/**
	 * Prueba que el teléfono tenga exactamente 10 dígitos.
	 */
	@Test
	void testCheckPhone() {
		assertDoesNotThrow(() -> ExceptionChecker.checkPhone("3001234567"), "Debe aceptar un celular");
		assertDoesNotThrow(() -> ExceptionChecker.checkPhone(" 6011234567 "), "Debe aceptar un fijo con espacios alrededor");
		assertThrows(
				PhoneNotValidException.class,
				() -> ExceptionChecker.checkPhone("300123"),
				"Debe rechazar un teléfono corto");
		assertThrows(
				PhoneNotValidException.class,
				() -> ExceptionChecker.checkPhone("hola123456"),
				"Debe rechazar letras");
		assertThrows(
				PhoneNotValidException.class,
				() -> ExceptionChecker.checkPhone(null),
				"Debe rechazar un teléfono nulo");
	}

	/**
	 * Prueba que un texto no pase del máximo de caracteres y que un texto nulo sea aceptado.
	 */
	@Test
	void testCheckLength() {
		assertDoesNotThrow(
				() -> ExceptionChecker.checkLength("nombre", "a".repeat(255), ExceptionChecker.MAX_TEXTO),
				"Debe aceptar justo el máximo");
		assertDoesNotThrow(
				() -> ExceptionChecker.checkLength("descripción", null, ExceptionChecker.MAX_DESCRIPCION),
				"Debe aceptar un texto opcional nulo");
		LengthNotValidException error =
				assertThrows(
						LengthNotValidException.class,
						() -> ExceptionChecker.checkLength("nombre", "a".repeat(256), ExceptionChecker.MAX_TEXTO),
						"Debe rechazar un carácter de más");
		assertEquals(
				"El campo nombre no puede tener más de 255 caracteres.",
				error.getMessage(),
				"El mensaje debe decir el campo y el máximo");
	}

	/**
	 * Prueba que la imagen sea opcional y, si viene, empiece por http:// o https://.
	 */
	@Test
	void testCheckUrl() {
		assertDoesNotThrow(() -> ExceptionChecker.checkUrl(null), "Debe aceptar una imagen nula");
		assertDoesNotThrow(() -> ExceptionChecker.checkUrl(""), "Debe aceptar una imagen vacía");
		assertDoesNotThrow(
				() -> ExceptionChecker.checkUrl("https://sitio.com/foto.jpg"), "Debe aceptar una URL https");
		assertThrows(
				UrlNotValidException.class,
				() -> ExceptionChecker.checkUrl("foto.jpg"),
				"Debe rechazar un texto que no es URL");
		assertThrows(
				UrlNotValidException.class,
				() -> ExceptionChecker.checkUrl("https://sitio.com/mi foto.jpg"),
				"Debe rechazar una URL con espacios");
	}

	/**
	 * Prueba que la ubicación de un conjunto sea obligatoria y esté dentro del rango de la Tierra.
	 */
	@Test
	void testCheckLocation() {
		assertDoesNotThrow(() -> ExceptionChecker.checkLocation(4.86, -74.05), "Debe aceptar Chía");
		assertThrows(
				CoordinateNotValidException.class,
				() -> ExceptionChecker.checkLocation(null, -74.05),
				"Debe rechazar una ubicación sin latitud");
		assertThrows(
				CoordinateNotValidException.class,
				() -> ExceptionChecker.checkLocation(4.86, 44205.94),
				"Debe rechazar una longitud mayor a 180");
	}
=======
>>>>>>> 2b4822b4030f007735f2c8baacfd4f174537e49a
}
