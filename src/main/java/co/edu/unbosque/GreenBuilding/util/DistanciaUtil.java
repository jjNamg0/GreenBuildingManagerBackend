package co.edu.unbosque.GreenBuilding.util;

/**
 * Clase de utilidad para calcular distancias entre dos coordenadas con la fórmula de Haversine.
 *
 * @author Juan Arango
 * @version 1.0
 */
public class DistanciaUtil {

	/** Radio de la Tierra en kilómetros. */
	private static final double RADIO_TIERRA_KM = 6371.0;

	/** Constructor privado: esta clase solo tiene métodos estáticos. */
	private DistanciaUtil() {}

	/**
	 * Calcula la distancia en línea recta entre dos puntos.
	 *
	 * @param latitud1 Latitud del primer punto
	 * @param longitud1 Longitud del primer punto
	 * @param latitud2 Latitud del segundo punto
	 * @param longitud2 Longitud del segundo punto
	 * @return Distancia en kilómetros
	 */
	public static double kilometros(
			double latitud1, double longitud1, double latitud2, double longitud2) {
		double deltaLatitud = Math.toRadians(latitud2 - latitud1);
		double deltaLongitud = Math.toRadians(longitud2 - longitud1);
		double a =
				Math.sin(deltaLatitud / 2) * Math.sin(deltaLatitud / 2)
						+ Math.cos(Math.toRadians(latitud1))
								* Math.cos(Math.toRadians(latitud2))
								* Math.sin(deltaLongitud / 2)
								* Math.sin(deltaLongitud / 2);
		return RADIO_TIERRA_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}

	/**
	 * Redondea un número a un decimal.
	 *
	 * @param valor Número a redondear
	 * @return Número redondeado
	 */
	public static double redondear(double valor) {
		return (long) (valor * 10.0 + 0.5) / 10.0;
	}
}
