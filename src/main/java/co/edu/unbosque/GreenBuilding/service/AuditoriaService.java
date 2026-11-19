package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.PaginaAuditoriaDTO;
import co.edu.unbosque.GreenBuilding.dto.RegistroAuditoriaDTO;
import co.edu.unbosque.GreenBuilding.model.RegistroAuditoria;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.RegistroAuditoriaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.FechaUtil;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servicio que registra y consulta la auditoría del sistema. Cada registro guarda quién hizo la
 * acción, cuándo, qué hizo, desde qué IP y, si el navegador la envió en las cabeceras X-Latitud y
 * X-Longitud, desde qué ubicación.
 */
@Service
public class AuditoriaService {

	/** Tamaño máximo de una página de auditoría. */
	private static final int TAMANO_MAXIMO = 100;

	/** Repositorio para acceder a los registros de auditoría. */
	@Autowired private RegistroAuditoriaRepository registroRepo;

	/** Repositorio para acceder a los datos de usuarios. */
	@Autowired private UsuarioRepository usuarioRepo;

	/** Mapeador para convertir entre entidades y DTOs. */
	@Autowired private ModelMapper modelMapper;

	/** Constructor por defecto. */
	public AuditoriaService() {}

	/**
	 * Registra una acción hecha por el usuario que tiene la sesión iniciada.
	 *
	 * @param accion Acción realizada
	 * @param entidad Entidad afectada
	 * @param motivo Descripción de la acción
	 */
	public void registrar(String accion, String entidad, String motivo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = null;
		if (authentication != null) {
			username = authentication.getName();
		}
		registrar(username, accion, entidad, motivo);
	}

	/**
	 * Registra una acción hecha por un usuario específico.
	 *
	 * @param username Correo del usuario, o null si es un visitante
	 * @param accion Acción realizada
	 * @param entidad Entidad afectada
	 * @param motivo Descripción de la acción
	 */
	public void registrar(String username, String accion, String entidad, String motivo) {
		Usuario usuario = null;
		if (username != null) {
			Optional<Usuario> found = usuarioRepo.findByUsername(username);
			if (found.isPresent()) {
				usuario = found.get();
			}
		}
		RegistroAuditoria registro;
		if (usuario != null) {
			registro =
					new RegistroAuditoria(
							usuario.getUsername(),
							usuario.getNombreCompleto(),
							usuario.getRole().name(),
							accion,
							entidad,
							motivo);
		} else {
			String nombre = "anónimo";
			if (username != null) {
				nombre = username;
			}
			registro = new RegistroAuditoria(nombre, "Desconocido", "PUBLICO", accion, entidad, motivo);
		}
		agregarOrigen(registro);
		registroRepo.save(registro);
	}

	/**
	 * Consulta la auditoría con filtros y paginación, del registro más reciente al más antiguo.
	 *
	 * @param usuario Texto a buscar en el correo o nombre del usuario (opcional)
	 * @param accion Acción exacta a filtrar (opcional)
	 * @param role Tipo de usuario exacto: ADMIN, USER o PUBLICO (opcional)
	 * @param motivo Texto que debe contener el motivo (opcional)
	 * @param desde Fecha inicial AAAA-MM-DD (opcional)
	 * @param hasta Fecha final AAAA-MM-DD (opcional)
	 * @param pagina Número de página, desde 0
	 * @param tamano Tamaño de la página
	 * @return Página de registros de auditoría
	 */
	public PaginaAuditoriaDTO getAll(
			String usuario,
			String accion,
			String role,
			String motivo,
			String desde,
			String hasta,
			int pagina,
			int tamano) {
		LocalDate fechaDesde = null;
		if (!TextoUtil.vacio(desde)) {
			fechaDesde = FechaUtil.leerFecha(desde);
		}
		LocalDate fechaHasta = null;
		if (!TextoUtil.vacio(hasta)) {
			fechaHasta = FechaUtil.leerFecha(hasta);
		}
		int tamanoSeguro = tamano;
		if (tamanoSeguro > TAMANO_MAXIMO) {
			tamanoSeguro = TAMANO_MAXIMO;
		}
		if (tamanoSeguro < 1) {
			tamanoSeguro = 1;
		}
		int paginaSegura = pagina;
		if (paginaSegura < 0) {
			paginaSegura = 0;
		}
		List<RegistroAuditoria> filtrados = new ArrayList<>();
		for (RegistroAuditoria registro : registroRepo.findAllByOrderByFechaDescIdDesc()) {
			if (cumpleFiltro(registro, usuario, accion, role, motivo, fechaDesde, fechaHasta)) {
				filtrados.add(registro);
			}
		}
		int total = filtrados.size();
		long desdeRegistro = (long) paginaSegura * tamanoSeguro;
		int inicio = total;
		if (desdeRegistro < total) {
			inicio = (int) desdeRegistro;
		}
		int fin = total;
		if (inicio + tamanoSeguro < total) {
			fin = inicio + tamanoSeguro;
		}
		List<RegistroAuditoriaDTO> contenido = new ArrayList<>();
		for (int i = inicio; i < fin; i++) {
			contenido.add(aDTO(filtrados.get(i)));
		}
		int totalPaginas = (total + tamanoSeguro - 1) / tamanoSeguro;
		return new PaginaAuditoriaDTO(contenido, total, totalPaginas, paginaSegura, tamanoSeguro);
	}

	/**
	 * Cuenta los registros de auditoría.
	 *
	 * @return Número de registros
	 */
	public long count() {
		return registroRepo.count();
	}

	/**
	 * Indica si un registro cumple todos los filtros de la consulta de auditoría.
	 *
	 * @param registro Registro a revisar
	 * @param usuario Texto a buscar en el correo o nombre del usuario
	 * @param accion Acción a filtrar
	 * @param role Tipo de usuario a filtrar
	 * @param motivo Texto que debe contener el motivo
	 * @param desde Fecha inicial
	 * @param hasta Fecha final
	 * @return true si cumple los filtros que se indicaron
	 */
	private boolean cumpleFiltro(
			RegistroAuditoria registro,
			String usuario,
			String accion,
			String role,
			String motivo,
			LocalDate desde,
			LocalDate hasta) {
		if (!TextoUtil.vacio(usuario)) {
			String buscado = usuario.trim().toLowerCase();
			boolean porCorreo = registro.getUsername() != null && registro.getUsername().toLowerCase().contains(buscado);
			boolean porNombre =
					registro.getNombreUsuario() != null && registro.getNombreUsuario().toLowerCase().contains(buscado);
			if (!porCorreo && !porNombre) {
				return false;
			}
		}
		if (!TextoUtil.vacio(accion) && !accion.trim().toUpperCase().equals(registro.getAccion())) {
			return false;
		}
		if (!TextoUtil.vacio(role) && !role.trim().toUpperCase().equals(registro.getRole())) {
			return false;
		}
		if (!TextoUtil.vacio(motivo)
				&& (registro.getMotivo() == null || !registro.getMotivo().toLowerCase().contains(motivo.trim().toLowerCase()))) {
			return false;
		}
		if (desde != null && (registro.getFecha() == null || registro.getFecha().isBefore(desde.atStartOfDay()))) {
			return false;
		}
		if (hasta != null && (registro.getFecha() == null || registro.getFecha().isAfter(hasta.atTime(LocalTime.MAX)))) {
			return false;
		}
		return true;
	}

	/**
	 * Convierte un registro de auditoría en DTO y arma el texto de la ubicación.
	 *
	 * @param registro Registro a convertir
	 * @return DTO del registro
	 */
	private RegistroAuditoriaDTO aDTO(RegistroAuditoria registro) {
		RegistroAuditoriaDTO dto = modelMapper.map(registro, RegistroAuditoriaDTO.class);
		dto.setFecha(FechaUtil.fechaHora(registro.getFecha()));
		if (registro.getLatitud() == null || registro.getLongitud() == null) {
			dto.setUbicacion("Sin ubicación");
		} else {
			dto.setUbicacion(recortarCoordenada(registro.getLatitud()) + ", " + recortarCoordenada(registro.getLongitud()));
		}
		return dto;
	}

	/**
	 * Agrega al registro la IP y la ubicación de la petición actual.
	 *
	 * @param registro Registro a completar
	 */
	private void agregarOrigen(RegistroAuditoria registro) {
		if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)) {
			return;
		}
		HttpServletRequest request =
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		registro.setLatitud(leerCoordenada(request.getHeader("X-Latitud"), 90));
		registro.setLongitud(leerCoordenada(request.getHeader("X-Longitud"), 180));
		String reenviada = request.getHeader("X-Forwarded-For");
		if (TextoUtil.vacio(reenviada)) {
			registro.setIp(request.getRemoteAddr());
		} else {
			registro.setIp(TextoUtil.separarPorComas(reenviada).get(0).trim());
		}
	}

	/**
	 * Deja una coordenada con máximo cinco decimales para mostrarla.
	 *
	 * @param coordenada Coordenada a recortar
	 * @return La coordenada con máximo cinco decimales
	 */
	private double recortarCoordenada(double coordenada) {
		return (long) (coordenada * 100000) / 100000.0;
	}

	/**
	 * Convierte el valor de una cabecera en coordenada.
	 *
	 * @param valor Valor de la cabecera
	 * @param limite Valor absoluto máximo permitido
	 * @return La coordenada, o null si no es válida
	 */
	private Double leerCoordenada(String valor, double limite) {
		if (TextoUtil.vacio(valor)) {
			return null;
		}
		try {
			double numero = Double.parseDouble(valor.trim());
			if (numero >= -limite && numero <= limite) {
				return numero;
			}
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
