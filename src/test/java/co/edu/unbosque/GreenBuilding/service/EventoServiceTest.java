package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.EventoDTO;
import co.edu.unbosque.GreenBuilding.model.Asistencia;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio EventoService.
 *
 * <p>Verifica la creación de eventos, los cruces con reservas, la cancelación y la asistencia de
 * los residentes.
 *
 * @author Juan Arango
 * @version 1.0
 */
class EventoServiceTest {

	/** Correo del residente de prueba. */
	private static final String CORREO = "ana@correo.com";

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Repositorio simulado de asistencias. */
	@Mock private AsistenciaRepository asistenciaRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio de correo simulado. */
	@Mock private CorreoService correoServ;

	/** Servicio de plantillas simulado. */
	@Mock private PlantillaCorreoService plantillaServ;

	/** Servicio de invitaciones simulado. */
	@Mock private InvitacionService invitacionServ;

	/** Servicio que se prueba. */
	@InjectMocks private EventoService eventoServ;

	/** Día del evento, dentro de tres días. */
	private LocalDate dia;

	/** Residente con unidad en el conjunto 1. */
	private Usuario residente;

	/**
	 * Inicializa los objetos simulados, el conjunto, el salón y el residente antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(eventoServ, "modelMapper", new ModelMapper());
		dia = LocalDate.now().plusDays(3);
		Conjunto conjunto = new Conjunto();
		conjunto.setId(1L);
		conjunto.setNombre("Torres");
		Espacio salon = new Espacio();
		salon.setId(3L);
		salon.setConjuntoId(1L);
		residente = new Usuario(CORREO, "clave", Role.USER);
		residente.setId(10L);
		residente.setConjuntoId(1L);
		residente.setUnidadId(5L);
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));
		when(espacioRepo.findById(3L)).thenReturn(Optional.of(salon));
		when(reservaRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(new ArrayList<>());
		when(eventoRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(new ArrayList<>());
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(residente));
		when(invitacionServ.nuevoCodigo()).thenReturn("codigoDePrueba");
	}

	/**
	 * Crea un DTO de evento en el salón del conjunto 1.
	 *
	 * @param fecha Fecha del evento
	 * @return DTO del evento
	 */
	private EventoDTO nuevoEvento(LocalDate fecha) {
		EventoDTO dto = new EventoDTO();
		dto.setConjuntoId(1L);
		dto.setEspacioId(3L);
		dto.setTitulo(" Bingo ");
		dto.setDescripcion("Bingo familiar");
		dto.setTipo(Evento.Tipo.FIESTA);
		dto.setFecha(fecha.toString());
		dto.setHoraInicio("15:00");
		dto.setHoraFin("17:00");
		dto.setPlantilla(Evento.Plantilla.FIESTA);
		return dto;
	}

	/**
	 * Crea un evento programado del conjunto 1.
	 *
	 * @param cupo Cupo máximo, o null si no tiene
	 * @return Evento
	 */
	private Evento eventoGuardado(Integer cupo) {
		Evento evento = new Evento();
		evento.setId(20L);
		evento.setConjuntoId(1L);
		evento.setTitulo("Bingo");
		evento.setFecha(dia);
		evento.setHoraInicio(LocalTime.of(15, 0));
		evento.setHoraFin(LocalTime.of(17, 0));
		evento.setCupoMaximo(cupo);
		when(eventoRepo.findById(20L)).thenReturn(Optional.of(evento));
		return evento;
	}

	/**
	 * Prueba que un evento válido se guarde con su código de invitación.
	 */
	@Test
	void testCreateEventoValido() {
		assertEquals(0, eventoServ.create(nuevoEvento(dia)), "Debe devolver 0");
		verify(eventoRepo).save(any(Evento.class));
	}

	/**
	 * Prueba los códigos de conjunto inexistente y fecha pasada.
	 */
	@Test
	void testCreateCodigosDeError() {
		EventoDTO sinConjunto = nuevoEvento(dia);
		sinConjunto.setConjuntoId(99L);
		when(conjuntoRepo.findById(99L)).thenReturn(Optional.empty());
		assertEquals(1, eventoServ.create(sinConjunto), "Sin conjunto debe devolver 1");
		assertEquals(3, eventoServ.create(nuevoEvento(LocalDate.now().minusDays(1))), "En el pasado debe devolver 3");
		verify(eventoRepo, never()).save(any());
	}

	/**
	 * Prueba que no se pueda crear un evento en un espacio con una reserva a la misma hora.
	 */
	@Test
	void testCreateEspacioOcupadoPorReserva() {
		Reserva reserva = new Reserva();
		reserva.setFecha(dia);
		reserva.setHoraInicio(LocalTime.of(16, 0));
		reserva.setHoraFin(LocalTime.of(18, 0));
		reserva.setEstado(Reserva.Estado.APROBADA);
		List<Reserva> delDia = new ArrayList<>();
		delDia.add(reserva);
		when(reservaRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(delDia);

		assertEquals(7, eventoServ.create(nuevoEvento(dia)), "Debe devolver 7");
	}

	/**
	 * Prueba que cancelar un evento cambie su estado y avise a los invitados.
	 */
	@Test
	void testCancelarEvento() {
		Evento evento = eventoGuardado(null);
		when(asistenciaRepo.findByEventoId(20L)).thenReturn(new ArrayList<>());

		assertEquals(0, eventoServ.cancelar(20L, " Lluvia "), "Debe devolver 0");
		assertEquals(Evento.Estado.CANCELADO, evento.getEstado(), "Debe quedar cancelado");
		assertEquals("Lluvia", evento.getMotivoCancelacion(), "Debe guardar el motivo limpio");
		verify(invitacionServ).avisarCancelacionEvento(evento);
		assertEquals(2, eventoServ.cancelar(20L, "Otra vez"), "Cancelarlo de nuevo debe devolver 2");
	}

	/**
	 * Prueba que un residente del conjunto pueda confirmar asistencia.
	 */
	@Test
	void testAsistirResidente() {
		eventoGuardado(null);
		when(asistenciaRepo.findByEventoIdAndUsuarioId(20L, 10L)).thenReturn(Optional.empty());

		assertEquals(0, eventoServ.asistir(CORREO, 20L), "Debe devolver 0");
		verify(asistenciaRepo).save(any(Asistencia.class));
	}

	/**
	 * Prueba que un residente de otro conjunto no pueda confirmar asistencia.
	 */
	@Test
	void testAsistirResidenteDeOtroConjunto() {
		eventoGuardado(null);
		residente.setConjuntoId(2L);
		assertEquals(3, eventoServ.asistir(CORREO, 20L), "Debe devolver 3");
	}

	/**
	 * Prueba que no se pueda confirmar asistencia a un evento lleno.
	 */
	@Test
	void testAsistirEventoLleno() {
		Evento evento = eventoGuardado(2);
		when(asistenciaRepo.findByEventoIdAndUsuarioId(20L, 10L)).thenReturn(Optional.empty());
		when(asistenciaRepo.countByEventoId(20L)).thenReturn(2L);

		assertTrue(eventoServ.estaLleno(evento), "El evento debe estar lleno");
		assertEquals(4, eventoServ.asistir(CORREO, 20L), "Debe devolver 4");
	}

	/**
	 * Prueba que no se pueda confirmar asistencia dos veces.
	 */
	@Test
	void testAsistirDosVeces() {
		eventoGuardado(null);
		when(asistenciaRepo.findByEventoIdAndUsuarioId(20L, 10L))
				.thenReturn(Optional.of(new Asistencia(20L, 10L)));

		assertEquals(5, eventoServ.asistir(CORREO, 20L), "Debe devolver 5");
	}

	/**
	 * Prueba que bajar el cupo por debajo de los asistentes confirmados devuelva 6.
	 */
	@Test
	void testUpdateCupoMenorQueAsistentes() {
		eventoGuardado(10);
		when(asistenciaRepo.countByEventoId(20L)).thenReturn(5L);
		EventoDTO cambio = nuevoEvento(dia);
		cambio.setCupoMaximo(3);

		assertEquals(6, eventoServ.updateById(20L, cambio), "Debe devolver 6");
		assertFalse(eventoServ.estaLleno(eventoGuardado(10)), "Con cupo 10 y 5 asistentes no está lleno");
	}
}
