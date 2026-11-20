package co.edu.unbosque.GreenBuilding.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la utilidad TextoUtil.
 *
 * <p>Verifica que los textos se limpien, normalicen, separen y escapen correctamente.
 *
 * @author Juan Arango
 * @version 1.0
 */
class TextoUtilTest {

	/**
	 * Prueba que un texto nulo o con solo espacios se considere vacío y uno con letras no.
	 */
	@Test
	void testVacioDetectaTextosSinContenido() {
		assertTrue(TextoUtil.vacio(null), "Un texto nulo debe ser vacío");
		assertTrue(TextoUtil.vacio("   "), "Un texto con espacios debe ser vacío");
		assertFalse(TextoUtil.vacio("hola"), "Un texto con letras no debe ser vacío");
	}

	/**
	 * Prueba que limpiar quite los espacios de los extremos y convierta null en texto vacío.
	 */
	@Test
	void testLimpiarQuitaEspaciosYManejaNulo() {
		assertEquals("hola", TextoUtil.limpiar("  hola  "), "Debe quitar los espacios");
		assertEquals("", TextoUtil.limpiar(null), "Un nulo debe volverse vacío");
	}

	/**
	 * Prueba que opcional devuelva null para textos vacíos y el texto limpio en otro caso.
	 */
	@Test
	void testOpcionalDevuelveNuloSiEstaVacio() {
		assertNull(TextoUtil.opcional("  "), "Un texto vacío debe volverse null");
		assertEquals("valor", TextoUtil.opcional(" valor "), "Debe devolver el texto limpio");
	}

	/**
	 * Prueba que el correo quede en minúsculas y sin espacios.
	 */
	@Test
	void testCorreoQuedaEnMinusculas() {
		assertEquals(
				"usuario@ejemplo.com",
				TextoUtil.correo("  Usuario@Ejemplo.COM "),
				"El correo debe quedar en minúsculas y sin espacios");
	}

	/**
	 * Prueba que normalizar pase a minúsculas y quite las tildes.
	 */
	@Test
	void testNormalizarQuitaTildesYMayusculas() {
		assertEquals("cancion de nino", TextoUtil.normalizar("Canción de Niño"), "Debe quitar tildes");
	}

	/**
	 * Prueba que separarPorComas divida el texto y elimine las partes vacías del final.
	 */
	@Test
	void testSepararPorComasDivideElTexto() {
		List<String> partes = TextoUtil.separarPorComas("uno,dos,tres,,");
		assertEquals(3, partes.size(), "Deben quedar tres partes");
		assertEquals("dos", partes.get(1), "La segunda parte debe ser dos");
	}

	/**
	 * Prueba que contiene encuentre un texto sin importar tildes ni mayúsculas.
	 */
	@Test
	void testContieneIgnoraTildesYMayusculas() {
		assertTrue(TextoUtil.contiene("Salón Social", "salon"), "Debe encontrar salon en Salón");
		assertFalse(TextoUtil.contiene("Piscina", "salon"), "No debe encontrar salon en Piscina");
	}

	/**
	 * Prueba que escaparHtml reemplace los caracteres especiales de HTML.
	 */
	@Test
	void testEscaparHtmlReemplazaCaracteresEspeciales() {
		assertEquals(
				"&lt;b&gt;Hola &amp; adiós&lt;/b&gt;",
				TextoUtil.escaparHtml("<b>Hola & adiós</b>"),
				"Debe escapar los caracteres especiales");
	}
}
