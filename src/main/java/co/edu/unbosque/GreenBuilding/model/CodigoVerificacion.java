package co.edu.unbosque.GreenBuilding.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase que representa un código de 6 dígitos enviado al correo para verificar la cuenta o
 * restablecer la contraseña.
 *
 * Esta entidad se almacena en la tabla "codigo_verificacion" en la base de datos.
 */
@Entity
@Table(name = "codigo_verificacion")
public class CodigoVerificacion {

	/**
	 * Identificador único del código.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Correo al que se envió el código.
	 */
	private String username;

	/**
	 * Código de 6 dígitos.
	 */
	private String codigo;

	/**
	 * Para qué sirve el código.
	 */
	@Enumerated(EnumType.STRING)
	private Proposito proposito;

	/**
	 * Fecha y hora en que vence el código.
	 */
	private LocalDateTime expiracion;

	/**
	 * Número de intentos fallidos con este código.
	 */
	private int intentos;

	/**
	 * Constructor por defecto.
	 */
	public CodigoVerificacion() {
	}

	/**
	 * Constructor con todos los datos del código.
	 *
	 * @param username Correo del usuario
	 * @param codigo Código de 6 dígitos
	 * @param proposito Para qué sirve
	 * @param expiracion Cuándo vence
	 */
	public CodigoVerificacion(String username, String codigo, Proposito proposito, LocalDateTime expiracion) {
		this.username = username;
		this.codigo = codigo;
		this.proposito = proposito;
		this.expiracion = expiracion;
	}

	/**
	 * Enumeración de los propósitos de un código.
	 */
	public enum Proposito {
		/** Verificar el correo al registrarse */
		VERIFICACION,
		/** Restablecer la contraseña */
		RESTABLECER
	}

	/**
	 * Indica si el código ya venció.
	 *
	 * @return true si venció
	 */
	public boolean estaVencido() {
		return expiracion.isBefore(LocalDateTime.now());
	}

	/**
	 * Obtiene el identificador del código.
	 *
	 * @return El ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del código.
	 *
	 * @param id El nuevo ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el correo del usuario.
	 *
	 * @return El correo
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el correo del usuario.
	 *
	 * @param username El correo
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el código.
	 *
	 * @return El código
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Establece el código.
	 *
	 * @param codigo El código
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Obtiene el propósito del código.
	 *
	 * @return El propósito
	 */
	public Proposito getProposito() {
		return proposito;
	}

	/**
	 * Establece el propósito del código.
	 *
	 * @param proposito El propósito
	 */
	public void setProposito(Proposito proposito) {
		this.proposito = proposito;
	}

	/**
	 * Obtiene la fecha de vencimiento.
	 *
	 * @return La expiración
	 */
	public LocalDateTime getExpiracion() {
		return expiracion;
	}

	/**
	 * Establece la fecha de vencimiento.
	 *
	 * @param expiracion La expiración
	 */
	public void setExpiracion(LocalDateTime expiracion) {
		this.expiracion = expiracion;
	}

	/**
	 * Obtiene los intentos fallidos.
	 *
	 * @return Los intentos
	 */
	public int getIntentos() {
		return intentos;
	}

	/**
	 * Establece los intentos fallidos.
	 *
	 * @param intentos Los intentos
	 */
	public void setIntentos(int intentos) {
		this.intentos = intentos;
	}

	/**
	 * Calcula el código hash para este código.
	 *
	 * @return El código hash calculado
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, username, codigo, proposito);
	}

	/**
	 * Compara este código con otro objeto para determinar si son iguales.
	 *
	 * @param obj El objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodigoVerificacion other = (CodigoVerificacion) obj;
		return Objects.equals(id, other.id) && Objects.equals(username, other.username)
				&& Objects.equals(codigo, other.codigo) && proposito == other.proposito;
	}

	/**
	 * Devuelve una representación en cadena de texto de este código. No incluye el código.
	 *
	 * @return Una cadena que representa este código
	 */
	@Override
	public String toString() {
		return "CodigoVerificacion [id=" + id + ", username=" + username + ", proposito=" + proposito
				+ ", expiracion=" + expiracion + "]";
	}

}
