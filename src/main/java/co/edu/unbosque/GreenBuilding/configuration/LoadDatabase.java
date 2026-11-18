package co.edu.unbosque.GreenBuilding.configuration;

import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración para cargar datos iniciales en la base de datos. Crea
 * el administrador (los administradores solo se crean desde el back) y, si está
 * activado, datos de ejemplo con conjuntos de Cundinamarca, unidades, espacios,
 * residentes y eventos.
 */
@Configuration
public class LoadDatabase {

	/** Logger para registrar información durante la carga de datos. */
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/** Correo del administrador inicial. */
	@Value("${app.admin.username:admin@greenbuilding.com}")
	private String adminUsername;

	/** Contraseña del administrador inicial. */
	@Value("${app.admin.password:Admin12345*}")
	private String adminPassword;

	/** Indica si se cargan los datos de ejemplo. */
	@Value("${app.datos-ejemplo:true}")
	private boolean datosEjemplo;

	/** Constructor por defecto. */
	public LoadDatabase() {
	}

	/**
	 * Inicializa la base de datos con el administrador y los datos de ejemplo.
	 *
	 * @param usuarioRepo     Repositorio de usuarios
	 * @param conjuntoRepo    Repositorio de conjuntos
	 * @param unidadRepo      Repositorio de unidades
	 * @param espacioRepo     Repositorio de espacios
	 * @param eventoRepo      Repositorio de eventos
	 * @param passwordEncoder Codificador de contraseñas
	 * @return CommandLineRunner que se ejecuta al iniciar la aplicación
	 */
	@Bean
	CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, ConjuntoRepository conjuntoRepo,
			UnidadRepository unidadRepo, EspacioRepository espacioRepo, EventoRepository eventoRepo,
			PasswordEncoder passwordEncoder) {
		return args -> {
			Optional<Usuario> found = usuarioRepo.findByUsername(adminUsername.toLowerCase());
			if (found.isPresent()) {
				log.info("El administrador ya existe, omitiendo la creación del administrador...");
			} else {
				Usuario admin = new Usuario(adminUsername.toLowerCase(), passwordEncoder.encode(adminPassword),
						Usuario.Role.ADMIN);
				admin.setNombre("Administrador");
				admin.setApellido("Green Building");
				admin.setTelefono("0000000000");
				admin.setVerificado(true);
				usuarioRepo.save(admin);
				log.info("Precargando administrador {}", adminUsername);
			}
			if (datosEjemplo && conjuntoRepo.count() == 0) {
				cargarDatosEjemplo(usuarioRepo, conjuntoRepo, unidadRepo, espacioRepo, eventoRepo, passwordEncoder);
			}
		};
	}

	/**
	 * Carga conjuntos, unidades, espacios, residentes y eventos de ejemplo.
	 *
	 * @param usuarioRepo     Repositorio de usuarios
	 * @param conjuntoRepo    Repositorio de conjuntos
	 * @param unidadRepo      Repositorio de unidades
	 * @param espacioRepo     Repositorio de espacios
	 * @param eventoRepo      Repositorio de eventos
	 * @param passwordEncoder Codificador de contraseñas
	 */
	private void cargarDatosEjemplo(UsuarioRepository usuarioRepo, ConjuntoRepository conjuntoRepo,
			UnidadRepository unidadRepo, EspacioRepository espacioRepo, EventoRepository eventoRepo,
			PasswordEncoder passwordEncoder) {
		Conjunto reservas = conjuntoRepo.save(
				new Conjunto("Reservas de la Sabana", Conjunto.Tipo.APARTAMENTOS, "Calle 9 # 11-40", "Chía", 4.8617,
						-74.0581, "Conjunto de apartamentos con amplias zonas verdes, piscina y salón social."));
		Conjunto robles = conjuntoRepo.save(new Conjunto("Los Robles Campestre", Conjunto.Tipo.CASAS,
				"Vereda Fusca km 2", "Cajicá", 4.9186, -74.0277, "Casas campestres con parque y zona BBQ."));
		Conjunto salinas = conjuntoRepo.save(new Conjunto("Mirador de las Salinas", Conjunto.Tipo.APARTAMENTOS,
				"Carrera 7 # 4-20", "Zipaquirá", 5.0221, -74.0048, "Torres modernas con gimnasio y cancha múltiple."));
		Conjunto hacienda = conjuntoRepo.save(new Conjunto("Hacienda Fontanar", Conjunto.Tipo.CASAS,
				"Avenida Sabana # 3-15", "Mosquera", 4.7059, -74.2302, "Casas familiares con zona infantil."));

		List<Unidad> unidades = unidadRepo
				.saveAll(List.of(new Unidad(reservas.getId(), Unidad.Tipo.APARTAMENTO, "Torre 1", "101"),
						new Unidad(reservas.getId(), Unidad.Tipo.APARTAMENTO, "Torre 1", "302"),
						new Unidad(reservas.getId(), Unidad.Tipo.APARTAMENTO, "Torre 2", "501"),
						new Unidad(robles.getId(), Unidad.Tipo.CASA, "Manzana A", "4"),
						new Unidad(robles.getId(), Unidad.Tipo.CASA, "Manzana B", "12"),
						new Unidad(salinas.getId(), Unidad.Tipo.APARTAMENTO, "Torre 3", "804"),
						new Unidad(hacienda.getId(), Unidad.Tipo.CASA, "Etapa 1", "22")));

		Espacio salon = new Espacio(reservas.getId(), "Salón social", Espacio.Tipo.SALON_SOCIAL, 60, LocalTime.of(8, 0),
				LocalTime.of(22, 0), true);
		salon.setDescripcion("Salón con cocina, sonido y capacidad para 60 personas.");
		Espacio piscina = new Espacio(reservas.getId(), "Piscina", Espacio.Tipo.PISCINA, 30, LocalTime.of(7, 0),
				LocalTime.of(19, 0), false);
		piscina.setDescripcion("Piscina climatizada de uso libre.");
		Espacio bbq = new Espacio(reservas.getId(), "Zona BBQ", Espacio.Tipo.BBQ, 20, LocalTime.of(10, 0),
				LocalTime.of(21, 0), true);
		Espacio parque = new Espacio(robles.getId(), "Parque central", Espacio.Tipo.PARQUE, 100, LocalTime.of(6, 0),
				LocalTime.of(20, 0), false);
		Espacio gimnasio = new Espacio(salinas.getId(), "Gimnasio", Espacio.Tipo.GIMNASIO, 15, LocalTime.of(5, 0),
				LocalTime.of(22, 0), true);
		espacioRepo.saveAll(List.of(salon, piscina, bbq, parque, gimnasio));

		Usuario residente = new Usuario("residente@greenbuilding.com", passwordEncoder.encode("Residente123*"),
				Usuario.Role.USER);
		residente.setNombre("Laura");
		residente.setApellido("Gómez");
		residente.setTelefono("3001234567");
		residente.setVerificado(true);
		residente.setConjuntoId(reservas.getId());
		residente.setUnidadId(unidades.get(1).getId());
		residente.setUnidadSolicitada("Torre 1 - 302");
		residente.setInteresesGuardados("FIESTAS,NINOS,PISCINA");
		Usuario pendiente = new Usuario("pendiente@greenbuilding.com", passwordEncoder.encode("Pendiente123*"),
				Usuario.Role.USER);
		pendiente.setNombre("Andrés");
		pendiente.setApellido("Rojas");
		pendiente.setTelefono("3109876543");
		pendiente.setVerificado(true);
		pendiente.setConjuntoId(reservas.getId());
		pendiente.setUnidadSolicitada("Torre 2 - 501");
		usuarioRepo.saveAll(List.of(residente, pendiente));

		eventoRepo.saveAll(List.of(
				evento(reservas, salon, "Fiesta de fin de año",
						"Celebremos juntos el cierre del año con música y comida.", Evento.Tipo.FIESTA, 10,
						LocalTime.of(19, 0), LocalTime.of(23, 0), false, 60, Evento.Plantilla.FIESTA),
				evento(reservas, null, "Tarde de juegos para niños",
						"Juegos, pintucaritas y refrigerio para los más pequeños.", Evento.Tipo.INFANTIL, 3,
						LocalTime.of(15, 0), LocalTime.of(18, 0), true, null, Evento.Plantilla.INFANTIL),
				evento(reservas, salon, "Asamblea general de copropietarios",
						"Revisión del presupuesto y elección del consejo.", Evento.Tipo.ASAMBLEA, 20,
						LocalTime.of(9, 0), LocalTime.of(12, 0), false, null, Evento.Plantilla.ASAMBLEA)));

		log.info(
				"Precargando datos de ejemplo: 4 conjuntos, residente@greenbuilding.com y pendiente@greenbuilding.com");
	}

	/**
	 * Crea un evento de ejemplo.
	 *
	 * @param conjunto     Conjunto del evento
	 * @param espacio      Espacio del evento (opcional)
	 * @param titulo       Título
	 * @param descripcion  Descripción
	 * @param tipo         Tipo de evento
	 * @param diasDesdeHoy Días desde hoy hasta el evento
	 * @param inicio       Hora de inicio
	 * @param fin          Hora de fin
	 * @param paraNinos    true si es para niños
	 * @param cupo         Cupo máximo (opcional)
	 * @param plantilla    Plantilla de la invitación
	 * @return Evento sin guardar
	 */
	private Evento evento(Conjunto conjunto, Espacio espacio, String titulo, String descripcion, Evento.Tipo tipo,
			int diasDesdeHoy, LocalTime inicio, LocalTime fin, boolean paraNinos, Integer cupo,
			Evento.Plantilla plantilla) {
		Evento evento = new Evento();
		evento.setConjuntoId(conjunto.getId());
		evento.setEspacioId(espacio == null ? null : espacio.getId());
		evento.setTitulo(titulo);
		evento.setDescripcion(descripcion);
		evento.setTipo(tipo);
		evento.setFecha(LocalDate.now().plusDays(diasDesdeHoy));
		evento.setHoraInicio(inicio);
		evento.setHoraFin(fin);
		evento.setParaNinos(paraNinos);
		evento.setCupoMaximo(cupo);
		evento.setPlantilla(plantilla);
		return evento;
	}
}
