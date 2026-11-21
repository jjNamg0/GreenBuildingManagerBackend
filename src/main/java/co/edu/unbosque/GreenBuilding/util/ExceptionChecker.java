package co.edu.unbosque.GreenBuilding.util;

import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.DateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmailNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.LengthNotValidException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PhoneNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import co.edu.unbosque.GreenBuilding.exception.UrlNotValidException;
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

	/** Patrón de un correo electrónico de máximo 255 caracteres. */
	private static final String PATRON_CORREO =
			"^(?=.{1,255}$)[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

	/** Dominios de correo aceptados: los proveedores más conocidos y el de la universidad. */
	public static final List<String> DOMINIOS_PERMITIDOS =
			List.of(
					"gmail.com", "hotmail.com", "hotmail.es", "outlook.com", "outlook.es", "live.com",
					"msn.com", "yahoo.com", "yahoo.es", "icloud.com", "me.com", "aol.com",
					"protonmail.com", "proton.me", "unbosque.edu.co");

	/** Mensaje cuando el correo es de un dominio que no está en la lista de permitidos. */
	public static final String MENSAJE_DOMINIO =
			"Usa un correo de un proveedor conocido, como Gmail, Hotmail, Outlook, Yahoo o iCloud.";

	/** Patrón de un teléfono de 10 dígitos, como los celulares y fijos de Colombia. */
	private static final String PATRON_TELEFONO =
			"^\\d{10}$";

	/** Patrón de una URL que empieza por http:// o https:// y no tiene espacios. */
	private static final String PATRON_URL =
			"^https?://\\S+$";

	/** Número máximo de caracteres de un texto corto, como un nombre o una dirección. */
	public static final int MAX_TEXTO = 255;

	/** Número máximo de caracteres de un motivo o una observación. */
	public static final int MAX_MOTIVO = 500;

	/** Número máximo de caracteres de la URL de una imagen. */
	public static final int MAX_URL = 1000;

	/** Número máximo de caracteres de una descripción. */
	public static final int MAX_DESCRIPCION = 2000;

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
		if (!dominioPermitido(correo)) {
			throw new EmailNotValidException(MENSAJE_DOMINIO);
		}
	}

	/**
	 * Indica si el dominio del correo (lo que va después del @) está en la lista de permitidos.
	 *
	 * @param correo Correo con formato válido
	 * @return true si el dominio es uno de los permitidos
	 */
	public static boolean dominioPermitido(String correo) {
		String limpio = correo.trim().toLowerCase();
		String dominio = limpio.substring(limpio.indexOf("@") + 1);
		return DOMINIOS_PERMITIDOS.contains(dominio);
	}

	/**
	 * Revisa que el teléfono tenga 10 dígitos.
	 *
	 * @param telefono Teléfono a revisar
	 * @throws PhoneNotValidException si el teléfono no tiene 10 dígitos
	 */
	public static void checkPhone(String telefono) throws PhoneNotValidException {
		if (telefono == null || !telefono.trim().matches(PATRON_TELEFONO)) {
			throw new PhoneNotValidException();
		}
	}

	/**
	 * Revisa que un texto no pase del número máximo de caracteres.
	 *
	 * @param campo Nombre del campo, para el mensaje de error
	 * @param valor Texto a revisar, puede ser nulo
	 * @param maximo Número máximo de caracteres
	 * @throws LengthNotValidException si el texto tiene más caracteres de los permitidos
	 */
	public static void checkLength(String campo, String valor, int maximo)
			throws LengthNotValidException {
		if (valor != null && valor.trim().length() > maximo) {
			throw new LengthNotValidException(campo, maximo);
		}
	}

	/**
	 * Revisa que la URL de una imagen, cuando viene, empiece por http:// o https://.
	 *
	 * @param url URL a revisar, puede ser nula o vacía
	 * @throws UrlNotValidException si la URL viene y no es válida
	 */
	public static void checkUrl(String url) throws UrlNotValidException {
		if (url != null && !url.trim().isEmpty() && !url.trim().matches(PATRON_URL)) {
			throw new UrlNotValidException();
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
	 * Revisa que se haya marcado una ubicación y que esté dentro de los rangos del planeta.
	 *
	 * @param latitud Latitud de la ubicación
	 * @param longitud Longitud de la ubicación
	 * @throws CoordinateNotValidException si falta la latitud o la longitud, o si están fuera de
	 *     rango
	 */
	public static void checkLocation(Double latitud, Double longitud)
			throws CoordinateNotValidException {
		if (latitud == null || longitud == null) {
			throw new CoordinateNotValidException("Marca la ubicación en el mapa.");
		}
		checkCoordinates(latitud, longitud);
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
			if (correo == null || !correo.trim().matches(PATRON_CORREO) || !dominioPermitido(correo)) {
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