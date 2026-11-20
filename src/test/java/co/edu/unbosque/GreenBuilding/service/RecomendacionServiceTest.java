package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.RecomendacionConjuntoDTO;
import co.edu.unbosque.GreenBuilding.dto.RecomendacionEspacioDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Clase de prueba para el servicio RecomendacionService.
 *
 * <p>Verifica que las recomendaciones se ordenen por puntaje según los intereses y por distancia
 * en el caso de los conjuntos cercanos.
 *
 * @author Juan Arango
 * @version 1.0
 */
class RecomendacionServiceTest {

	/** Correo de la residente de prueba. */
	private static final String CORREO = "ana@correo.com";

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de asistencias. */
	@Mock private AsistenciaRepository asistenciaRepo;

	/** Servicio de eventos simulado. */
	@Mock private EventoService eventoServ;

	/** Servicio de conjuntos simulado. */
	@Mock private ConjuntoService conjuntoServ;

	/** Servicio de espacios simulado. */
	@Mock private EspacioService espacioServ;

	/** Servicio que se prueba. */
	@InjectMocks private RecomendacionService recomendacionServ;

	/** Residente con unidad en el conjunto 1. */
	private Usuario residente;

	/**
	 * Inicializa los objetos simulados y la residente antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		residente = new Usuario(CORREO, "hash", Role.USER);
		residente.setId(10L);
		residente.setConjuntoId(1L);
		residente.setUnidadId(5L);
		when(usuarioRepo.findByUsername(CORREO)).thenReturn(Optional.of(residente));
		when(reservaRepo.findByUsuarioId(10L)).thenReturn(new ArrayList<>());
	}

	/**
	 * Crea un espacio disponible del conjunto 1 que requiere reserva.
	 *
	 * @param id Id del espacio
	 * @param tipo Tipo de espacio
	 * @return Espacio
	 */
	private Espacio espacio(Long id, Espacio.Tipo tipo) {
		Espacio espacio = new Espacio();
		espacio.setId(id);
		espacio.setConjuntoId(1L);
		espacio.setTipo(tipo);
		espacio.setDisponible(true);
		espacio.setRequiereReserva(true);
		return espacio;
	}

	/**
	 * Prueba que un usuario sin unidad no reciba recomendaciones.
	 */
	@Test
	void testSinUnidadNoHayRecomendaciones() {
		residente.setUnidadId(null);
		assertTrue(recomendacionServ.getEventos(CORREO).isEmpty(), "No debe recomendar eventos");
		assertTrue(recomendacionServ.getEspacios(CORREO).isEmpty(), "No debe recomendar espacios");
	}

	/**
	 * Prueba que el espacio que coincide con los intereses quede primero con más puntaje.
	 */
	@Test
	void testGetEspaciosOrdenaPorIntereses() {
		residente.setInteresesGuardados("PISCINA");
		List<Espacio> espacios = new ArrayList<>();
		espacios.add(espacio(1L, Espacio.Tipo.GIMNASIO));
		espacios.add(espacio(2L, Espacio.Tipo.PISCINA));
		when(espacioRepo.findByConjuntoIdOrderByNombreAsc(1L)).thenReturn(espacios);

		List<RecomendacionEspacioDTO> recomendaciones = recomendacionServ.getEspacios(CORREO);

		assertEquals(2, recomendaciones.size(), "Deben salir los dos espacios");
		assertEquals(60, recomendaciones.get(0).getPuntaje(), "La piscina debe tener 10 + 50 puntos");
		assertEquals("Coincide con tus intereses", recomendaciones.get(0).getMotivo(), "Debe explicar el motivo");
		assertEquals(10, recomendaciones.get(1).getPuntaje(), "El gimnasio solo debe tener el puntaje base");
	}

	/**
	 * Prueba que un espacio no disponible no se recomiende.
	 */
	@Test
	void testGetEspaciosOmiteNoDisponibles() {
		Espacio cerrado = espacio(1L, Espacio.Tipo.BBQ);
		cerrado.setDisponible(false);
		List<Espacio> espacios = new ArrayList<>();
		espacios.add(cerrado);
		when(espacioRepo.findByConjuntoIdOrderByNombreAsc(1L)).thenReturn(espacios);

		assertTrue(recomendacionServ.getEspacios(CORREO).isEmpty(), "No debe recomendar espacios cerrados");
	}

	/**
	 * Prueba que los conjuntos cercanos salgan del más cercano al más lejano.
	 */
	@Test
	void testGetConjuntosCercanosOrdenaPorDistancia() {
		List<Conjunto> conjuntos = new ArrayList<>();
		Conjunto lejano = new Conjunto();
		lejano.setLatitud(5.0);
		lejano.setLongitud(-74.0);
		lejano.setMunicipio("Zipaquirá");
		lejano.setTipo(Conjunto.Tipo.CASAS);
		conjuntos.add(lejano);
		Conjunto cercano = new Conjunto();
		cercano.setLatitud(4.71);
		cercano.setLongitud(-74.05);
		cercano.setMunicipio("Bogotá");
		cercano.setTipo(Conjunto.Tipo.APARTAMENTOS);
		conjuntos.add(cercano);
		when(conjuntoRepo.findAll()).thenReturn(conjuntos);

		List<RecomendacionConjuntoDTO> resultado = recomendacionServ.getConjuntosCercanos(4.70, -74.05);

		assertEquals(2, resultado.size(), "Deben salir los dos conjuntos");
		assertTrue(
				resultado.get(0).getDistanciaKm() < resultado.get(1).getDistanciaKm(),
				"El primero debe ser el más cercano");
		assertTrue(resultado.get(0).getMotivo().contains("Bogotá"), "El motivo debe decir el municipio");
	}
}
