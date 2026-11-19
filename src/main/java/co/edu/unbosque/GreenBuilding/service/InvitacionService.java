package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.InvitacionDTO;
import co.edu.unbosque.GreenBuilding.dto.InvitadoDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Invitado;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.InvitadoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.FechaUtil;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio de las invitaciones a eventos y a reservas: genera el enlace público
 * de cada plan, envía la invitación por correo a los invitados que escribe el
 * anfitrión, los guarda y les avisa si el plan se cancela.
 */
@Service
public class InvitacionService {

	/** Letras y números con los que se arma el código del enlace. */
	private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

	/** Largo del código del enlace. */
	private static final int LARGO_CODIGO = 24;

	/** Generador de números aleatorios seguro para los códigos. */
	private final SecureRandom aleatorio = new SecureRandom();

	/** Dirección del front, para armar los enlaces. */
	@Value("${app.front.url:http://localhost:4200}")
	private String urlFront;

	/** Repositorio para acceder a los invitados. */
	@Autowired
	private InvitadoRepository invitadoRepo;

	/** Repositorio para acceder a los eventos. */
	@Autowired
	private EventoRepository eventoRepo;

	/** Repositorio para acceder a las reservas. */
	@Autowired
	private ReservaRepository reservaRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired
	private EspacioRepository espacioRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired
	private UsuarioRepository usuarioRepo;

	/** Servicio de correo. */
	@Autowired
	private CorreoService correoServ;

	/** Servicio que arma el HTML de los correos. */
	@Autowired
	private PlantillaCorreoService plantillaServ;

	/** Servicio de auditoría. */
	@Autowired
	private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public InvitacionService() {
	}

	/**
	 * Busca la invitación de un enlace público. Primero busca un evento con ese
	 * código y luego una reserva aprobada, cancelada o ya pasada.
	 *
	 * @param codigo Código del enlace
	 * @return DTO de la invitación, o null si el código no existe
	 */
	public InvitacionDTO getByCodigo(String codigo) {
		Optional<Evento> evento = eventoRepo.findByCodigoInvitacion(codigo);
		if (evento.isPresent()) {
			auditoriaServ.registrar(null, "INVITACION", "Evento", "Abrió la invitación de " + evento.get().getTitulo());
			return deEvento(evento.get());
		}
		Optional<Reserva> reserva = reservaRepo.findByCodigoInvitacion(codigo);
		if (reserva.isPresent() && reserva.get().getEstado() != Reserva.Estado.PENDIENTE) {
			auditoriaServ.registrar(null, "INVITACION", "Reserva",
					"Abrió la invitación de " + nombreEspacio(reserva.get().getEspacioId()));
			return deReserva(reserva.get());
		}
		return null;
	}

	/**
	 * Obtiene el enlace público de un evento. Si el evento aún no tiene código, lo
	 * genera.
	 *
	 * @param id ID del evento
	 * @return El enlace, o null si el evento no existe
	 */
	public String getEnlaceEvento(Long id) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		Evento evento = found.get();
		if (evento.getCodigoInvitacion() == null) {
			evento.setCodigoInvitacion(nuevoCodigo());
			eventoRepo.save(evento);
		}
		return enlace(evento.getCodigoInvitacion());
	}

	/**
	 * Obtiene el enlace público de una reserva. Si la reserva aún no tiene código,
	 * lo genera.
	 *
	 * @param id ID de la reserva
	 * @return El enlace, o null si la reserva no existe
	 */
	public String getEnlaceReserva(Long id) {

	    Optional<Reserva> found = reservaRepo.findById(id);

	    if (!found.isPresent()) {
	        return null;
	    }

	    Reserva reserva = found.get();

	    if (reserva.getCodigoInvitacion() == null) {
	        reserva.setCodigoInvitacion(nuevoCodigo());
	        reservaRepo.save(reserva);
	    }

	    return enlace(reserva.getCodigoInvitacion());
	}

	/**
	 * Revisa si el residente puede invitar a una reserva: debe ser suya, estar
	 * aprobada y no haber empezado.
	 *
	 * @param username Correo del residente
	 * @param id       ID de la reserva
	 * @return 0 si puede invitar, 1 si la reserva no existe, 2 si es de otro
	 *         residente, 3 si no está aprobada o ya empezó
	 */
	public int revisarReserva(String username, Long id) {
		Optional<Reserva> found = reservaRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Reserva reserva = found.get();
		Optional<Usuario> usuario = usuarioRepo.findById(reserva.getUsuarioId());
		if (!usuario.isPresent() || !usuario.get().getUsername().equals(username)) {
			return 2;
		}
		if (reserva.getEstado() != Reserva.Estado.APROBADA || !reserva.getInicio().isAfter(LocalDateTime.now())) {
			return 3;
		}
		return 0;
	}

	/**
	 * Envía la invitación de un evento programado a una lista de correos y los
	 * guarda como invitados.
	 *
	 * @param id      ID del evento
	 * @param correos Correos ya revisados
	 * @return Número de correos enviados (0 o más), -1 si el evento no existe, -2
	 *         si fue cancelado o finalizó
	 */
	public int enviarEvento(Long id, List<String> correos) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return -1;
		}
		Evento evento = found.get();
		if (evento.getEstadoActual() != Evento.Estado.PROGRAMADO) {
			return -2;
		}
		String html = plantillaServ.invitacion(evento, getEnlaceEvento(id));
		int enviados = 0;
		for (String correo : limpiar(correos)) {
			if (correoServ.enviarHtml(correo, "Invitación: " + evento.getTitulo(), html)) {
				Optional<Invitado> invitado = invitadoRepo.findByEventoIdAndCorreo(id, correo);
				if (invitado.isPresent()) {
					guardar(invitado.get());
				} else {
					guardar(new Invitado(id, null, correo));
				}
				enviados++;
			}
		}
		auditoriaServ.registrar("INVITACION", "Evento",
				"Envió la invitación de " + evento.getTitulo() + " a " + enviados + " correo(s)");
		return enviados;
	}

	/**
	 * Envía la invitación de una reserva aprobada a una lista de correos y los
	 * guarda como invitados. Antes hay que revisar la reserva con
	 * {@link #revisarReserva(String, Long)}.
	 *
	 * @param username Correo del residente que invita
	 * @param id       ID de la reserva
	 * @param correos  Correos ya revisados
	 * @return Número de correos enviados (0 o más)
	 */
	public int enviarReserva(String username, Long id, List<String> correos) {
		Reserva reserva = reservaRepo.findById(id).get();
		String html = plantillaServ.invitacionReserva(reserva, nombreUsuario(reserva.getUsuarioId()),
				getEnlaceReserva(id));
		String asunto = nombreUsuario(reserva.getUsuarioId()) + " te invita a " + nombreEspacio(reserva.getEspacioId());
		int enviados = 0;
		for (String correo : limpiar(correos)) {
			if (correoServ.enviarHtml(correo, asunto, html)) {
				Optional<Invitado> invitado = invitadoRepo.findByReservaIdAndCorreo(id, correo);
				if (invitado.isPresent()) {
					guardar(invitado.get());
				} else {
					guardar(new Invitado(null, id, correo));
				}
				enviados++;
			}
		}
		auditoriaServ.registrar(username, "INVITACION", "Reserva", "Envió la invitación de su reserva de "
				+ nombreEspacio(reserva.getEspacioId()) + " a " + enviados + " correo(s)");
		return enviados;
	}

	/**
	 * Obtiene los invitados de un evento.
	 *
	 * @param id ID del evento
	 * @return Lista de DTOs de invitados
	 */
	public List<InvitadoDTO> getInvitadosEvento(Long id) {
		return aDTOs(invitadoRepo.findByEventoIdOrderByFechaEnvioDesc(id));
	}

	/**
	 * Obtiene los invitados de una reserva.
	 *
	 * @param id ID de la reserva
	 * @return Lista de DTOs de invitados
	 */
	public List<InvitadoDTO> getInvitadosReserva(Long id) {
		return aDTOs(invitadoRepo.findByReservaIdOrderByFechaEnvioDesc(id));
	}

	/**
	 * Avisa por correo a los invitados de un evento que se canceló.
	 *
	 * @param evento Evento cancelado
	 */
	public void avisarCancelacionEvento(Evento evento) {
		String html = plantillaServ.cancelacionEvento(evento);
		for (Invitado invitado : invitadoRepo.findByEventoIdOrderByFechaEnvioDesc(evento.getId())) {
			correoServ.enviarHtml(invitado.getCorreo(), "Evento cancelado: " + evento.getTitulo(), html);
		}
	}

	/**
	 * Avisa por correo a los invitados de una reserva que se canceló.
	 *
	 * @param reserva Reserva cancelada
	 */
	public void avisarCancelacionReserva(Reserva reserva) {
		String html = plantillaServ.cancelacionReserva(reserva, nombreUsuario(reserva.getUsuarioId()));
		for (Invitado invitado : invitadoRepo.findByReservaIdOrderByFechaEnvioDesc(reserva.getId())) {
			correoServ.enviarHtml(invitado.getCorreo(), "Plan cancelado en " + nombreEspacio(reserva.getEspacioId()),
					html);
		}
	}

	/**
	 * Borra los invitados de un evento eliminado.
	 *
	 * @param eventoId ID del evento
	 */
	public void borrarInvitadosEvento(Long eventoId) {
		for (Invitado invitado : invitadoRepo.findByEventoIdOrderByFechaEnvioDesc(eventoId)) {
			invitadoRepo.delete(invitado);
		}
	}

	/**
	 * Genera un código nuevo para un enlace de invitación.
	 *
	 * @return Código aleatorio de letras y números
	 */
	public String nuevoCodigo() {
		String codigo = "";
		for (int i = 0; i < LARGO_CODIGO; i++) {
			int posicion = aleatorio.nextInt(CARACTERES.length());
			codigo = codigo + CARACTERES.substring(posicion, posicion + 1);
		}
		return codigo;
	}

	/**
	 * Arma el enlace público de un código.
	 *
	 * @param codigo Código de la invitación
	 * @return Enlace a la página de la invitación en el front
	 */
	private String enlace(String codigo) {
		String base = urlFront.trim();
		if (!base.isEmpty() && base.substring(base.length() - 1).equals("/")) {
			base = base.substring(0, base.length() - 1);
		}
		return base + "/invitacion/" + codigo;
	}

	/**
	 * Pasa los correos a minúscula, les quita los espacios y descarta los
	 * repetidos.
	 *
	 * @param correos Correos recibidos
	 * @return Correos sin repetir
	 */
	private List<String> limpiar(List<String> correos) {
		List<String> limpios = new ArrayList<>();
		for (String correo : correos) {
			String limpio = correo.trim().toLowerCase();
			if (!limpios.contains(limpio)) {
				limpios.add(limpio);
			}
		}
		return limpios;
	}

	/**
	 * Guarda un invitado con la fecha de envío actual.
	 *
	 * @param invitado Invitado a guardar
	 */
	private void guardar(Invitado invitado) {
		invitado.setFechaEnvio(LocalDateTime.now());
		invitadoRepo.save(invitado);
	}

	/**
	 * Convierte una lista de invitados en DTOs.
	 *
	 * @param invitados Invitados a convertir
	 * @return Lista de DTOs
	 */
	private List<InvitadoDTO> aDTOs(List<Invitado> invitados) {
		List<InvitadoDTO> dtoList = new ArrayList<>();
		for (Invitado invitado : invitados) {
			dtoList.add(new InvitadoDTO(invitado.getCorreo(), FechaUtil.fechaHora(invitado.getFechaEnvio())));
		}
		return dtoList;
	}

	/**
	 * Arma la invitación pública de un evento.
	 *
	 * @param evento Evento
	 * @return DTO de la invitación
	 */
	private InvitacionDTO deEvento(Evento evento) {
		InvitacionDTO dto = new InvitacionDTO();
		dto.setTipo("EVENTO");
		dto.setTitulo(evento.getTitulo());
		dto.setDescripcion(evento.getDescripcion());
		dto.setFecha(FechaUtil.fecha(evento.getFecha()));
		dto.setHoraInicio(FechaUtil.hora(evento.getHoraInicio()));
		dto.setHoraFin(FechaUtil.hora(evento.getHoraFin()));
		dto.setPlantilla(evento.getPlantilla());
		dto.setEstado(estadoEvento(evento));
		dto.setMotivoCancelacion(evento.getMotivoCancelacion());
		if (evento.getEspacioId() != null) {
			dto.setEspacioNombre(nombreEspacio(evento.getEspacioId()));
		}
		ponerConjunto(dto, evento.getConjuntoId());
		dto.setAnfitrion(dto.getConjuntoNombre());
		return dto;
	}

	/**
	 * Arma la invitación pública de una reserva.
	 *
	 * @param reserva Reserva
	 * @return DTO de la invitación
	 */
	private InvitacionDTO deReserva(Reserva reserva) {
		InvitacionDTO dto = new InvitacionDTO();
		Optional<Espacio> espacio = espacioRepo.findById(reserva.getEspacioId());
		dto.setTipo("RESERVA");
		dto.setTitulo(reserva.getMotivo());
		dto.setAnfitrion(nombreUsuario(reserva.getUsuarioId()));
		dto.setFecha(FechaUtil.fecha(reserva.getFecha()));
		dto.setHoraInicio(FechaUtil.hora(reserva.getHoraInicio()));
		dto.setHoraFin(FechaUtil.hora(reserva.getHoraFin()));
		dto.setPlantilla(Evento.Plantilla.CLASICO);
		dto.setEstado(estadoReserva(reserva));
		dto.setEspacioNombre(nombreEspacio(reserva.getEspacioId()));
		if (espacio.isPresent()) {
			ponerConjunto(dto, espacio.get().getConjuntoId());
		}
		return dto;
	}

	/**
	 * Copia en la invitación los datos del conjunto que se necesitan para el mapa.
	 *
	 * @param dto        Invitación
	 * @param conjuntoId ID del conjunto (puede ser null)
	 */
	private void ponerConjunto(InvitacionDTO dto, Long conjuntoId) {
		if (conjuntoId == null) {
			return;
		}
		Optional<Conjunto> found = conjuntoRepo.findById(conjuntoId);
		if (found.isPresent()) {
			Conjunto conjunto = found.get();
			dto.setConjuntoId(conjunto.getId());
			dto.setConjuntoNombre(conjunto.getNombre());
			dto.setDireccion(conjunto.getDireccion());
			dto.setMunicipio(conjunto.getMunicipio());
			dto.setLatitud(conjunto.getLatitud());
			dto.setLongitud(conjunto.getLongitud());
		}
	}

	/**
	 * Traduce el estado de un evento al de la invitación.
	 *
	 * @param evento Evento
	 * @return VIGENTE, CANCELADO o FINALIZADO
	 */
	private String estadoEvento(Evento evento) {
		if (evento.getEstadoActual() == Evento.Estado.CANCELADO) {
			return "CANCELADO";
		}
		if (evento.getEstadoActual() == Evento.Estado.FINALIZADO) {
			return "FINALIZADO";
		}
		return "VIGENTE";
	}

	/**
	 * Traduce el estado de una reserva al de la invitación.
	 *
	 * @param reserva Reserva
	 * @return VIGENTE, CANCELADO o FINALIZADO
	 */
	private String estadoReserva(Reserva reserva) {
		if (reserva.getEstado() != Reserva.Estado.APROBADA) {
			return "CANCELADO";
		}
		boolean termino = LocalDateTime.of(reserva.getFecha(), reserva.getHoraFin()).isBefore(LocalDateTime.now());
		if (termino) {
			return "FINALIZADO";
		}
		return "VIGENTE";
	}

	/**
	 * Busca el nombre completo de un usuario.
	 *
	 * @param id ID del usuario
	 * @return Nombre y apellido, o "Un residente" si ya no existe
	 */
	private String nombreUsuario(Long id) {
		Optional<Usuario> found = usuarioRepo.findById(id);
		if (found.isPresent()) {
			return found.get().getNombreCompleto();
		}
		return "Un residente";
	}

	/**
	 * Busca el nombre de un espacio.
	 *
	 * @param id ID del espacio
	 * @return Nombre del espacio, o "un espacio común" si ya no existe
	 */
	private String nombreEspacio(Long id) {
		Optional<Espacio> found = espacioRepo.findById(id);
		if (found.isPresent()) {
			return found.get().getNombre();
		}
		return "un espacio común";
	}
}
