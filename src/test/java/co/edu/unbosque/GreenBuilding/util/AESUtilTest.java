package co.edu.unbosque.GreenBuilding.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Clase de prueba para la utilidad AESUtil.
 *
 * <p>Verifica que el cifrado AES se pueda revertir y que los hashes tengan el largo esperado.
 *
 * @author Juan Arango
 * @version 1.0
 */
class AESUtilTest {

	/**
	 * Prueba que un texto cifrado se pueda descifrar y recuperar el original.
	 */
	@Test
	void testCifrarYDescifrarDevuelveElTextoOriginal() {
		String original = "texto secreto de prueba";
		String cifrado = AESUtil.encrypt(original);
		assertNotEquals(original, cifrado, "El texto cifrado no debe ser igual al original");
		assertEquals(original, AESUtil.decrypt(cifrado), "Al descifrar debe volver el original");
	}

	/**
	 * Prueba que el hash MD5 de un texto conocido sea el esperado.
	 */
	@Test
	void testHashMd5DeTextoConocido() {
		assertEquals(
				"5d41402abc4b2a76b9719d911017c592",
				AESUtil.hashingToMD5("hello"),
				"El MD5 de hello debe ser el conocido");
	}

	/**
	 * Prueba que cada hash tenga el largo en hexadecimal que le corresponde.
	 */
	@Test
	void testLargoDeLosHashes() {
		assertEquals(40, AESUtil.hashingToSHA1("a").length(), "SHA-1 debe tener 40 caracteres");
		assertEquals(64, AESUtil.hashingToSHA256("a").length(), "SHA-256 debe tener 64 caracteres");
		assertEquals(96, AESUtil.hashingToSHA384("a").length(), "SHA-384 debe tener 96 caracteres");
		assertEquals(128, AESUtil.hashingToSHA512("a").length(), "SHA-512 debe tener 128 caracteres");
	}
}
