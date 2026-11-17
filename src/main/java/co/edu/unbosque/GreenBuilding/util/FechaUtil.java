package co.edu.unbosque.GreenBuilding.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Clase de utilidad para convertir fechas y horas entre texto y los tipos de java.time. Las horas
 * usan el formato HH:mm y las fechas el formato AAAA-MM-DD.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class FechaUtil {

	/** Formato de las horas que viajan entre el front y el back. */
	public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

	/** Formato largo de fechas en español para los correos, por ejemplo "viernes 20 de noviembre de 2026". */
	public static final DateTimeFormatter FECHA_LARGA =
			DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-CO"));

	/** Constructor privado: esta clase solo tiene métodos estáticos. */
	private FechaUtil() {}

	/**
	 * Convierte una hora a texto HH:mm.
	 *
	 * @param hora Hora a convertir
	 * @return Hora en texto, o null si la hora es nula
	 */
	public static String hora(LocalTime hora) {
		return hora == null ? null : hora.format(HORA);
	}

	/**
	 * Convierte una fecha a texto AAAA-MM-DD.
	 *
	 * @param fecha Fecha a convertir
	 * @return Fecha en texto, o null si la fecha es nula
	 */
	public static String fecha(LocalDate fecha) {
		return fecha == null ? null : fecha.toString();
	}

	/**
	 * Convierte una fecha y hora a texto ISO sin nanosegundos.
	 *
	 * @param fechaHora Fecha y hora a convertir
	 * @return Fecha y hora en texto, o null si es nula
	 */
	public static String fechaHora(LocalDateTime fechaHora) {
		return fechaHora == null ? null : fechaHora.withNano(0).toString();
	}

	/**
	 * Convierte un texto HH:mm a hora.
	 *
	 * @param texto Hora en texto
	 * @return La hora, o null si el texto no es válido
	 */
	public static LocalTime leerHora(String texto) {
		try {
			return LocalTime.parse(texto, HORA);
		} catch (DateTimeParseException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Convierte un texto AAAA-MM-DD a fecha.
	 *
	 * @param texto Fecha en texto
	 * @return La fecha, o null si el texto no es válido
	 */
	public static LocalDate leerFecha(String texto) {
		try {
			return LocalDate.parse(texto);
		} catch (DateTimeParseException | NullPointerException e) {
			return null;
		}
	}
}
