package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Clase de prueba para el servicio PlantillaCorreoService.
 *
 * <p>Verifica que los correos en HTML incluyan los datos correctos y escapen el texto del usuario.
 *
 * @author Juan Arango
 * @version 1.0
 */
class PlantillaCorreoServiceTest {

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Servicio que se prueba. */
	@InjectMocks private PlantillaCorreoService plantillaServ;

	/**
	 * Inicializa los objetos simulados antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(espacioRepo.findById(any())).thenReturn(Optional.empty());
	}

	/**
	 * Prueba que el correo de verificación muestre el código y escape el nombre.
	 */
	@Test
	void testCodigoDeVerificacion() {
		String html = plantillaServ.codigo("Ana <b>", "123456", Proposito.VERIFICACION);
		assertTrue(html.contains("123456"), "Debe mostrar el código");
		assertTrue(html.contains("verificar tu cuenta"), "Debe ser el texto de verificación");
		assertTrue(html.contains("Ana &lt;b&gt;"), "Debe escapar el nombre");
		assertFalse(html.contains("Ana <b>"), "No debe dejar etiquetas del usuario");
	}

	/**
	 * Prueba que el correo para restablecer la contraseña use su propio texto.
	 */
	@Test
	void testCodigoParaRestablecer() {
		String html = plantillaServ.codigo("Ana", "654321", Proposito.RESTABLECER);
		assertTrue(html.contains("restablecer tu contraseña"), "Debe ser el texto de restablecer");
	}

	/**
	 * Prueba que la invitación a un evento muestre el título, el conjunto y el estilo de su plantilla.
	 */
	@Test
	void testInvitacionDeEvento() {
		Conjunto conjunto = new Conjunto();
		conjunto.setNombre("Torres del Parque");
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));

		Evento evento = new Evento();
		evento.setConjuntoId(1L);
		evento.setTitulo("Fiesta de fin de año");
		evento.setFecha(LocalDate.of(2026, 12, 20));
		evento.setHoraInicio(LocalTime.of(18, 0));
		evento.setHoraFin(LocalTime.of(22, 0));
		evento.setPlantilla(Evento.Plantilla.FIESTA);

		String html = plantillaServ.invitacion(evento, "http://localhost:4200/invitacion/abc");
		assertTrue(html.contains("Fiesta de fin de año"), "Debe mostrar el título");
		assertTrue(html.contains("Torres del Parque"), "Debe mostrar el conjunto");
		assertTrue(html.contains("¡Vamos de fiesta!"), "Debe usar el estilo de fiesta");
		assertTrue(html.contains("18:00 a 22:00"), "Debe mostrar la hora");
	}

	/**
	 * Prueba que la respuesta a una reserva rechazada muestre la observación.
	 */
	@Test
	void testEstadoReservaRechazada() {
		Reserva reserva = new Reserva();
		reserva.setEspacioId(3L);
		reserva.setFecha(LocalDate.of(2026, 12, 5));
		reserva.setHoraInicio(LocalTime.of(10, 0));
		reserva.setHoraFin(LocalTime.of(12, 0));
		reserva.setEstado(Reserva.Estado.RECHAZADA);
		reserva.setObservacion("El salón está en mantenimiento");

		String html = plantillaServ.estadoReserva(reserva);
		assertTrue(html.contains("Tu reserva fue rechazada"), "Debe decir que fue rechazada");
		assertTrue(html.contains("El salón está en mantenimiento"), "Debe mostrar la observación");
	}
}
