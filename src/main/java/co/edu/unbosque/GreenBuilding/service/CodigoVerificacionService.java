package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion;
import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.CodigoVerificacionRepository;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que genera, envía y valida los códigos de 6 dígitos para verificar
 * la cuenta y para restablecer la contraseña. Cada código vence en 15 minutos y
 * admite 5 intentos.
 */
@Service
public class CodigoVerificacionService {

	/** Minutos que dura un código. */
	public static final int MINUTOS_VIGENCIA = 15;

	/** Segundos que hay que esperar para pedir otro código. */
	public static final int SEGUNDOS_ENTRE_ENVIOS = 60;

	/** Intentos fallidos permitidos por código. */
	public static final int INTENTOS_MAXIMOS = 5;

	/** Generador seguro de números aleatorios. */
	private final SecureRandom aleatorio = new SecureRandom();

	/** Repositorio para acceder a los códigos. */
	@Autowired
	private CodigoVerificacionRepository codigoRepo;

	/** Servicio para enviar el código por correo. */
	@Autowired
	private CorreoService correoServ;

	/** Constructor por defecto. */
	public CodigoVerificacionService() {
	}

	/**
	 * Genera un código nuevo, borra los anteriores del mismo propósito y lo envía
	 * al correo.
	 *
	 * @param usuario   Usuario que recibe el código
	 * @param proposito Para qué sirve el código
	 * @return 0 si el código se envió, 1 si debe esperar un minuto para pedir otro,
	 *         2 si el correo no se pudo enviar
	 */
	public int generarYEnviar(Usuario usuario, Proposito proposito) {
		Optional<CodigoVerificacion> anterior = codigoRepo
				.findFirstByUsernameAndPropositoOrderByIdDesc(usuario.getUsername(), proposito);
		LocalDateTime limite = LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA).minusSeconds(SEGUNDOS_ENTRE_ENVIOS);
		if (anterior.isPresent() && anterior.get().getExpiracion().isAfter(limite)) {
			return 1;
		}
		codigoRepo.deleteByUsernameAndProposito(usuario.getUsername(), proposito);
		String codigo = String.format("%06d", aleatorio.nextInt(1_000_000));
		codigoRepo.save(new CodigoVerificacion(usuario.getUsername(), codigo, proposito,
				LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA)));
		if (correoServ.enviarCodigo(usuario, codigo, proposito)) {
			return 0;
		} else {
			return 2;
		}
	}

	/**
	 * Valida el código que escribió el usuario. Si es incorrecto, suma un intento
	 * fallido.
	 *
	 * @param username        Correo del usuario
	 * @param codigoIngresado Código escrito por el usuario
	 * @param proposito       Para qué sirve el código
	 * @return 0 si el código es correcto, 2 si es incorrecto, 3 si venció, 4 si
	 *         superó los intentos, 5 si no tiene un código activo
	 */
	public int validar(String username, String codigoIngresado, Proposito proposito) {
		Optional<CodigoVerificacion> found = codigoRepo.findFirstByUsernameAndPropositoOrderByIdDesc(username,
				proposito);
		if (!found.isPresent()) {
			return 5;
		}
		CodigoVerificacion codigo = found.get();
		if (codigo.estaVencido()) {
			return 3;
		}
		if (codigo.getIntentos() >= INTENTOS_MAXIMOS) {
			return 4;
		}
		if (!codigo.getCodigo().equals(TextoUtil.limpiar(codigoIngresado))) {
			codigo.setIntentos(codigo.getIntentos() + 1);
			codigoRepo.save(codigo);
			return 2;
		}
		return 0;
	}

	/**
	 * Elimina los códigos de un usuario para un propósito.
	 *
	 * @param username  Correo del usuario
	 * @param proposito Propósito de los códigos
	 */
	public void eliminar(String username, Proposito proposito) {
		codigoRepo.deleteByUsernameAndProposito(username, proposito);
	}

	/**
	 * Elimina todos los códigos de un usuario.
	 *
	 * @param username Correo del usuario
	 */
	public void eliminarTodos(String username) {
		codigoRepo.deleteByUsername(username);
	}
}
