package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.RecomendacionConjuntoDTO;
import co.edu.unbosque.GreenBuilding.dto.RecomendacionEspacioDTO;
import co.edu.unbosque.GreenBuilding.dto.RecomendacionEventoDTO;
import co.edu.unbosque.GreenBuilding.model.Asistencia;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.DistanciaUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que calcula recomendaciones: eventos y espacios para el residente
 * según sus intereses e historial, y conjuntos cercanos a la ubicación de un
 * visitante.
 */
@Service
public class RecomendacionService {

	/** Máximo de recomendaciones de eventos y espacios. */
	private static final int MAXIMO_RESULTADOS = 10;

	/** Máximo de conjuntos cercanos. */
	private static final int MAXIMO_CONJUNTOS = 6;

	/** Puntaje base de toda recomendación. */
	private static final int PUNTAJE_BASE = 10;

	/** Puntaje por coincidir con un interés. */
	private static final int PUNTAJE_INTERES = 50;

	/** Puntaje por el historial del residente. */
	private static final int PUNTAJE_HISTORIAL = 25;

	/** Puntaje por ser un plan para niños y tener ese interés. */
	private static final int PUNTAJE_NINOS = 15;

	/** Puntaje por ser pronto o de uso libre. */
	private static final int PUNTAJE_EXTRA = 10;

	/** Días que se consideran "esta semana". */
	private static final int DIAS_PRONTO = 7;

	/**
	 * Interés relacionado con cada tipo de evento, en el mismo orden del enum
	 * Evento.Tipo: FIESTA, INFANTIL, DEPORTIVO, CULTURAL, ASAMBLEA y OTRO (que no
	 * tiene interés relacionado).
	 */
	private static final String[] INTERES_POR_EVENTO = { "FIESTAS", "NINOS", "DEPORTE", "CULTURA", "ASAMBLEAS", null };

	/**
	 * Interés relacionado con cada tipo de espacio, en el mismo orden del enum
	 * Espacio.Tipo: SALON_SOCIAL, PARQUE, PISCINA, GIMNASIO, BBQ, CANCHA,
	 * ZONA_INFANTIL, ZONA_HUMEDA y OTRO (que no tiene interés relacionado).
	 */
	private static final String[] INTERES_POR_ESPACIO = { "FIESTAS", "NATURALEZA", "PISCINA", "GIMNASIO", "BBQ",
			"DEPORTE", "NINOS", "PISCINA", null };

	/** Repositorio para acceder a los usuarios. */
	@Autowired
	private UsuarioRepository usuarioRepo;

	/** Repositorio para acceder a los eventos. */
	@Autowired
	private EventoRepository eventoRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired
	private EspacioRepository espacioRepo;

	/** Repositorio para acceder a las reservas. */
	@Autowired
	private ReservaRepository reservaRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a las asistencias a eventos. */
	@Autowired
	private AsistenciaRepository asistenciaRepo;

	/** Servicio de eventos, para convertir eventos en DTO. */
	@Autowired
	private EventoService eventoServ;

	/** Servicio de conjuntos, para convertir conjuntos en DTO. */
	@Autowired
	private ConjuntoService conjuntoServ;

	/** Servicio de espacios, para convertir espacios en DTO. */
	@Autowired
	private EspacioService espacioServ;

	/** Constructor por defecto. */
	public RecomendacionService() {
	}

	/**
	 * Recomienda eventos próximos del conjunto del residente a los que aún no se
	 * inscribió.
	 *
	 * @param username Correo del residente
	 * @return Lista de recomendaciones ordenadas por puntaje
	 */
	public List<RecomendacionEventoDTO> getEventos(String username) {
		List<RecomendacionEventoDTO> recomendaciones = new ArrayList<>();
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent() || found.get().getUnidadId() == null) {
			return recomendaciones;
		}
		Usuario usuario = found.get();
		List<Evento.Tipo> tiposAsistidos = new ArrayList<>();
		for (Asistencia asistencia : asistenciaRepo.findByUsuarioId(usuario.getId())) {
			Optional<Evento> asistido = eventoRepo.findById(asistencia.getEventoId());
			if (asistido.isPresent()) {
				tiposAsistidos.add(asistido.get().getTipo());
			}
		}
		for (Evento evento : eventoServ.proximosDelConjunto(usuario.getConjuntoId())) {
			boolean inscrito = asistenciaRepo.findByEventoIdAndUsuarioId(evento.getId(), usuario.getId()).isPresent();
			if (!inscrito && !eventoServ.estaLleno(evento)) {
				insertarEvento(recomendaciones, puntuarEvento(evento, usuario, tiposAsistidos));
			}
		}
		return new ArrayList<>(recomendaciones.subList(0, Math.min(MAXIMO_RESULTADOS, recomendaciones.size())));
	}

	/**
	 * Recomienda espacios disponibles del conjunto del residente.
	 *
	 * @param username Correo del residente
	 * @return Lista de recomendaciones ordenadas por puntaje
	 */
	public List<RecomendacionEspacioDTO> getEspacios(String username) {
		List<RecomendacionEspacioDTO> recomendaciones = new ArrayList<>();
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent() || found.get().getUnidadId() == null) {
			return recomendaciones;
		}
		Usuario usuario = found.get();
		List<Long> reservados = new ArrayList<>();
		for (Reserva reserva : reservaRepo.findByUsuarioId(usuario.getId())) {
			if (reserva.getEstado() != Reserva.Estado.RECHAZADA) {
				reservados.add(reserva.getEspacioId());
			}
		}
		for (Espacio espacio : espacioRepo.findByConjuntoIdOrderByNombreAsc(usuario.getConjuntoId())) {
			if (espacio.isDisponible()) {
				insertarEspacio(recomendaciones, puntuarEspacio(espacio, usuario, reservados));
			}
		}
		return new ArrayList<>(recomendaciones.subList(0, Math.min(MAXIMO_RESULTADOS, recomendaciones.size())));
	}

	/**
	 * Recomienda los conjuntos más cercanos a una ubicación.
	 *
	 * @param latitud  Latitud del visitante
	 * @param longitud Longitud del visitante
	 * @return Lista de conjuntos ordenados por distancia
	 */
	public List<RecomendacionConjuntoDTO> getConjuntosCercanos(double latitud, double longitud) {
		List<RecomendacionConjuntoDTO> recomendaciones = new ArrayList<>();
		for (Conjunto conjunto : conjuntoRepo.findAll()) {
			double distancia = DistanciaUtil.redondear(
					DistanciaUtil.kilometros(latitud, longitud, conjunto.getLatitud(), conjunto.getLongitud()));
			String tipo = conjunto.getTipo() == Conjunto.Tipo.CASAS ? "Conjunto de casas" : "Conjunto de apartamentos";
			String motivo = tipo + " a " + String.format(Locale.forLanguageTag("es-CO"), "%.1f", distancia)
					+ " km de ti, en " + conjunto.getMunicipio();
			insertarConjunto(recomendaciones,
					new RecomendacionConjuntoDTO(conjuntoServ.aDTO(conjunto), distancia, motivo));
		}
		return new ArrayList<>(recomendaciones.subList(0, Math.min(MAXIMO_CONJUNTOS, recomendaciones.size())));
	}

	/**
	 * Une los motivos de una recomendación separados por un punto medio.
	 *
	 * @param motivos Motivos de la recomendación
	 * @return Los motivos en un solo texto
	 */
	private String unirMotivos(List<String> motivos) {
		String texto = "";
		for (int i = 0; i < motivos.size(); i++) {
			texto = i == 0 ? motivos.get(i) : texto + " · " + motivos.get(i);
		}
		return texto;
	}

	/**
	 * Agrega una recomendación de evento en su lugar: primero el mayor puntaje y,
	 * con el mismo puntaje, la fecha más próxima.
	 *
	 * @param lista Lista ya ordenada
	 * @param nueva Recomendación a agregar
	 */
	private void insertarEvento(List<RecomendacionEventoDTO> lista, RecomendacionEventoDTO nueva) {
		int posicion = lista.size();
		for (int i = 0; i < lista.size() && posicion == lista.size(); i++) {
			RecomendacionEventoDTO actual = lista.get(i);
			boolean mayorPuntaje = nueva.getPuntaje() > actual.getPuntaje();
			boolean masPronto = nueva.getPuntaje() == actual.getPuntaje()
					&& nueva.getEvento().getFecha().compareTo(actual.getEvento().getFecha()) < 0;
			if (mayorPuntaje || masPronto) {
				posicion = i;
			}
		}
		lista.add(posicion, nueva);
	}

	/**
	 * Agrega una recomendación de espacio en su lugar: primero el mayor puntaje.
	 *
	 * @param lista Lista ya ordenada
	 * @param nueva Recomendación a agregar
	 */
	private void insertarEspacio(List<RecomendacionEspacioDTO> lista, RecomendacionEspacioDTO nueva) {
		int posicion = lista.size();
		for (int i = 0; i < lista.size() && posicion == lista.size(); i++) {
			RecomendacionEspacioDTO actual = lista.get(i);
			if (nueva.getPuntaje() > actual.getPuntaje()) {
				posicion = i;
			}
		}
		lista.add(posicion, nueva);
	}

	/**
	 * Agrega un conjunto cercano en su lugar: primero el más cercano.
	 *
	 * @param lista Lista ya ordenada
	 * @param nueva Recomendación a agregar
	 */
	private void insertarConjunto(List<RecomendacionConjuntoDTO> lista, RecomendacionConjuntoDTO nueva) {
		int posicion = lista.size();
		for (int i = 0; i < lista.size() && posicion == lista.size(); i++) {
			RecomendacionConjuntoDTO actual = lista.get(i);
			if (nueva.getDistanciaKm() < actual.getDistanciaKm()) {
				posicion = i;
			}
		}
		lista.add(posicion, nueva);
	}

	/**
	 * Calcula el puntaje y el motivo de un evento para un residente.
	 *
	 * @param evento         Evento a puntuar
	 * @param usuario        Residente
	 * @param tiposAsistidos Tipos de eventos a los que ya asistió
	 * @return Recomendación del evento
	 */
	private RecomendacionEventoDTO puntuarEvento(Evento evento, Usuario usuario, List<Evento.Tipo> tiposAsistidos) {
		int puntaje = PUNTAJE_BASE;
		List<String> motivos = new ArrayList<>();
		String interes = evento.getTipo() == null ? null : INTERES_POR_EVENTO[evento.getTipo().ordinal()];
		if (interes != null && usuario.interesesComoLista().contains(interes)) {
			puntaje += PUNTAJE_INTERES;
			motivos.add("Coincide con tus intereses");
		}
		if (evento.isParaNinos() && usuario.interesesComoLista().contains("NINOS")) {
			puntaje += PUNTAJE_NINOS;
			motivos.add("Es un plan para niños");
		}
		if (tiposAsistidos.contains(evento.getTipo())) {
			puntaje += PUNTAJE_HISTORIAL;
			motivos.add("Has ido a eventos parecidos");
		}
		if (!evento.getFecha().isAfter(LocalDate.now().plusDays(DIAS_PRONTO))) {
			puntaje += PUNTAJE_EXTRA;
			motivos.add("Es esta semana");
		}
		String motivo = motivos.isEmpty() ? "Próximo evento en tu conjunto" : unirMotivos(motivos);
		return new RecomendacionEventoDTO(eventoServ.aDTO(evento, usuario), Math.min(puntaje, 100), motivo);
	}

	/**
	 * Calcula el puntaje y el motivo de un espacio para un residente.
	 *
	 * @param espacio    Espacio a puntuar
	 * @param usuario    Residente
	 * @param reservados IDs de los espacios que ya reservó
	 * @return Recomendación del espacio
	 */
	private RecomendacionEspacioDTO puntuarEspacio(Espacio espacio, Usuario usuario, List<Long> reservados) {
		int puntaje = PUNTAJE_BASE;
		List<String> motivos = new ArrayList<>();
		String interes = espacio.getTipo() == null ? null : INTERES_POR_ESPACIO[espacio.getTipo().ordinal()];
		if (interes != null && usuario.interesesComoLista().contains(interes)) {
			puntaje += PUNTAJE_INTERES;
			motivos.add("Coincide con tus intereses");
		}
		if (reservados.contains(espacio.getId())) {
			puntaje += PUNTAJE_HISTORIAL;
			motivos.add("Lo has reservado antes");
		}
		if (!espacio.isRequiereReserva()) {
			puntaje += PUNTAJE_EXTRA;
			motivos.add("Es de uso libre");
		}
		String motivo = motivos.isEmpty() ? "Espacio disponible en tu conjunto" : unirMotivos(motivos);
		return new RecomendacionEspacioDTO(espacioServ.aDTO(espacio), Math.min(puntaje, 100), motivo);
	}
}
