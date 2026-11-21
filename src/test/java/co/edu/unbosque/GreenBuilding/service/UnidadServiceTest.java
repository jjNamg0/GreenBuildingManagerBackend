package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.UnidadDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
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
 * Clase de prueba para el servicio UnidadService.
 *
 * <p>Verifica la creación, actualización y eliminación de apartamentos y casas.
 *
 * @author Juan Arango
 * @version 1.0
 */
class UnidadServiceTest {

	/** Repositorio simulado de unidades. */
	@Mock private UnidadRepository unidadRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio que se prueba. */
	@InjectMocks private UnidadService unidadServ;

	/** Conjunto de prueba. */
	private Conjunto conjunto;

	/**
	 * Inicializa los objetos simulados, un ModelMapper real y el conjunto antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(unidadServ, "modelMapper", new ModelMapper());
		conjunto = new Conjunto();
		conjunto.setId(1L);
		conjunto.setNombre("Torres");
	}

	/**
	 * Crea un DTO de unidad para el conjunto de prueba.
	 *
	 * @param agrupacion Torre o manzana
	 * @param numero Número de la unidad
	 * @return DTO de la unidad
	 */
	private UnidadDTO nuevaUnidad(String agrupacion, String numero) {
		UnidadDTO dto = new UnidadDTO();
		dto.setConjuntoId(1L);
		dto.setTipo(Unidad.Tipo.APARTAMENTO);
		dto.setAgrupacion(agrupacion);
		dto.setNumero(numero);
		return dto;
	}

	/**
	 * Prueba que una unidad nueva se guarde.
	 */
	@Test
	void testCreateGuardaLaUnidad() {
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));
		when(unidadRepo.findByConjuntoIdAndAgrupacionIgnoreCaseAndNumeroIgnoreCase(1L, "Torre 1", "101"))
				.thenReturn(Optional.empty());

		assertEquals(0, unidadServ.create(nuevaUnidad(" Torre 1 ", " 101 ")), "Debe devolver 0");
		verify(unidadRepo).save(any(Unidad.class));
	}

	/**
	 * Prueba que crear una unidad en un conjunto que no existe devuelva 2.
	 */
	@Test
	void testCreateConjuntoInexistente() {
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.empty());
		assertEquals(2, unidadServ.create(nuevaUnidad("Torre 1", "101")), "Debe devolver 2");
		verify(unidadRepo, never()).save(any());
	}

	/**
	 * Prueba que una unidad repetida en el mismo conjunto devuelva 1.
	 */
	@Test
	void testCreateUnidadRepetida() {
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));
		when(unidadRepo.findByConjuntoIdAndAgrupacionIgnoreCaseAndNumeroIgnoreCase(1L, "Torre 1", "101"))
				.thenReturn(Optional.of(new Unidad()));

		assertEquals(1, unidadServ.create(nuevaUnidad("Torre 1", "101")), "Debe devolver 1");
	}

	/**
	 * Prueba que no se pueda eliminar una unidad con residentes.
	 */
	@Test
	void testDeleteUnidadConResidentes() {
		Unidad unidad = new Unidad(1L, Unidad.Tipo.APARTAMENTO, "Torre 1", "101");
		unidad.setId(7L);
		List<Usuario> residentes = new ArrayList<>();
		residentes.add(new Usuario());
		when(unidadRepo.findById(7L)).thenReturn(Optional.of(unidad));
		when(usuarioRepo.findByUnidadId(7L)).thenReturn(residentes);

		assertEquals(2, unidadServ.deleteById(7L), "Debe devolver 2");
		verify(unidadRepo, never()).delete(any());
	}

	/**
	 * Prueba que una unidad vacía sí se elimine.
	 */
	@Test
	void testDeleteUnidadVacia() {
		Unidad unidad = new Unidad(1L, Unidad.Tipo.APARTAMENTO, "Torre 1", "101");
		unidad.setId(7L);
		when(unidadRepo.findById(7L)).thenReturn(Optional.of(unidad));
		when(usuarioRepo.findByUnidadId(7L)).thenReturn(new ArrayList<>());

		assertEquals(0, unidadServ.deleteById(7L), "Debe devolver 0");
		verify(unidadRepo).delete(unidad);
	}

	/**
	 * Prueba que las unidades de un conjunto que no existe sean una lista vacía.
	 */
	@Test
	void testGetByConjuntoInexistente() {
		when(conjuntoRepo.findById(99L)).thenReturn(Optional.empty());
		assertTrue(unidadServ.getByConjunto(99L).isEmpty(), "Debe devolver una lista vacía");
	}
}
