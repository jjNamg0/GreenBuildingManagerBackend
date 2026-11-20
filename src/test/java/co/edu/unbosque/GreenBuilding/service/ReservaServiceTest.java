package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.FranjaHorariaDTO;
import co.edu.unbosque.GreenBuilding.dto.ReservaDTO;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
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
 * Clase de prueba para el servicio ReservaService.
 *
 * <p>Verifica las reglas de las reservas: quién puede reservar, horario, anticipación, cupo,
 * cruces con otras reservas, aprobación, cancelación y franjas disponibles.
 *
 * @author Juan Arango
 * @version 1.0
 */
class ReservaServiceTest {

	/** Correo de la residente de prueba. */
	private static final String CORREO = "ana@correo.com";

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de unidades. */
	@Mock private UnidadRepository unidadRepo;

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio de correo simulado. */
	@Mock private CorreoService correoServ;

	/** Servicio de plantillas simulado. */
	@Mock private PlantillaCorreoService plantillaServ;

	/** Servicio de invitaciones simulado. */
	@Mock private InvitacionService invitacionServ;

	/** Servicio que se prueba. */
	@InjectMocks private ReservaService reservaServ;

	/** Residente con unidad en el conjunto 1. */
	private Usuario residente;

	/** Salón del conjunto 1, de 08:00 a 22:00 y para 20 personas. */
	private Espacio salon;

	/** Día de la reserva, dentro de dos días. */
	private LocalDate dia;

	/**
	 * Inicializa los objetos simulados, la residente y el salón antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(reservaServ, "modelMapper", new ModelMapper());
		residente = new Usuario(CORREO, "clave", Role.USER);
		residente.setId(10L);
		residente.setConjuntoId(1L);
		residente.setUnidadId(5L);
		salon = new Espacio();
		salon.setId(3L);
		salon.setConjuntoId(1L);
		salon.setNombre("Salón social");
		salon.setHoraApertura(LocalTime.of(8, 0));
		salon.setHoraCierre(LocalTime.of(22, 0));
		salon.setCapacidad(20);
		salon.setRequiereReserva(true);
		salon.setDisponible(true);
		dia = LocalDate.now().plusDays(2);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(residente));
		when(usuarioRepo.findById(10L)).thenReturn(Optional.of(residente));
		when(espacioRepo.findById(3L)).thenReturn(Optional.of(salon));
		when(reservaRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(new ArrayList<>());
		when(eventoRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(new ArrayList<>());
	}

	/**
	 * Crea una solicitud de reserva para el salón.
	 *
	 * @param fecha Fecha de la reserva
	 * @param inicio Hora de inicio HH:mm
	 * @param fin Hora de fin HH:mm
	 * @param asistentes Número de asistentes
	 * @return DTO de la reserva
	 */
	private ReservaDTO solicitud(LocalDate fecha, String inicio, String fin, int asistentes) {
		ReservaDTO dto = new ReservaDTO();
		dto.setEspacioId(3L);
		dto.setFecha(fecha.toString());
		dto.setHoraInicio(inicio);
		dto.setHoraFin(fin);
		dto.setMotivo("Cumpleaños");
		dto.setAsistentes(asistentes);
		return dto;
	}

	/**
	 * Crea una reserva guardada del salón para el día de prueba.
	 *
	 * @param id Id de la reserva
	 * @param estado Estado de la reserva
	 * @return Reserva
	 */
	private Reserva reservaGuardada(Long id, Reserva.Estado estado) {
		Reserva reserva = new Reserva();
		reserva.setId(id);
		reserva.setEspacioId(3L);
		reserva.setUsuarioId(10L);
		reserva.setFecha(dia);
		reserva.setHoraInicio(LocalTime.of(10, 0));
		reserva.setHoraFin(LocalTime.of(12, 0));
		reserva.setEstado(estado);
		return reserva;
	}

	/**
	 * Prueba que una reserva válida se guarde como pendiente.
	 */
	@Test
	void testCreateReservaValida() {
		assertEquals(0, reservaServ.create(CORREO, solicitud(dia, "10:00", "12:00", 15)), "Debe devolver 0");
		verify(reservaRepo).save(any(Reserva.class));
	}

	/**
	 * Prueba que un usuario sin unidad no pueda reservar.
	 */
	@Test
	void testCreateUsuarioSinUnidad() {
		residente.setUnidadId(null);
		assertEquals(1, reservaServ.create(CORREO, solicitud(dia, "10:00", "12:00", 5)), "Debe devolver 1");
		verify(reservaRepo, never()).save(any());
	}

	/**
	 * Prueba que no se pueda reservar un espacio de otro conjunto.
	 */
	@Test
	void testCreateEspacioDeOtroConjunto() {
		salon.setConjuntoId(2L);
		assertEquals(3, reservaServ.create(CORREO, solicitud(dia, "10:00", "12:00", 5)), "Debe devolver 3");
	}

	/**
	 * Prueba que un espacio de uso libre o no disponible no se pueda reservar.
	 */
	@Test
	void testCreateEspacioSinReservaONoDisponible() {
		salon.setRequiereReserva(false);
		assertEquals(4, reservaServ.create(CORREO, solicitud(dia, "10:00", "12:00", 5)), "Debe devolver 4");
		salon.setRequiereReserva(true);
		salon.setDisponible(false);
		assertEquals(5, reservaServ.create(CORREO, solicitud(dia, "10:00", "12:00", 5)), "Debe devolver 5");
	}

	/**
	 * Prueba los códigos de horario, fecha pasada, anticipación y cupo.
	 */
	@Test
	void testCreateValidacionesDeFechaYCupo() {
		assertEquals(6, reservaServ.create(CORREO, solicitud(dia, "06:00", "08:00", 5)), "Fuera de horario: 6");
		LocalDate ayer = LocalDate.now().minusDays(1);
		assertEquals(7, reservaServ.create(CORREO, solicitud(ayer, "10:00", "12:00", 5)), "En el pasado: 7");
		LocalDate lejos = LocalDate.now().plusDays(ReservaService.DIAS_MAXIMOS_ANTICIPACION + 5);
		assertEquals(8, reservaServ.create(CORREO, solicitud(lejos, "10:00", "12:00", 5)), "Muy lejos: 8");
		assertEquals(9, reservaServ.create(CORREO, solicitud(dia, "10:00", "12:00", 50)), "Sin cupo: 9");
	}

	/**
	 * Prueba que no se pueda reservar una franja que se cruza con otra reserva activa.
	 */
	@Test
	void testCreateSeCruzaConOtraReserva() {
		List<Reserva> delDia = new ArrayList<>();
		delDia.add(reservaGuardada(20L, Reserva.Estado.APROBADA));
		when(reservaRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(delDia);

		assertEquals(10, reservaServ.create(CORREO, solicitud(dia, "11:00", "13:00", 5)), "Debe devolver 10");
	}

	/**
	 * Prueba que el administrador apruebe una reserva pendiente y se avise por correo.
	 */
	@Test
	void testAprobarReservaPendiente() {
		Reserva reserva = reservaGuardada(20L, Reserva.Estado.PENDIENTE);
		when(reservaRepo.findById(20L)).thenReturn(Optional.of(reserva));

		assertEquals(0, reservaServ.aprobar(20L), "Debe devolver 0");
		assertEquals(Reserva.Estado.APROBADA, reserva.getEstado(), "Debe quedar aprobada");
		verify(correoServ).enviarHtml(any(), any(), any());
	}

	/**
	 * Prueba que no se pueda aprobar una reserva que ya no está pendiente.
	 */
	@Test
	void testAprobarReservaNoPendiente() {
		when(reservaRepo.findById(20L)).thenReturn(Optional.of(reservaGuardada(20L, Reserva.Estado.CANCELADA)));
		assertEquals(2, reservaServ.aprobar(20L), "Debe devolver 2");
	}

	/**
	 * Prueba que al rechazar se guarde la observación.
	 */
	@Test
	void testRechazarGuardaObservacion() {
		Reserva reserva = reservaGuardada(20L, Reserva.Estado.PENDIENTE);
		when(reservaRepo.findById(20L)).thenReturn(Optional.of(reserva));

		assertEquals(0, reservaServ.rechazar(20L, "  En mantenimiento "), "Debe devolver 0");
		assertEquals("En mantenimiento", reserva.getObservacion(), "Debe guardar la observación limpia");
	}

	/**
	 * Prueba que solo el dueño pueda cancelar su reserva y que se avise a los invitados si estaba
	 * aprobada.
	 */
	@Test
	void testCancelarReserva() {
		Reserva reserva = reservaGuardada(20L, Reserva.Estado.APROBADA);
		when(reservaRepo.findById(20L)).thenReturn(Optional.of(reserva));

		assertEquals(2, reservaServ.cancelar("otro@correo.com", 20L), "Otro usuario debe recibir 2");
		assertEquals(0, reservaServ.cancelar(CORREO, 20L), "La dueña debe poder cancelar");
		assertEquals(Reserva.Estado.CANCELADA, reserva.getEstado(), "Debe quedar cancelada");
		verify(invitacionServ).avisarCancelacionReserva(reserva);
	}

	/**
	 * Prueba que las franjas ocupadas por una reserva aparezcan como no disponibles.
	 */
	@Test
	void testGetDisponibilidadMarcaFranjasOcupadas() {
		List<Reserva> delDia = new ArrayList<>();
		delDia.add(reservaGuardada(20L, Reserva.Estado.APROBADA));
		when(reservaRepo.findByEspacioIdAndFecha(3L, dia)).thenReturn(delDia);

		List<FranjaHorariaDTO> franjas = reservaServ.getDisponibilidad(3L, dia.toString());

		assertEquals(14, franjas.size(), "De 08:00 a 22:00 deben salir 14 franjas de una hora");
		assertTrue(franjas.get(0).isDisponible(), "De 08:00 a 09:00 debe estar libre");
		assertFalse(franjas.get(2).isDisponible(), "De 10:00 a 11:00 debe estar ocupada");
	}

	/**
	 * Prueba que un espacio que no existe no tenga franjas.
	 */
	@Test
	void testGetDisponibilidadEspacioInexistente() {
		when(espacioRepo.findById(99L)).thenReturn(Optional.empty());
		assertTrue(reservaServ.getDisponibilidad(99L, dia.toString()).isEmpty(), "Debe ser una lista vacía");
	}
}
