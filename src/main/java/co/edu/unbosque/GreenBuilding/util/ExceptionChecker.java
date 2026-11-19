package co.edu.unbosque.GreenBuilding.util;

import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.DateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmailNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import java.util.List;

/**
 * Clase de utilidad que revisa los datos que llegan a los controladores y lanza la excepción
 * correspondiente cuando un dato no es válido.
 *
 * @author Daniel M
 * @version 1.0
 */
public class ExceptionChecker {

	/** Patrón de una contraseña segura: 8 caracteres, minúscula, mayúscula, número y símbolo. */
	private static final String PATRON_PASSWORD =
			"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d\\s]).{8,}$";

	/** Patrón de un correo electrónico. */
	private static final String PATRON_CORREO =
			"^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

	/** Patrón de una hora con formato HH:mm. */
	private static final String PATRON_HORA =
			"^([01]\\d|2[0-3]):[0-5]\\d$";

	/** Patrón de una fecha con formato AAAA-MM-DD. */
	private static final String PATRON_FECHA =
			"^\\d{4}-\\d{2}-\\d{2}$";

	/** Número máximo de correos por envío de invitaciones. */
	public static final int MAX_CORREOS_INVITACION = 30;

	/** Constructor privado: esta clase solo tiene métodos estáticos. */
	private ExceptionChecker() {}

	/**
	 * Revisa que la contraseña cumpla con el estándar de seguridad.
	 *
	 * @param password Contraseña a revisar
	 * @throws PasswordNotValidException si la contraseña no cumple el estándar
	 */
	public static void checkPassword(String password) throws PasswordNotValidException {
		if (password == null || !password.matches(PATRON_PASSWORD)) {
			throw new PasswordNotValidException();
		}
	}

	/**
	 * Revisa que el correo tenga un formato válido.
	 *
	 * @param correo Correo a revisar
	 * @throws EmailNotValidException si el correo no es válido
	 */
	public static void checkEmail(String correo) throws EmailNotValidException {
		if (correo == null || !correo.trim().matches(PATRON_CORREO)) {
			throw new EmailNotValidException();
		}
	}

	/**
	 * Revisa que un campo de texto obligatorio no esté vacío.
	 *
	 * @param campo Nombre del campo, para el mensaje de error
	 * @param valor Valor del campo
	 * @throws EmptyFieldException si el campo está vacío
	 */
	public static void checkEmpty(String campo, String valor) throws EmptyFieldException {
		if (valor == null || valor.trim().isEmpty()) {
			throw new EmptyFieldException(campo);
		}
	}

	/**
	 * Revisa que un texto no tenga los caracteres de etiquetas HTML.
	 *
	 * @param valor Texto a revisar
	 * @throws InvalidCharactersException si el texto contiene "&lt;" o "&gt;"
	 */
	public static void checkCharacters(String valor) throws InvalidCharactersException {
		if (valor != null && (valor.contains("<") || valor.contains(">"))) {
			throw new InvalidCharactersException();
		}
	}

	/**
	 * Revisa que una hora tenga el formato HH:mm.
	 *
	 * @param hora Hora a revisar
	 * @throws TimeNotValidException si la hora no tiene el formato
	 */
	public static void checkTime(String hora) throws TimeNotValidException {
		if (hora == null || !hora.matches(PATRON_HORA)) {
			throw new TimeNotValidException();
		}
	}

	/**
	 * Revisa que la hora de inicio sea antes de la hora de fin.
	 *
	 * @param inicio Hora de inicio con formato HH:mm
	 * @param fin Hora de fin con formato HH:mm
	 * @throws TimeNotValidException si alguna hora no tiene el formato o el rango es incorrecto
	 */
	public static void checkTimeRange(String inicio, String fin) throws TimeNotValidException {
		checkTime(inicio);
		checkTime(fin);
		if (!FechaUtil.leerHora(inicio).isBefore(FechaUtil.leerHora(fin))) {
			throw new TimeNotValidException("La hora de inicio debe ser antes de la hora de fin.");
		}
	}

	/**
	 * Revisa que una fecha tenga el formato AAAA-MM-DD y sea una fecha real.
	 *
	 * @param fecha Fecha a revisar
	 * @throws DateNotValidException si la fecha no es válida
	 */
	public static void checkDate(String fecha) throws DateNotValidException {
		if (fecha == null || !fecha.matches(PATRON_FECHA) || FechaUtil.leerFecha(fecha) == null) {
			throw new DateNotValidException();
		}
	}

	/**
	 * Revisa que un número obligatorio sea mayor a cero.
	 *
	 * @param campo Nombre del campo, para el mensaje de error
	 * @param valor Valor del campo
	 * @throws NumberNotValidException si el número es nulo o no es mayor a cero
	 */
	public static void checkPositive(String campo, Integer valor) throws NumberNotValidException {
		if (valor == null || valor <= 0) {
			throw new NumberNotValidException(campo);
		}
	}

	/**
	 * Revisa que un número opcional, cuando viene, sea mayor a cero.
	 *
	 * @param campo Nombre del campo, para el mensaje de error
	 * @param valor Valor del campo, puede ser nulo
	 * @throws NumberNotValidException si el número viene y no es mayor a cero
	 */
	public static void checkOptionalPositive(String campo, Integer valor)
			throws NumberNotValidException {
		if (valor != null && valor <= 0) {
			throw new NumberNotValidException(campo);
		}
	}

	/**
	 * Revisa que se haya elegido un dato obligatorio, como un conjunto, un espacio o un tipo.
	 *
	 * @param mensaje Mensaje de error si falta el dato
	 * @param valor Dato elegido
	 * @throws OptionNotValidException si el dato es nulo
	 */
	public static void checkSelected(String mensaje, Object valor) throws OptionNotValidException {
		if (valor == null) {
			throw new OptionNotValidException(mensaje);
		}
	}

	/**
	 * Revisa que la opción recibida sea una de las permitidas.
	 *
	 * @param valida true si la opción es una de las permitidas
	 * @throws OptionNotValidException si la opción no es válida
	 */
	public static void checkOption(boolean valida) throws OptionNotValidException {
		if (!valida) {
			throw new OptionNotValidException();
		}
	}

	/**
	 * Revisa que se haya marcado una ubicación.
	 *
	 * @param latitud Latitud de la ubicación
	 * @param longitud Longitud de la ubicación
	 * @throws CoordinateNotValidException si falta la latitud o la longitud
	 */
	public static void checkLocation(Double latitud, Double longitud)
			throws CoordinateNotValidException {
		if (latitud == null || longitud == null) {
			throw new CoordinateNotValidException("Marca la ubicación en el mapa.");
		}
	}

	/**
	 * Revisa que la latitud y la longitud estén dentro de los rangos del planeta.
	 *
	 * @param latitud Latitud, entre -90 y 90
	 * @param longitud Longitud, entre -180 y 180
	 * @throws CoordinateNotValidException si alguna está fuera de rango
	 */
	public static void checkCoordinates(double latitud, double longitud)
			throws CoordinateNotValidException {
		if (latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) {
			throw new CoordinateNotValidException();
		}
	}

	/**
	 * Revisa la lista de correos de una invitación: que tenga al menos uno, que no pase del máximo y
	 * que todos tengan un formato válido.
	 *
	 * @param correos Correos a revisar
	 * @throws EmailNotValidException si la lista está vacía, es muy larga o tiene correos inválidos
	 */
	public static void checkEmails(List<String> correos) throws EmailNotValidException {
		if (correos == null || correos.isEmpty()) {
			throw new EmailNotValidException("Escribe al menos un correo.");
		}
		if (correos.size() > MAX_CORREOS_INVITACION) {
			throw new EmailNotValidException(
					"Puedes invitar hasta " + MAX_CORREOS_INVITACION + " correos por envío.");
		}
		String invalidos = "";
		for (String correo : correos) {
			if (correo == null || !correo.trim().matches(PATRON_CORREO)) {
				if (!invalidos.isEmpty()) {
					invalidos = invalidos + ", ";
				}
				invalidos = invalidos + correo;
			}
		}
		if (!invalidos.isEmpty()) {
			throw new EmailNotValidException("Estos correos no son válidos: " + invalidos);
		}
	}
}