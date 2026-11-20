package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.ResumenDTO;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Clase de prueba para el servicio ResumenService.
 *
 * <p>Verifica que el resumen del administrador junte los conteos de cada repositorio.
 *
 * @author Juan Arango
 * @version 1.0
 */
class ResumenServiceTest {

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de unidades. */
	@Mock private UnidadRepository unidadRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Servicio que se prueba, con los repositorios simulados. */
	@InjectMocks private ResumenService resumenServ;

	/**
	 * Inicializa los objetos simulados antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * Prueba que cada número del resumen venga del repositorio que le corresponde.
	 */
	@Test
	void testGetAdminJuntaLosConteos() {
		when(conjuntoRepo.count()).thenReturn(4L);
		when(unidadRepo.count()).thenReturn(20L);
		when(usuarioRepo.countByUnidadIdIsNotNull()).thenReturn(15L);
		when(espacioRepo.count()).thenReturn(8L);
		when(eventoRepo.countByEstadoAndFechaGreaterThanEqual(eq(Evento.Estado.PROGRAMADO), any()))
				.thenReturn(3L);
		when(reservaRepo.countByEstado(Reserva.Estado.PENDIENTE)).thenReturn(2L);
		when(usuarioRepo.countByRoleAndUnidadIdIsNullAndActivoTrue(Role.USER)).thenReturn(5L);

		ResumenDTO resumen = resumenServ.getAdmin();

		assertEquals(4L, resumen.getTotalConjuntos(), "Debe haber 4 conjuntos");
		assertEquals(20L, resumen.getTotalUnidades(), "Debe haber 20 unidades");
		assertEquals(15L, resumen.getTotalResidentes(), "Debe haber 15 residentes");
		assertEquals(8L, resumen.getTotalEspacios(), "Debe haber 8 espacios");
		assertEquals(3L, resumen.getEventosProximos(), "Debe haber 3 eventos próximos");
		assertEquals(2L, resumen.getReservasPendientes(), "Debe haber 2 reservas pendientes");
		assertEquals(5L, resumen.getUsuariosSinUnidad(), "Debe haber 5 usuarios sin unidad");
	}
}
