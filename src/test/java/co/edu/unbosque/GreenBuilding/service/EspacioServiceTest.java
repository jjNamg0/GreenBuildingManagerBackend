package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.EspacioDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.time.LocalTime;
import java.util.ArrayList;
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
 * Clase de prueba para el servicio EspacioService.
 *
 * <p>Verifica la gestión de espacios comunes y quién puede ver los espacios de un conjunto.
 *
 * @author Juan Arango
 * @version 1.0
 */
class EspacioServiceTest {

	/** Repositorio simulado de espacios. */
	@Mock private EspacioRepository espacioRepo;

	/** Repositorio simulado de conjuntos. */
	@Mock private ConjuntoRepository conjuntoRepo;

	/** Repositorio simulado de reservas. */
	@Mock private ReservaRepository reservaRepo;

	/** Repositorio simulado de eventos. */
	@Mock private EventoRepository eventoRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Servicio de auditoría simulado. */
	@Mock private AuditoriaService auditoriaServ;

	/** Servicio que se prueba. */
	@InjectMocks private EspacioService espacioServ;

	/**
	 * Inicializa los objetos simulados y un ModelMapper real antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(espacioServ, "modelMapper", new ModelMapper());
	}

	/**
	 * Crea un DTO de espacio para el conjunto 1.
	 *
	 * @return DTO del espacio
	 */
	private EspacioDTO nuevoEspacio() {
		EspacioDTO dto = new EspacioDTO();
		dto.setConjuntoId(1L);
		dto.setNombre(" Salón social ");
		dto.setTipo(Espacio.Tipo.SALON_SOCIAL);
		dto.setDescripcion("Para 50 personas");
		dto.setCapacidad(50);
		dto.setHoraApertura("08:00");
		dto.setHoraCierre("22:00");
		dto.setRequiereReserva(true);
		dto.setDisponible(true);
		dto.setImagenUrl("  ");
		return dto;
	}

	/**
	 * Prueba que un espacio nuevo se guarde con las horas leídas y sin imagen.
	 */
	@Test
	void testCreateGuardaElEspacio() {
		Conjunto conjunto = new Conjunto();
		conjunto.setId(1L);
		conjunto.setNombre("Torres");
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.of(conjunto));

		assertEquals(0, espacioServ.create(nuevoEspacio()), "Debe devolver 0");

		ArgumentCaptor<Espacio> captor = ArgumentCaptor.forClass(Espacio.class);
		verify(espacioRepo).save(captor.capture());
		assertEquals("Salón social", captor.getValue().getNombre(), "El nombre debe quedar sin espacios");
		assertEquals(LocalTime.of(8, 0), captor.getValue().getHoraApertura(), "Debe abrir a las 8");
		assertNull(captor.getValue().getImagenUrl(), "Una imagen vacía debe quedar nula");
	}

	/**
	 * Prueba que crear un espacio en un conjunto que no existe devuelva 1.
	 */
	@Test
	void testCreateConjuntoInexistente() {
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.empty());
		assertEquals(1, espacioServ.create(nuevoEspacio()), "Debe devolver 1");
		verify(espacioRepo, never()).save(any());
	}

	/**
	 * Prueba los códigos de actualizar un espacio o un conjunto que no existen.
	 */
	@Test
	void testUpdateCodigosDeError() {
		when(espacioRepo.findById(3L)).thenReturn(Optional.empty());
		assertEquals(2, espacioServ.updateById(3L, nuevoEspacio()), "Sin espacio debe devolver 2");

		when(espacioRepo.findById(3L)).thenReturn(Optional.of(new Espacio()));
		when(conjuntoRepo.findById(1L)).thenReturn(Optional.empty());
		assertEquals(3, espacioServ.updateById(3L, nuevoEspacio()), "Sin conjunto debe devolver 3");
	}

	/**
	 * Prueba que eliminar un espacio borre también sus reservas.
	 */
	@Test
	void testDeleteEspacio() {
		Espacio espacio = new Espacio();
		espacio.setId(3L);
		espacio.setNombre("Piscina");
		when(espacioRepo.findById(3L)).thenReturn(Optional.of(espacio));
		when(eventoRepo.findByEspacioId(3L)).thenReturn(new ArrayList<>());
		when(reservaRepo.findByEspacioId(3L)).thenReturn(new ArrayList<>());

		assertEquals(0, espacioServ.deleteById(3L), "Debe devolver 0");
		verify(espacioRepo).delete(espacio);
		when(espacioRepo.findById(4L)).thenReturn(Optional.empty());
		assertEquals(1, espacioServ.deleteById(4L), "Un espacio inexistente debe devolver 1");
	}

	/**
	 * Prueba que el administrador vea cualquier conjunto y el residente solo el suyo.
	 */
	@Test
	void testPuedeVerConjunto() {
		Usuario admin = new Usuario("admin@correo.com", "clave", Role.ADMIN);
		Usuario residente = new Usuario("ana@correo.com", "clave", Role.USER);
		residente.setConjuntoId(1L);
		when(usuarioRepo.findByUsername("admin@correo.com")).thenReturn(Optional.of(admin));
		when(usuarioRepo.findByUsername("ana@correo.com")).thenReturn(Optional.of(residente));
		when(usuarioRepo.findByUsername("nadie@correo.com")).thenReturn(Optional.empty());

		assertTrue(espacioServ.puedeVerConjunto("admin@correo.com", 9L), "El admin debe ver todo");
		assertTrue(espacioServ.puedeVerConjunto("ana@correo.com", 1L), "La residente debe ver su conjunto");
		assertFalse(espacioServ.puedeVerConjunto("ana@correo.com", 2L), "No debe ver otro conjunto");
		assertFalse(espacioServ.puedeVerConjunto("nadie@correo.com", 1L), "Un usuario inexistente no ve nada");
	}
}
