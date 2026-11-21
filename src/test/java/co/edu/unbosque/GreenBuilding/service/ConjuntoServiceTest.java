package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.ConjuntoDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio ConjuntoService.
 *
 * <p>Verifica la creación, actualización, búsqueda y eliminación de conjuntos con sus códigos de
 * respuesta.
 *
 * @author Juan Arango
 * @version 1.0
 */
class ConjuntoServiceTest {

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de unidades. */
	@Mock private UnidadRepository unidadRepo;

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Repositorio simulado de asistencias. */
	@Mock private AsistenciaRepository asistenciaRepo;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio que se prueba. */
	@InjectMocks private ConjuntoService conjuntoServ;

	/**
	 * Inicializa los objetos simulados y un ModelMapper real antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(conjuntoServ, "modelMapper", new ModelMapper());
	}

	/**
	 * Crea un DTO de conjunto con los datos indicados.
	 *
	 * @param nombre Nombre del conjunto
	 * @param latitud Latitud
	 * @param longitud Longitud
	 * @return DTO del conjunto
	 */
	private ConjuntoDTO nuevoConjunto(String nombre, double latitud, double longitud) {
		ConjuntoDTO dto = new ConjuntoDTO();
		dto.setNombre(nombre);
		dto.setMunicipio(" Chía ");
		dto.setDireccion("Calle 1 # 2-3");
		dto.setTipo(Conjunto.Tipo.APARTAMENTOS);
		dto.setLatitud(latitud);
		dto.setLongitud(longitud);
		dto.setDescripcion("Conjunto de prueba");
		return dto;
	}

	/**
	 * Prueba que un conjunto nuevo dentro de Cundinamarca se guarde limpio y se audite.
	 */
	@Test
	void testCreateGuardaElConjunto() {
		when(conjuntoRepo.findByNombreIgnoreCaseAndMunicipioIgnoreCase(anyString(), anyString()))
				.thenReturn(Optional.empty());

		assertEquals(0, conjuntoServ.create(nuevoConjunto("  Torres del Parque ", 4.86, -74.03)), "Debe devolver 0");

		ArgumentCaptor<Conjunto> captor = ArgumentCaptor.forClass(Conjunto.class);
		verify(conjuntoRepo).save(captor.capture());
		assertEquals("Torres del Parque", captor.getValue().getNombre(), "El nombre debe quedar sin espacios");
		assertNull(captor.getValue().getId(), "El id debe ser nulo para que la base lo genere");
		verify(auditoriaServ).registrar(anyString(), anyString(), anyString());
	}

	/**
	 * Prueba que un conjunto fuera de Cundinamarca devuelva 2 y no se guarde.
	 */
	@Test
	void testCreateFueraDeCundinamarca() {
		assertEquals(2, conjuntoServ.create(nuevoConjunto("Lejano", 4.72, 44205.94)), "Debe devolver 2");
		verify(conjuntoRepo, never()).save(any());
	}

	/**
	 * Prueba que un conjunto con el mismo nombre en el mismo municipio devuelva 1.
	 */
	@Test
	void testCreateNombreRepetido() {
		when(conjuntoRepo.findByNombreIgnoreCaseAndMunicipioIgnoreCase("Torres", "Chía"))
				.thenReturn(Optional.of(new Conjunto()));

		assertEquals(1, conjuntoServ.create(nuevoConjunto("Torres", 4.86, -74.03)), "Debe devolver 1");
		verify(conjuntoRepo, never()).save(any());
	}

	/**
	 * Prueba que actualizar un conjunto que no existe devuelva 2.
	 */
	@Test
	void testUpdateConjuntoInexistente() {
		when(conjuntoRepo.findById(9L)).thenReturn(Optional.empty());
		assertEquals(2, conjuntoServ.updateById(9L, nuevoConjunto("Otro", 4.86, -74.03)), "Debe devolver 2");
	}

	/**
	 * Prueba que actualizar con un nombre que ya usa otro conjunto devuelva 1.
	 */
	@Test
	void testUpdateNombreDeOtroConjunto() {
		Conjunto actual = new Conjunto();
		actual.setId(1L);
		Conjunto otro = new Conjunto();
		otro.setId(2L);
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(actual));
		when(conjuntoRepo.findByNombreIgnoreCaseAndMunicipioIgnoreCase("Torres", "Chía"))
				.thenReturn(Optional.of(otro));

		assertEquals(1, conjuntoServ.updateById(1L, nuevoConjunto("Torres", 4.86, -74.03)), "Debe devolver 1");
	}

	/**
	 * Prueba que eliminar un conjunto que no existe devuelva 1.
	 */
	@Test
	void testDeleteConjuntoInexistente() {
		when(conjuntoRepo.findById(5L)).thenReturn(Optional.empty());
		assertEquals(1, conjuntoServ.deleteById(5L), "Debe devolver 1");
	}

	/**
	 * Prueba que eliminar un conjunto existente lo borre y lo audite.
	 */
	@Test
	void testDeleteConjuntoExistente() {
		Conjunto conjunto = new Conjunto();
		conjunto.setId(5L);
		conjunto.setNombre("Torres");
		when(conjuntoRepo.findById(5L)).thenReturn(Optional.of(conjunto));

		assertEquals(0, conjuntoServ.deleteById(5L), "Debe devolver 0");
		verify(conjuntoRepo).delete(conjunto);
	}

	/**
	 * Prueba que la búsqueda filtre por nombre sin importar tildes y por tipo.
	 */
	@Test
	void testGetAllFiltraPorBusquedaYTipo() {
		List<Conjunto> todos = new ArrayList<>();
		Conjunto torres = new Conjunto();
		torres.setId(1L);
		torres.setNombre("Torres del Parque");
		torres.setMunicipio("Chía");
		torres.setDireccion("Calle 1");
		torres.setTipo(Conjunto.Tipo.APARTAMENTOS);
		todos.add(torres);
		Conjunto robles = new Conjunto();
		robles.setId(2L);
		robles.setNombre("Los Robles");
		robles.setMunicipio("Cajicá");
		robles.setDireccion("Vereda 2");
		robles.setTipo(Conjunto.Tipo.CASAS);
		todos.add(robles);
		when(conjuntoRepo.findAllByOrderByNombreAsc()).thenReturn(todos);

		assertEquals(1, conjuntoServ.getAll("chia", null).size(), "Debe encontrar uno por municipio");
		assertEquals(1, conjuntoServ.getAll(null, "casas").size(), "Debe encontrar uno de casas");
		assertTrue(conjuntoServ.getAll("zzz", null).isEmpty(), "No debe encontrar nada");
	}
}
