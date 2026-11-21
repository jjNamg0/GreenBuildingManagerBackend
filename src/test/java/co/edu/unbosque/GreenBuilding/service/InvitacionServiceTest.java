package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.InvitacionDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Invitado;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.InvitadoRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio InvitacionService.
 *
 * <p>Verifica los códigos y enlaces de invitación, la consulta pública por código y el envío de
 * invitaciones por correo.
 *
 * @author Juan Arango
 * @version 1.0
 */
class InvitacionServiceTest {

	/** Repositorio simulado de invitados. */
	@Mock private InvitadoRepository invitadoRepo;

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Servicio de correo simulado. */
	@Mock private CorreoService correoServ;

	/** Servicio de plantillas simulado. */
	@Mock private PlantillaCorreoService plantillaServ;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio que se prueba. */
	@InjectMocks private InvitacionService invitacionServ;

	/**
	 * Inicializa los objetos simulados y la dirección del front antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(invitacionServ, "urlFront", "https://greenbuildingmanager.netlify.app/");
	}

	/**
	 * Crea un evento programado del conjunto 1 dentro de cinco días.
	 *
	 * @return Evento
	 */
	private Evento evento() {
		Evento evento = new Evento();
		evento.setId(20L);
		evento.setConjuntoId(1L);
		evento.setTitulo("Bingo");
		evento.setFecha(LocalDate.now().plusDays(5));
		evento.setHoraInicio(LocalTime.of(15, 0));
		evento.setHoraFin(LocalTime.of(17, 0));
		evento.setPlantilla(Evento.Plantilla.FIESTA);
		return evento;
	}

	/**
	 * Prueba que el código tenga 24 caracteres y que dos códigos sean distintos.
	 */
	@Test
	void testNuevoCodigo() {
		String codigo = invitacionServ.nuevoCodigo();
		assertEquals(24, codigo.length(), "El código debe tener 24 caracteres");
		assertTrue(codigo.matches("[A-Za-z2-9]+"), "Solo debe tener letras y números sin confusiones");
		assertNotEquals(codigo, invitacionServ.nuevoCodigo(), "Dos códigos no deben ser iguales");
	}

	/**
	 * Prueba que el enlace use la dirección del front sin la barra final y cree el código si falta.
	 */
	@Test
	void testGetEnlaceEventoCreaElCodigo() {
		Evento evento = evento();
		when(eventoRepo.findById(20L)).thenReturn(Optional.of(evento));

		String enlace = invitacionServ.getEnlaceEvento(20L);

		assertNotNull(evento.getCodigoInvitacion(), "El evento debe quedar con código");
		assertEquals(
				"https://greenbuildingmanager.netlify.app/invitacion/" + evento.getCodigoInvitacion(),
				enlace,
				"El enlace debe apuntar al front");
		verify(eventoRepo).save(evento);
		assertNull(invitacionServ.getEnlaceEvento(99L), "Un evento inexistente no tiene enlace");
	}

	/**
	 * Prueba que el código de un evento devuelva su invitación con el conjunto y la plantilla.
	 */
	@Test
	void testGetByCodigoDeEvento() {
		Conjunto conjunto = new Conjunto();
		conjunto.setId(1L);
		conjunto.setNombre("Torres");
		when(eventoRepo.findByCodigoInvitacion("abc")).thenReturn(Optional.of(evento()));
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));

		InvitacionDTO dto = invitacionServ.getByCodigo("abc");

		assertEquals("EVENTO", dto.getTipo(), "Debe ser de un evento");
		assertEquals("Bingo", dto.getTitulo(), "Debe tener el título");
		assertEquals("Torres", dto.getAnfitrion(), "El anfitrión debe ser el conjunto");
		assertEquals(Evento.Plantilla.FIESTA, dto.getPlantilla(), "Debe tener la plantilla del evento");
		assertEquals("VIGENTE", dto.getEstado(), "Debe estar vigente");
	}

	/**
	 * Prueba que una reserva pendiente o un código inexistente no muestren invitación.
	 */
	@Test
	void testGetByCodigoSinInvitacion() {
		Reserva pendiente = new Reserva();
		when(eventoRepo.findByCodigoInvitacion(anyString())).thenReturn(Optional.empty());
		when(reservaRepo.findByCodigoInvitacion("pendiente")).thenReturn(Optional.of(pendiente));
		when(reservaRepo.findByCodigoInvitacion("nada")).thenReturn(Optional.empty());

		assertNull(invitacionServ.getByCodigo("pendiente"), "Una reserva pendiente no se muestra");
		assertNull(invitacionServ.getByCodigo("nada"), "Un código inexistente no se muestra");
	}

	/**
	 * Prueba los resultados de revisar si un residente puede invitar a su reserva.
	 */
	@Test
	void testRevisarReserva() {
		Usuario dueno = new Usuario("ana@correo.com", "hash", Role.USER);
		Reserva reserva = new Reserva();
		reserva.setUsuarioId(10L);
		reserva.setFecha(LocalDate.now().plusDays(2));
		reserva.setHoraInicio(LocalTime.of(10, 0));
		reserva.setHoraFin(LocalTime.of(12, 0));
		when(reservaRepo.findById(30L)).thenReturn(Optional.of(reserva));
		when(usuarioRepo.findById(10L)).thenReturn(Optional.of(dueno));

		assertEquals(1, invitacionServ.revisarReserva("ana@correo.com", 99L), "Sin reserva debe devolver 1");
		assertEquals(2, invitacionServ.revisarReserva("otro@correo.com", 30L), "Otro usuario debe devolver 2");
		assertEquals(3, invitacionServ.revisarReserva("ana@correo.com", 30L), "Pendiente debe devolver 3");
		reserva.setEstado(Reserva.Estado.APROBADA);
		assertEquals(0, invitacionServ.revisarReserva("ana@correo.com", 30L), "Aprobada debe devolver 0");
	}

	/**
	 * Prueba que la invitación de un evento se envíe una vez por correo, sin repetir.
	 */
	@Test
	void testEnviarEventoSinRepetirCorreos() {
		Evento evento = evento();
		evento.setCodigoInvitacion("abc");
		when(eventoRepo.findById(20L)).thenReturn(Optional.of(evento));
		when(correoServ.enviarHtml(anyString(), anyString(), any())).thenReturn(true);
		List<String> correos = new ArrayList<>();
		correos.add("luis@correo.com");
		correos.add(" LUIS@correo.com ");
		correos.add("maria@correo.com");

		assertEquals(2, invitacionServ.enviarEvento(20L, correos), "Deben enviarse 2 correos");
		verify(invitadoRepo, times(2)).save(any(Invitado.class));
	}

	/**
	 * Prueba los códigos de enviar la invitación de un evento inexistente o cancelado.
	 */
	@Test
	void testEnviarEventoCodigosDeError() {
		Evento cancelado = evento();
		cancelado.setEstado(Evento.Estado.CANCELADO);
		when(eventoRepo.findById(21L)).thenReturn(Optional.of(cancelado));

		assertEquals(-1, invitacionServ.enviarEvento(99L, new ArrayList<>()), "Sin evento debe devolver -1");
		assertEquals(-2, invitacionServ.enviarEvento(21L, new ArrayList<>()), "Cancelado debe devolver -2");
	}
}
