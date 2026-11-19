package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.FranjaHorariaDTO;
import co.edu.unbosque.GreenBuilding.dto.ReservaDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Reserva.Estado;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.FechaUtil;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para las reservas de espacios comunes: disponibilidad por franjas de una hora, creación
 * con validación de cruces y horarios, cancelación por el residente y revisión por el
 * administrador.
 */
@Service
public class ReservaService {

	/** Duración de cada franja del calendario, en minutos. */
	private static final int MINUTOS_POR_FRANJA = 60;

	/** Días máximos de anticipación para reservar. */
	public static final int DIAS_MAXIMOS_ANTICIPACION = 60;

	/** Repositorio para acceder a las reservas. */
	@Autowired private ReservaRepository reservaRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired private EspacioRepository espacioRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired private UsuarioRepository usuarioRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a las unidades. */
	@Autowired private UnidadRepository unidadRepo;

	/** Mapeador para convertir entre entidades y DTOs. */
	@Autowired private ModelMapper modelMapper;

	/** Servicio de auditoría. */
	@Autowired private AuditoriaService auditoriaServ;

	/** Servicio de correo. */
	@Autowired private CorreoService correoServ;

	/** Servicio que arma el HTML de los correos. */
	@Autowired private PlantillaCorreoService plantillaServ;

	/** Repositorio para acceder a los eventos, para no cruzar una reserva con un evento. */
	@Autowired private EventoRepository eventoRepo;

	/** Servicio de invitaciones: aviso a los invitados si la reserva se cancela. */
	@Autowired private InvitacionService invitacionServ;

	/** Constructor por defecto. */
	public ReservaService() {}

	/**
	 * Cuenta el número total de reservas.
	 *
	 * @return Número total de reservas
	 */
	public long count() {
		return reservaRepo.count();
	}

	/**
	 * Obtiene todas las reservas, de la más reciente a la más antigua.
	 *
	 * @return Lista de DTOs de reservas
	 */
	public List<ReservaDTO> getAll() {
		return aDTOs(reservaRepo.findAllByOrderByFechaDescHoraInicioDesc());
	}

	/**
	 * Obtiene las reservas con un estado. Si el estado está vacío, devuelve todas.
	 *
	 * @param estado PENDIENTE, APROBADA, RECHAZADA o CANCELADA (opcional)
	 * @return Lista de DTOs de reservas
	 */
	public List<ReservaDTO> getAll(Estado estado) {
		if (estado == null) {
			return getAll();
		}
		return aDTOs(reservaRepo.findByEstadoOrderByFechaDescHoraInicioDesc(estado));
	}

	/**
	 * Obtiene las reservas del residente con la sesión iniciada.
	 *
	 * @param username Correo del residente
	 * @return Lista de DTOs de reservas
	 */
	public List<ReservaDTO> getMine(String username) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent()) {
			return new ArrayList<>();
		}
		return aDTOs(reservaRepo.findByUsuarioIdOrderByFechaDescHoraInicioDesc(found.get().getId()));
	}

	/**
	 * Obtiene una reserva por su ID.
	 *
	 * @param id ID de la reserva
	 * @return DTO de la reserva si existe, null en caso contrario
	 */
	public ReservaDTO getById(Long id) {
		Optional<Reserva> found = reservaRepo.findById(id);
		if (found.isPresent()) {
			return aDTO(found.get());
		} else {
			return null;
		}
	}

	/**
	 * Arma las franjas de una hora de un espacio en un día, marcando las ocupadas por reservas
	 * pendientes o aprobadas, por eventos programados y las que ya pasaron.
	 *
	 * @param espacioId ID del espacio
	 * @param fecha Fecha AAAA-MM-DD
	 * @return Lista de franjas, vacía si el espacio es de uso libre, o null si el espacio no existe
	 */
	public List<FranjaHorariaDTO> getDisponibilidad(Long espacioId, String fecha) {
		Optional<Espacio> found = espacioRepo.findById(espacioId);
		if (!found.isPresent()) {
			return null;
		}
		Espacio espacio = found.get();
		List<FranjaHorariaDTO> franjas = new ArrayList<>();
		if (!espacio.isRequiereReserva()) {
			return franjas;
		}
		LocalDate dia = FechaUtil.leerFecha(fecha);
		List<Reserva> delDia = reservaRepo.findByEspacioIdAndFecha(espacioId, dia);
		LocalDateTime ahora = LocalDateTime.now();
		for (LocalTime inicio = espacio.getHoraApertura();
				inicio.isBefore(espacio.getHoraCierre());
				inicio = finDeFranja(inicio, espacio)) {
			LocalTime fin = finDeFranja(inicio, espacio);
			boolean ocupada = false;
			for (Reserva reserva : delDia) {
				if (reserva.estaActiva() && reserva.seCruzaCon(inicio, fin)) {
					ocupada = true;
				}
			}
			for (Evento evento : eventoRepo.findByEspacioIdAndFecha(espacioId, dia)) {
				if (evento.getEstadoActual() == Evento.Estado.PROGRAMADO && evento.seCruzaCon(inicio, fin)) {
					ocupada = true;
				}
			}
			boolean pasada = !LocalDateTime.of(dia, inicio).isAfter(ahora);
			franjas.add(
					new FranjaHorariaDTO(
							FechaUtil.hora(inicio),
							FechaUtil.hora(fin),
							espacio.isDisponible() && !ocupada && !pasada));
		}
		return franjas;
	}

	/**
	 * Calcula la hora en que termina una franja: una hora después del inicio, o la hora de cierre
	 * del espacio si llega antes.
	 *
	 * @param inicio Hora de inicio de la franja
	 * @param espacio Espacio con su hora de cierre
	 * @return Hora de fin de la franja
	 */
	private LocalTime finDeFranja(LocalTime inicio, Espacio espacio) {
		LocalTime fin = inicio.plusMinutes(MINUTOS_POR_FRANJA);
		if (fin.isAfter(espacio.getHoraCierre()) || !fin.isAfter(inicio)) {
			fin = espacio.getHoraCierre();
		}
		return fin;
	}

	/**
	 * Crea una reserva en estado PENDIENTE para el residente con la sesión iniciada.
	 *
	 * @param username Correo del residente
	 * @param data DTO con el espacio, la fecha, las horas, el motivo y los asistentes
	 * @return 0 si la reserva se creó, 1 si el residente no tiene unidad, 2 si el espacio no existe,
	 *     3 si el espacio es de otro conjunto, 4 si el espacio es de uso libre, 5 si el espacio no
	 *     está disponible, 6 si está fuera del horario del espacio, 7 si la fecha u hora ya pasó, 8
	 *     si supera los días de anticipación, 9 si supera la capacidad, 10 si se cruza con otra
	 *     reserva, 11 si se cruza con un evento programado en el espacio
	 */
	public int create(String username, ReservaDTO data) {
		Optional<Usuario> usuario = usuarioRepo.findByUsername(username);
		if (!usuario.isPresent() || usuario.get().getUnidadId() == null) {
			return 1;
		}
		Optional<Espacio> found = espacioRepo.findById(data.getEspacioId());
		if (!found.isPresent()) {
			return 2;
		}
		Espacio espacio = found.get();
		if (!espacio.getConjuntoId().equals(usuario.get().getConjuntoId())) {
			return 3;
		}
		if (!espacio.isRequiereReserva()) {
			return 4;
		}
		if (!espacio.isDisponible()) {
			return 5;
		}
		Reserva entity = new Reserva();
		entity.setEspacioId(espacio.getId());
		entity.setUsuarioId(usuario.get().getId());
		entity.setFecha(FechaUtil.leerFecha(data.getFecha()));
		entity.setHoraInicio(FechaUtil.leerHora(data.getHoraInicio()));
		entity.setHoraFin(FechaUtil.leerHora(data.getHoraFin()));
		entity.setMotivo(TextoUtil.limpiar(data.getMotivo()));
		entity.setAsistentes(data.getAsistentes());
		int validacion = validarFechaYCupo(entity);
		if (validacion != 0) {
			return validacion;
		}
		if (seCruza(entity, false)) {
			return 10;
		}
		if (hayEvento(entity)) {
			return 11;
		}
		reservaRepo.save(entity);
		auditoriaServ.registrar(
				username,
				"RESERVA",
				"Reserva",
				"Solicitó "
						+ espacio.getNombre()
						+ " el "
						+ entity.getFecha()
						+ " de "
						+ data.getHoraInicio()
						+ " a "
						+ data.getHoraFin());
		return 0;
	}

	/**
	 * Cancela una reserva del residente con la sesión iniciada. Si estaba aprobada, avisa a sus
	 * invitados.
	 *
	 * @param username Correo del residente
	 * @param id ID de la reserva
	 * @return 0 si se canceló, 1 si la reserva no existe, 2 si la reserva es de otro residente, 3 si
	 *     ya no se puede cancelar
	 */
	public int cancelar(String username, Long id) {
		Optional<Reserva> found = reservaRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Reserva temp = found.get();
		if (!username.equals(usuarioDe(temp).getUsername())) {
			return 2;
		}
		if (!temp.estaActiva() || !temp.getInicio().isAfter(LocalDateTime.now())) {
			return 3;
		}
		boolean estabaAprobada = temp.getEstado() == Estado.APROBADA;
		temp.setEstado(Estado.CANCELADA);
		reservaRepo.save(temp);
		if (estabaAprobada) {
			invitacionServ.avisarCancelacionReserva(temp);
		}
		auditoriaServ.registrar(
				username, "RESERVA", "Reserva", "Canceló su reserva de " + espacioDe(temp).getNombre());
		return 0;
	}

	/**
	 * Aprueba una reserva pendiente y avisa al residente por correo.
	 *
	 * @param id ID de la reserva
	 * @return 0 si se aprobó, 1 si la reserva no existe, 2 si no está pendiente, 3 si su fecha ya
	 *     pasó, 4 si se cruza con otra reserva aprobada o con un evento programado
	 */
	public int aprobar(Long id) {
		Optional<Reserva> found = reservaRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Reserva temp = found.get();
		int revision = revisarPendiente(temp);
		if (revision != 0) {
			return revision;
		}
		if (seCruza(temp, true) || hayEvento(temp)) {
			return 4;
		}
		temp.setEstado(Estado.APROBADA);
		temp.setObservacion(null);
		reservaRepo.save(temp);
		correoServ.enviarHtml(
				usuarioDe(temp).getUsername(), "Tu reserva fue aprobada", plantillaServ.estadoReserva(temp));
		auditoriaServ.registrar(
				"RESERVA",
				"Reserva",
				"Aprobó la reserva de "
						+ usuarioDe(temp).getNombreCompleto()
						+ " en "
						+ espacioDe(temp).getNombre());
		return 0;
	}

	/**
	 * Rechaza una reserva pendiente con una observación y avisa al residente por correo.
	 *
	 * @param id ID de la reserva
	 * @param observacion Motivo del rechazo
	 * @return 0 si se rechazó, 1 si la reserva no existe, 2 si no está pendiente, 3 si su fecha ya
	 *     pasó
	 */
	public int rechazar(Long id, String observacion) {
		Optional<Reserva> found = reservaRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Reserva temp = found.get();
		int revision = revisarPendiente(temp);
		if (revision != 0) {
			return revision;
		}
		temp.setEstado(Estado.RECHAZADA);
		temp.setObservacion(TextoUtil.limpiar(observacion));
		reservaRepo.save(temp);
		correoServ.enviarHtml(
				usuarioDe(temp).getUsername(), "Tu reserva fue rechazada", plantillaServ.estadoReserva(temp));
		auditoriaServ.registrar(
				"RESERVA",
				"Reserva",
				"Rechazó la reserva de "
						+ usuarioDe(temp).getNombreCompleto()
						+ " en "
						+ espacioDe(temp).getNombre()
						+ ": "
						+ temp.getObservacion());
		return 0;
	}

	/**
	 * Revisa que una reserva esté pendiente y que su fecha no haya pasado.
	 *
	 * @param reserva Reserva a revisar
	 * @return 0 si se puede revisar, 2 si no está pendiente, 3 si ya pasó
	 */
	private int revisarPendiente(Reserva reserva) {
		if (reserva.getEstado() != Estado.PENDIENTE) {
			return 2;
		}
		if (!reserva.getInicio().isAfter(LocalDateTime.now())) {
			return 3;
		}
		return 0;
	}

	/**
	 * Revisa el horario, la fecha y la capacidad de una reserva nueva.
	 *
	 * @param reserva Reserva a revisar
	 * @return 0 si es válida, 6 si está fuera del horario, 7 si ya pasó, 8 si supera la
	 *     anticipación, 9 si supera la capacidad
	 */
	private int validarFechaYCupo(Reserva reserva) {
		Espacio espacio = espacioDe(reserva);
		if (reserva.getHoraInicio().isBefore(espacio.getHoraApertura())
				|| reserva.getHoraFin().isAfter(espacio.getHoraCierre())) {
			return 6;
		}
		if (!reserva.getInicio().isAfter(LocalDateTime.now())) {
			return 7;
		}
		if (reserva.getFecha().isAfter(LocalDate.now().plusDays(DIAS_MAXIMOS_ANTICIPACION))) {
			return 8;
		}
		if (reserva.getAsistentes() > espacio.getCapacidad()) {
			return 9;
		}
		return 0;
	}

	/**
	 * Indica si una reserva se cruza con otra del mismo espacio y día.
	 *
	 * @param reserva Reserva a revisar
	 * @param soloAprobadas true para comparar solo con reservas aprobadas
	 * @return true si hay cruce
	 */
	private boolean seCruza(Reserva reserva, boolean soloAprobadas) {
		for (Reserva otra : reservaRepo.findByEspacioIdAndFecha(reserva.getEspacioId(), reserva.getFecha())) {
			boolean cuenta = otra.estaActiva();
			if (soloAprobadas) {
				cuenta = otra.getEstado() == Estado.APROBADA;
			}
			boolean esLaMisma = reserva.getId() != null && reserva.getId().equals(otra.getId());
			if (cuenta && !esLaMisma && otra.seCruzaCon(reserva.getHoraInicio(), reserva.getHoraFin())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Indica si una reserva se cruza con un evento programado en el mismo espacio y día.
	 *
	 * @param reserva Reserva a revisar
	 * @return true si hay cruce
	 */
	private boolean hayEvento(Reserva reserva) {
		for (Evento evento : eventoRepo.findByEspacioIdAndFecha(reserva.getEspacioId(), reserva.getFecha())) {
			if (evento.getEstadoActual() == Evento.Estado.PROGRAMADO
					&& evento.seCruzaCon(reserva.getHoraInicio(), reserva.getHoraFin())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convierte una lista de reservas en DTOs.
	 *
	 * @param reservas Reservas a convertir
	 * @return Lista de DTOs
	 */
	private List<ReservaDTO> aDTOs(List<Reserva> reservas) {
		List<ReservaDTO> dtoList = new ArrayList<>();
		reservas.forEach((entity) -> dtoList.add(aDTO(entity)));
		return dtoList;
	}

	/**
	 * Convierte una reserva en DTO con los nombres del conjunto, el residente y su unidad.
	 *
	 * @param reserva Reserva a convertir
	 * @return DTO de la reserva
	 */
	private ReservaDTO aDTO(Reserva reserva) {
		ReservaDTO dto = modelMapper.map(reserva, ReservaDTO.class);
		dto.setFecha(FechaUtil.fecha(reserva.getFecha()));
		dto.setHoraInicio(FechaUtil.hora(reserva.getHoraInicio()));
		dto.setHoraFin(FechaUtil.hora(reserva.getHoraFin()));
		dto.setFechaCreacion(FechaUtil.fechaHora(reserva.getFechaCreacion()));
		Espacio espacio = espacioDe(reserva);
		Usuario usuario = usuarioDe(reserva);
		dto.setEspacioNombre(espacio.getNombre());
		dto.setConjuntoNombre(nombreConjunto(espacio.getConjuntoId()));
		dto.setUsuarioNombre(usuario.getNombreCompleto());
		dto.setUnidadDescripcion(descripcionUnidad(usuario.getUnidadId()));
		return dto;
	}

	/**
	 * Busca el espacio de una reserva.
	 *
	 * @param reserva Reserva
	 * @return El espacio, o un espacio vacío si ya no existe
	 */
	private Espacio espacioDe(Reserva reserva) {
		Optional<Espacio> found = espacioRepo.findById(reserva.getEspacioId());
		if (found.isPresent()) {
			return found.get();
		}
		return new Espacio();
	}

	/**
	 * Busca el residente que hizo una reserva.
	 *
	 * @param reserva Reserva
	 * @return El residente, o un usuario vacío si ya no existe
	 */
	private Usuario usuarioDe(Reserva reserva) {
		Optional<Usuario> found = usuarioRepo.findById(reserva.getUsuarioId());
		if (found.isPresent()) {
			return found.get();
		}
		return new Usuario();
	}

	/**
	 * Busca el nombre de un conjunto por su identificador.
	 *
	 * @param id Identificador a buscar (puede ser null)
	 * @return El nombre, o null si no existe
	 */
	private String nombreConjunto(Long id) {
		if (id == null) {
			return null;
		}
		Optional<Conjunto> found = conjuntoRepo.findById(id);
		if (found.isPresent()) {
			return found.get().getNombre();
		}
		return null;
	}

	/**
	 * Busca la descripción de una unidad por su identificador.
	 *
	 * @param id Identificador de la unidad (puede ser null)
	 * @return La descripción, o "Sin unidad" si no tiene o ya no existe
	 */
	private String descripcionUnidad(Long id) {
		if (id == null) {
			return "Sin unidad";
		}
		Optional<Unidad> found = unidadRepo.findById(id);
		if (found.isPresent()) {
			return found.get().getDescripcion();
		}
		return "Sin unidad";
	}
}
