package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.EventoDTO;
import co.edu.unbosque.GreenBuilding.model.Asistencia;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Evento.Estado;
import co.edu.unbosque.GreenBuilding.model.Evento.Tipo;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.FechaUtil;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona operaciones CRUD para los eventos de los conjuntos,
 * la asistencia de los residentes y las invitaciones por correo. Implementa la
 * interfaz CRUDOperation.
 */
@Service
public class EventoService implements CRUDOperation<EventoDTO> {

	/** Repositorio para acceder a los eventos. */
	@Autowired
	private EventoRepository eventoRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired
	private EspacioRepository espacioRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired
	private UsuarioRepository usuarioRepo;

	/** Repositorio para acceder a las asistencias a eventos. */
	@Autowired
	private AsistenciaRepository asistenciaRepo;

	/** Mapeador para convertir entre entidades y DTOs. */
	@Autowired
	private ModelMapper modelMapper;

	/** Servicio de auditoría. */
	@Autowired
	private AuditoriaService auditoriaServ;

	/** Servicio de correo. */
	@Autowired
	private CorreoService correoServ;

	/** Servicio que arma el HTML de los correos. */
	@Autowired
	private PlantillaCorreoService plantillaServ;

	/** Constructor por defecto. */
	public EventoService() {
	}

	/**
	 * Cuenta el número total de eventos.
	 *
	 * @return Número total de eventos
	 */
	@Override
	public long count() {
		return eventoRepo.count();
	}

	/**
	 * Verifica si existe un evento con el ID especificado.
	 *
	 * @param id ID del evento
	 * @return true si existe, false en caso contrario
	 */
	@Override
	public boolean exist(Long id) {
		return eventoRepo.existsById(id);
	}

	/**
	 * Crea un nuevo evento en estado PROGRAMADO.
	 *
	 * @param data DTO con los datos del evento
	 * @return 0 si la creación fue exitosa, 1 si el conjunto no existe, 2 si el
	 *         espacio no existe o es de otro conjunto, 3 si la fecha y hora ya
	 *         pasaron
	 */
	@Override
	public int create(EventoDTO data) {
		Evento entity = new Evento();
		int resultado = aplicar(entity, data);
		if (resultado != 0) {
			return resultado;
		}
		eventoRepo.save(entity);
		auditoriaServ.registrar("EVENTO", "Evento",
				"Creó el evento " + entity.getTitulo() + " en " + nombreConjunto(entity.getConjuntoId()));
		return 0;
	}

	/**
	 * Obtiene todos los eventos, del más reciente al más antiguo.
	 *
	 * @return Lista de DTOs de eventos
	 */
	@Override
	public List<EventoDTO> getAll() {
		return aDTOs(eventoRepo.findAllByOrderByFechaDescHoraInicioDesc(), null);
	}

	/**
	 * Obtiene los eventos de un conjunto, o todos si no se indica conjunto.
	 *
	 * @param conjuntoId ID del conjunto (opcional)
	 * @return Lista de DTOs de eventos, o null si el conjunto no existe
	 */
	public List<EventoDTO> getAll(Long conjuntoId) {
		if (conjuntoId == null) {
			return getAll();
		}
		Optional<Conjunto> conjunto = conjuntoRepo.findById(conjuntoId);
		if (!conjunto.isPresent()) {
			return null;
		}
		return aDTOs(eventoRepo.findByConjuntoIdOrderByFechaDescHoraInicioDesc(conjuntoId), null);
	}

	/**
	 * Obtiene los eventos de hoy en adelante de un conjunto, con filtros, indicando
	 * si el usuario asistirá.
	 *
	 * @param username   Correo del usuario
	 * @param conjuntoId ID del conjunto
	 * @param tipo       Tipo de evento (opcional)
	 * @param paraNinos  true para ver solo planes para niños
	 * @return Lista de DTOs de eventos, o null si el conjunto no existe
	 */
	public List<EventoDTO> getByConjunto(String username, Long conjuntoId, Tipo tipo, boolean paraNinos) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(conjuntoId);
		if (!conjunto.isPresent()) {
			return null;
		}
		Usuario usuario = usuarioRepo.findByUsername(username).orElse(null);
		LocalDate hoy = LocalDate.now();
		List<Evento> eventos = new ArrayList<>();
		for (Evento evento : eventoRepo.findByConjuntoIdOrderByFechaAscHoraInicioAsc(conjuntoId)) {
			boolean vigente = !evento.getFecha().isBefore(hoy);
			boolean delTipo = tipo == null || evento.getTipo() == tipo;
			boolean paraElPublico = !paraNinos || evento.isParaNinos();
			if (vigente && delTipo && paraElPublico) {
				eventos.add(evento);
			}
		}
		return aDTOs(eventos, usuario);
	}

	/**
	 * Obtiene un evento por su ID.
	 *
	 * @param id ID del evento
	 * @return DTO del evento si existe, null en caso contrario
	 */
	public EventoDTO getById(Long id) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (found.isPresent()) {
			return aDTO(found.get(), null);
		} else {
			return null;
		}
	}

	/**
	 * Elimina un evento y la asistencia de sus residentes.
	 *
	 * @param id ID del evento
	 * @return 0 si la eliminación fue exitosa, 1 si el evento no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		asistenciaRepo.deleteAll(asistenciaRepo.findByEventoId(id));
		eventoRepo.delete(found.get());
		auditoriaServ.registrar("ELIMINAR", "Evento", "Eliminó el evento " + found.get().getTitulo());
		return 0;
	}

	/**
	 * Actualiza un evento programado.
	 *
	 * @param id      ID del evento
	 * @param newData DTO con los nuevos datos
	 * @return 0 si la actualización fue exitosa, 1 si el conjunto no existe, 2 si
	 *         el espacio no existe o es de otro conjunto, 3 si la fecha y hora ya
	 *         pasaron, 4 si el evento no existe, 5 si el evento fue cancelado o ya
	 *         finalizó, 6 si el cupo es menor que los inscritos
	 */
	@Override
	public int updateById(Long id, EventoDTO newData) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return 4;
		}
		Evento temp = found.get();
		if (temp.getEstadoActual() != Estado.PROGRAMADO) {
			return 5;
		}
		if (newData.getCupoMaximo() != null && newData.getCupoMaximo() < asistenciaRepo.countByEventoId(id)) {
			return 6;
		}
		int resultado = aplicar(temp, newData);
		if (resultado != 0) {
			return resultado;
		}
		eventoRepo.save(temp);
		auditoriaServ.registrar("EVENTO", "Evento", "Actualizó el evento " + temp.getTitulo());
		return 0;
	}

	/**
	 * Cancela un evento programado y avisa por correo a los inscritos.
	 *
	 * @param id     ID del evento
	 * @param motivo Motivo de la cancelación
	 * @return 0 si se canceló, 1 si el evento no existe, 2 si ya estaba cancelado o
	 *         finalizó
	 */
	public int cancelar(Long id, String motivo) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Evento temp = found.get();
		if (temp.getEstadoActual() != Estado.PROGRAMADO) {
			return 2;
		}
		temp.setEstado(Estado.CANCELADO);
		temp.setMotivoCancelacion(TextoUtil.limpiar(motivo));
		eventoRepo.save(temp);
		String html = plantillaServ.cancelacionEvento(temp);
		for (Asistencia asistencia : asistenciaRepo.findByEventoId(id)) {
			Optional<Usuario> asistente = usuarioRepo.findById(asistencia.getUsuarioId());
			if (asistente.isPresent()) {
				correoServ.enviarHtml(asistente.get().getUsername(), "Evento cancelado: " + temp.getTitulo(), html);
			}
		}
		auditoriaServ.registrar("EVENTO", "Evento",
				"Canceló el evento " + temp.getTitulo() + ": " + temp.getMotivoCancelacion());
		return 0;
	}

	/**
	 * Inscribe al residente con la sesión iniciada en un evento de su conjunto.
	 *
	 * @param username Correo del residente
	 * @param id       ID del evento
	 * @return 0 si quedó inscrito, 1 si el evento no existe, 2 si fue cancelado o
	 *         finalizó, 3 si el usuario no es residente del conjunto, 4 si el cupo
	 *         está lleno, 5 si ya estaba inscrito
	 */
	public int asistir(String username, Long id) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Evento temp = found.get();
		if (temp.getEstadoActual() != Estado.PROGRAMADO) {
			return 2;
		}
		Usuario usuario = residenteDelEvento(username, temp);
		if (usuario == null) {
			return 3;
		}
		if (asistenciaRepo.findByEventoIdAndUsuarioId(id, usuario.getId()).isPresent()) {
			return 5;
		}
		if (estaLleno(temp)) {
			return 4;
		}
		asistenciaRepo.save(new Asistencia(id, usuario.getId()));
		auditoriaServ.registrar(username, "EVENTO", "Evento", "Confirmó asistencia a " + temp.getTitulo());
		return 0;
	}

	/**
	 * Cancela la asistencia del residente con la sesión iniciada a un evento.
	 *
	 * @param username Correo del residente
	 * @param id       ID del evento
	 * @return 0 si se canceló la asistencia, 1 si el evento no existe, 2 si fue
	 *         cancelado o finalizó, 3 si el usuario no es residente del conjunto
	 */
	public int noAsistir(String username, Long id) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Evento temp = found.get();
		if (temp.getEstadoActual() != Estado.PROGRAMADO) {
			return 2;
		}
		Usuario usuario = residenteDelEvento(username, temp);
		if (usuario == null) {
			return 3;
		}
		Optional<Asistencia> asistencia = asistenciaRepo.findByEventoIdAndUsuarioId(id, usuario.getId());
		if (asistencia.isPresent()) {
			asistenciaRepo.delete(asistencia.get());
		}
		auditoriaServ.registrar(username, "EVENTO", "Evento", "Canceló su asistencia a " + temp.getTitulo());
		return 0;
	}

	/**
	 * Envía la invitación del evento, con su plantilla, a todos los residentes
	 * activos del conjunto.
	 *
	 * @param id ID del evento
	 * @return Número de correos enviados (0 o más), -1 si el evento no existe, -2
	 *         si fue cancelado o finalizó, -3 si el conjunto no tiene residentes
	 *         activos
	 */
	public int enviarInvitacion(Long id) {
		Optional<Evento> found = eventoRepo.findById(id);
		if (!found.isPresent()) {
			return -1;
		}
		Evento temp = found.get();
		if (temp.getEstadoActual() != Estado.PROGRAMADO) {
			return -2;
		}
		List<Usuario> residentes = usuarioRepo
				.findByConjuntoIdAndUnidadIdIsNotNullAndActivoTrueAndVerificadoTrue(temp.getConjuntoId());
		if (residentes.isEmpty()) {
			return -3;
		}
		String html = plantillaServ.invitacion(temp);
		int enviados = 0;
		for (Usuario residente : residentes) {
			if (correoServ.enviarHtml(residente.getUsername(), "Invitación: " + temp.getTitulo(), html)) {
				enviados++;
			}
		}
		auditoriaServ.registrar("EVENTO", "Evento",
				"Envió la invitación de " + temp.getTitulo() + " a " + enviados + " residente(s)");
		return enviados;
	}

	/**
	 * Arma el HTML de la invitación sin guardar el evento.
	 *
	 * @param data DTO con los datos del evento
	 * @return HTML de la invitación, o null si el conjunto no existe
	 */
	public String vistaPrevia(EventoDTO data) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(data.getConjuntoId());
		if (!conjunto.isPresent()) {
			return null;
		}
		Evento temp = new Evento();
		temp.setConjuntoId(conjunto.get().getId());
		if (data.getEspacioId() != null && espacioRepo.existsById(data.getEspacioId())) {
			temp.setEspacioId(data.getEspacioId());
		}
		temp.setTitulo(data.getTitulo());
		temp.setDescripcion(data.getDescripcion());
		temp.setTipo(data.getTipo());
		temp.setFecha(FechaUtil.leerFecha(data.getFecha()));
		temp.setHoraInicio(FechaUtil.leerHora(data.getHoraInicio()));
		temp.setHoraFin(FechaUtil.leerHora(data.getHoraFin()));
		temp.setParaNinos(data.isParaNinos());
		temp.setCupoMaximo(data.getCupoMaximo());
		temp.setPlantilla(data.getPlantilla());
		return plantillaServ.invitacion(temp);
	}

	/**
	 * Obtiene los eventos programados de un conjunto, del más próximo al más
	 * lejano.
	 *
	 * @param conjuntoId ID del conjunto
	 * @return Lista de eventos programados
	 */
	public List<Evento> proximosDelConjunto(Long conjuntoId) {
		List<Evento> eventos = new ArrayList<>();
		for (Evento evento : eventoRepo.findByConjuntoIdOrderByFechaAscHoraInicioAsc(conjuntoId)) {
			if (evento.getEstadoActual() == Estado.PROGRAMADO) {
				eventos.add(evento);
			}
		}
		return eventos;
	}

	/**
	 * Indica si un evento ya completó su cupo.
	 *
	 * @param evento Evento a revisar
	 * @return true si tiene cupo máximo y ya está lleno
	 */
	public boolean estaLleno(Evento evento) {
		return evento.getCupoMaximo() != null
				&& asistenciaRepo.countByEventoId(evento.getId()) >= evento.getCupoMaximo();
	}

	/**
	 * Convierte un evento en DTO con los nombres del conjunto y el espacio, el
	 * total de asistentes, su estado actual y si el usuario asistirá.
	 *
	 * @param evento  Evento a convertir
	 * @param usuario Usuario actual, o null
	 * @return DTO del evento
	 */
	public EventoDTO aDTO(Evento evento, Usuario usuario) {
		EventoDTO dto = modelMapper.map(evento, EventoDTO.class);
		dto.setFecha(FechaUtil.fecha(evento.getFecha()));
		dto.setHoraInicio(FechaUtil.hora(evento.getHoraInicio()));
		dto.setHoraFin(FechaUtil.hora(evento.getHoraFin()));
		dto.setConjuntoNombre(nombreConjunto(evento.getConjuntoId()));
		dto.setEspacioNombre(nombreEspacio(evento.getEspacioId()));
		dto.setTotalAsistentes((int) asistenciaRepo.countByEventoId(evento.getId()));
		dto.setAsistire(usuario != null
				&& asistenciaRepo.findByEventoIdAndUsuarioId(evento.getId(), usuario.getId()).isPresent());
		dto.setEstado(evento.getEstadoActual());
		return dto;
	}

	/**
	 * Convierte una lista de eventos en DTOs.
	 *
	 * @param eventos Eventos a convertir
	 * @param usuario Usuario actual, o null
	 * @return Lista de DTOs
	 */
	private List<EventoDTO> aDTOs(List<Evento> eventos, Usuario usuario) {
		List<EventoDTO> dtoList = new ArrayList<>();
		eventos.forEach((entity) -> dtoList.add(aDTO(entity, usuario)));
		return dtoList;
	}

	/**
	 * Busca al usuario y revisa que sea residente del conjunto del evento.
	 *
	 * @param username Correo del usuario
	 * @param evento   Evento
	 * @return El usuario, o null si no es residente del conjunto
	 */
	private Usuario residenteDelEvento(String username, Evento evento) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent()) {
			return null;
		}
		Usuario usuario = found.get();
		if (usuario.getUnidadId() == null || !evento.getConjuntoId().equals(usuario.getConjuntoId())) {
			return null;
		}
		return usuario;
	}

	/**
	 * Copia los datos del DTO en la entidad y revisa el conjunto, el espacio y la
	 * fecha.
	 *
	 * @param entity Evento a modificar
	 * @param data   Datos nuevos
	 * @return 0 si los datos son válidos, 1 si el conjunto no existe, 2 si el
	 *         espacio no existe o es de otro conjunto, 3 si la fecha y hora ya
	 *         pasaron
	 */
	private int aplicar(Evento entity, EventoDTO data) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(data.getConjuntoId());
		if (!conjunto.isPresent()) {
			return 1;
		}
		Espacio espacio = null;
		if (data.getEspacioId() != null) {
			Optional<Espacio> found = espacioRepo.findById(data.getEspacioId());
			if (!found.isPresent() || !found.get().getConjuntoId().equals(conjunto.get().getId())) {
				return 2;
			}
			espacio = found.get();
		}
		LocalDate fecha = FechaUtil.leerFecha(data.getFecha());
		if (!LocalDateTime.of(fecha, FechaUtil.leerHora(data.getHoraInicio())).isAfter(LocalDateTime.now())) {
			return 3;
		}
		entity.setConjuntoId(conjunto.get().getId());
		entity.setEspacioId(espacio == null ? null : espacio.getId());
		entity.setTitulo(TextoUtil.limpiar(data.getTitulo()));
		entity.setDescripcion(TextoUtil.limpiar(data.getDescripcion()));
		entity.setTipo(data.getTipo());
		entity.setFecha(fecha);
		entity.setHoraInicio(FechaUtil.leerHora(data.getHoraInicio()));
		entity.setHoraFin(FechaUtil.leerHora(data.getHoraFin()));
		entity.setParaNinos(data.isParaNinos());
		entity.setCupoMaximo(data.getCupoMaximo());
		entity.setPlantilla(data.getPlantilla());
		return 0;
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
		return found.isPresent() ? found.get().getNombre() : null;
	}

	/**
	 * Busca el nombre de un espacio por su identificador.
	 *
	 * @param id Identificador a buscar (puede ser null)
	 * @return El nombre, o null si no existe
	 */
	private String nombreEspacio(Long id) {
		if (id == null) {
			return null;
		}
		Optional<Espacio> found = espacioRepo.findById(id);
		return found.isPresent() ? found.get().getNombre() : null;
	}
}
