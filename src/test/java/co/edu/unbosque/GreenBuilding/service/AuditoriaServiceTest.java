package co.edu.unbosque.GreenBuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.GreenBuilding.dto.PaginaAuditoriaDTO;
import co.edu.unbosque.GreenBuilding.model.RegistroAuditoria;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.RegistroAuditoriaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.time.LocalDateTime;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Clase de prueba para el servicio AuditoriaService.
 *
 * <p>Verifica que cada acción quede registrada con su usuario y que la consulta filtre y pagine
 * los registros.
 *
 * @author Juan Arango
 * @version 1.0
 */
class AuditoriaServiceTest {

	/** Repositorio simulado de registros de auditoría. */
	@Mock private RegistroAuditoriaRepository registroRepo;

	/** Repositorio simulado de usuarios. */
	@Mock private UsuarioRepository usuarioRepo;

	/** Servicio que se prueba. */
	@InjectMocks private AuditoriaService auditoriaServ;

	/**
	 * Inicializa los objetos simulados y un ModelMapper real antes de cada prueba.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(auditoriaServ, "modelMapper", new ModelMapper());
		SecurityContextHolder.clearContext();
	}

	/**
	 * Crea un registro de auditoría con los datos indicados.
	 *
	 * @param username Correo del usuario
	 * @param accion Acción realizada
	 * @param motivo Descripción de la acción
	 * @param diasAtras Hace cuántos días ocurrió
	 * @return Registro de auditoría
	 */
	private RegistroAuditoria registro(String username, String accion, String motivo, int diasAtras) {
		RegistroAuditoria registro = new RegistroAuditoria(username, "Nombre", "USER", accion, "Entidad", motivo);
		registro.setFecha(LocalDateTime.now().minusDays(diasAtras));
		return registro;
	}

	/**
	 * Prueba que la acción de un usuario registrado guarde su nombre y su rol.
	 */
	@Test
	void testRegistrarUsuarioExistente() {
		Usuario usuario = new Usuario("ana@correo.com", "hash", Role.USER);
		usuario.setNombre("Ana");
		usuario.setApellido("Pérez");
		when(usuarioRepo.findByUsername("ana@correo.com")).thenReturn(Optional.of(usuario));

		auditoriaServ.registrar("ana@correo.com", "RESERVA", "Reserva", "Solicitó el salón");

		ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
		verify(registroRepo).save(captor.capture());
		assertEquals("ana@correo.com", captor.getValue().getUsername(), "Debe guardar el correo");
		assertEquals("USER", captor.getValue().getRole(), "Debe guardar el rol");
		assertEquals("RESERVA", captor.getValue().getAccion(), "Debe guardar la acción");
	}

	/**
	 * Prueba que la acción de un visitante sin sesión quede como anónima y pública.
	 */
	@Test
	void testRegistrarVisitanteAnonimo() {
		auditoriaServ.registrar(null, "INVITACION", "Evento", "Abrió una invitación");

		ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
		verify(registroRepo).save(captor.capture());
		assertEquals("anónimo", captor.getValue().getUsername(), "Debe quedar como anónimo");
		assertEquals("PUBLICO", captor.getValue().getRole(), "Debe quedar como público");
	}

	/**
	 * Prueba que sin indicar usuario se use el de la sesión iniciada.
	 */
	@Test
	void testRegistrarUsaLaSesionIniciada() {
		Usuario admin = new Usuario("admin@correo.com", "hash", Role.ADMIN);
		when(usuarioRepo.findByUsername("admin@correo.com")).thenReturn(Optional.of(admin));
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("admin@correo.com", null));

		auditoriaServ.registrar("CREAR", "Conjunto", "Creó un conjunto");

		ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
		verify(registroRepo).save(captor.capture());
		assertEquals("admin@correo.com", captor.getValue().getUsername(), "Debe usar el usuario de la sesión");
		assertEquals("ADMIN", captor.getValue().getRole(), "Debe guardar el rol de administrador");
		SecurityContextHolder.clearContext();
	}

	/**
	 * Prueba que la consulta filtre por acción y por texto del motivo.
	 */
	@Test
	void testGetAllFiltraPorAccionYMotivo() {
		List<RegistroAuditoria> todos = new ArrayList<>();
		todos.add(registro("ana@correo.com", "RESERVA", "Solicitó el salón", 0));
		todos.add(registro("luis@correo.com", "CREAR", "Creó el conjunto Torres", 1));
		todos.add(registro("ana@correo.com", "CREAR", "Creó la piscina", 2));
		when(registroRepo.findAllByOrderByFechaDescIdDesc()).thenReturn(todos);

		PaginaAuditoriaDTO crear = auditoriaServ.getAll(null, "crear", null, null, null, null, 0, 10);
		assertEquals(2, crear.getTotalElements(), "Deben salir 2 registros de CREAR");

		PaginaAuditoriaDTO piscina = auditoriaServ.getAll("ana", null, null, "PISCINA", null, null, 0, 10);
		assertEquals(1, piscina.getTotalElements(), "Debe salir 1 registro de Ana con piscina");
	}

	/**
	 * Prueba que la paginación divida los registros y corrija los valores inválidos.
	 */
	@Test
	void testGetAllPagina() {
		List<RegistroAuditoria> todos = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			todos.add(registro("ana@correo.com", "RESERVA", "Registro " + i, i));
		}
		when(registroRepo.findAllByOrderByFechaDescIdDesc()).thenReturn(todos);

		PaginaAuditoriaDTO segunda = auditoriaServ.getAll(null, null, null, null, null, null, 1, 2);
		assertEquals(3, segunda.getTotalPages(), "Con 5 registros de 2 en 2 deben ser 3 páginas");
		assertEquals(2, segunda.getContent().size(), "La segunda página debe tener 2 registros");

		PaginaAuditoriaDTO corregida = auditoriaServ.getAll(null, null, null, null, null, null, -3, 500);
		assertEquals(0, corregida.getNumber(), "Una página negativa debe volverse 0");
		assertEquals(100, corregida.getSize(), "El tamaño máximo debe ser 100");
	}
}
