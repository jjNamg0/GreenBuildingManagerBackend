package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.CambioPasswordDTO;
import co.edu.unbosque.GreenBuilding.dto.CodigoDTO;
import co.edu.unbosque.GreenBuilding.dto.UsuarioDTO;
import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona operaciones CRUD y funcionalidades adicionales para
 * usuarios: registro, verificación del correo, recuperación de contraseña,
 * perfil, intereses y gestión de residentes. Implementa la interfaz
 * CRUDOperation para operaciones básicas de creación, lectura, actualización y
 * eliminación.
 */
@Service
public class UsuarioService implements CRUDOperation<UsuarioDTO> {

	/** Intereses que puede elegir un residente. */
	public static final List<String> INTERESES_VALIDOS = List.of("FIESTAS", "NINOS", "DEPORTE", "CULTURA", "ASAMBLEAS",
			"PISCINA", "GIMNASIO", "BBQ", "NATURALEZA", "MASCOTAS");

	/** Filtros válidos para la lista de usuarios. */
	public static final List<String> FILTROS_VALIDOS = List.of("TODOS", "SIN_UNIDAD", "RESIDENTES", "INACTIVOS");

	/** Repositorio para acceder a los datos de usuarios en la base de datos. */
	@Autowired
	private UsuarioRepository usuarioRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a las unidades. */
	@Autowired
	private UnidadRepository unidadRepo;

	/** Repositorio para acceder a las reservas. */
	@Autowired
	private ReservaRepository reservaRepo;

	/** Repositorio para acceder a las asistencias a eventos. */
	@Autowired
	private AsistenciaRepository asistenciaRepo;

	/** Mapeador para convertir entre entidades Usuario y DTOs UsuarioDTO. */
	@Autowired
	private ModelMapper modelMapper;

	/** Codificador para encriptar contraseñas de usuarios. */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/** Servicio de códigos de verificación. */
	@Autowired
	private CodigoVerificacionService codigoServ;

	/** Servicio de auditoría. */
	@Autowired
	private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public UsuarioService() {
	}

	/**
	 * Cuenta el número total de usuarios en la base de datos.
	 *
	 * @return Número total de usuarios
	 */
	@Override
	public long count() {
		return usuarioRepo.count();
	}

	/**
	 * Verifica si existe un usuario con el ID especificado.
	 *
	 * @param id ID del usuario a verificar
	 * @return true si el usuario existe, false en caso contrario
	 */
	@Override
	public boolean exist(Long id) {
		return usuarioRepo.existsById(id);
	}

	/**
	 * Registra un nuevo residente. Codifica la contraseña, lo deja sin verificar y
	 * le envía el código de verificación al correo. Siempre se crea con rol USER.
	 *
	 * @param data DTO con los datos del usuario a crear
	 * @return 0 si el registro fue exitoso, 1 si el correo ya está registrado, 2 si
	 *         el conjunto no existe, 3 si se registró pero no se pudo enviar el
	 *         código
	 */
	@Override
	public int create(UsuarioDTO data) {
		String correo = TextoUtil.correo(data.getUsername());
		if (findUsernameAlreadyTaken(correo)) {
			return 1;
		}
		Optional<Conjunto> conjunto = data.getConjuntoId() == null ? Optional.empty()
				: conjuntoRepo.findById(data.getConjuntoId());
		if (!conjunto.isPresent()) {
			return 2;
		}
		Usuario entity = new Usuario(correo, passwordEncoder.encode(data.getPassword()), Role.USER);
		entity.setNombre(TextoUtil.limpiar(data.getNombre()));
		entity.setApellido(TextoUtil.limpiar(data.getApellido()));
		entity.setTelefono(TextoUtil.limpiar(data.getTelefono()));
		entity.setConjuntoId(conjunto.get().getId());
		entity.setUnidadSolicitada(TextoUtil.limpiar(data.getUnidadSolicitada()));
		usuarioRepo.save(entity);
		auditoriaServ.registrar(correo, "REGISTRO", "Usuario", "Registro en " + conjunto.get().getNombre());
		if (codigoServ.generarYEnviar(entity, Proposito.VERIFICACION) == 0) {
			return 0;
		} else {
			return 3;
		}
	}

	/**
	 * Obtiene todos los usuarios de la base de datos.
	 *
	 * @return Lista de DTOs de usuarios
	 */
	@Override
	public List<UsuarioDTO> getAll() {
		List<Usuario> entityList = usuarioRepo.findAll();
		List<UsuarioDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			UsuarioDTO dto = aDTO(entity);
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene los usuarios que cumplen un filtro y una búsqueda, del más reciente
	 * al más antiguo.
	 *
	 * @param filtro   TODOS, SIN_UNIDAD, RESIDENTES o INACTIVOS
	 * @param busqueda Texto a buscar en el nombre, correo o unidad solicitada
	 *                 (opcional)
	 * @return Lista de DTOs de usuarios
	 */
	public List<UsuarioDTO> getAll(String filtro, String busqueda) {
		List<UsuarioDTO> dtoList = new ArrayList<>();
		for (Usuario usuario : usuarioRepo.findAllByOrderByFechaRegistroDesc()) {
			if (cumpleFiltro(usuario, filtro) && coincideBusqueda(usuario, busqueda)) {
				dtoList.add(aDTO(usuario));
			}
		}
		return dtoList;
	}

	/**
	 * Elimina un usuario por su ID, junto con sus reservas, su asistencia a eventos
	 * y sus códigos. Las cuentas de administrador no se pueden eliminar.
	 *
	 * @param id ID del usuario a eliminar
	 * @return 0 si la eliminación fue exitosa, 1 si el usuario no existe, 2 si es
	 *         administrador
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Usuario> found = usuarioRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Usuario usuario = found.get();
		if (usuario.getRole() == Role.ADMIN) {
			return 2;
		}
		asistenciaRepo.deleteAll(asistenciaRepo.findByUsuarioId(id));
		reservaRepo.deleteAll(reservaRepo.findByUsuarioId(id));
		codigoServ.eliminarTodos(usuario.getUsername());
		usuarioRepo.delete(usuario);
		auditoriaServ.registrar("ELIMINAR", "Usuario", "Eliminó la cuenta de " + usuario.getUsername());
		return 0;
	}

	/**
	 * Actualiza el nombre, apellido y teléfono de un usuario por su ID.
	 *
	 * @param id      ID del usuario a actualizar
	 * @param newData DTO con los nuevos datos del usuario
	 * @return 0 si la actualización fue exitosa, 2 si el usuario no existe
	 */
	@Override
	public int updateById(Long id, UsuarioDTO newData) {
		Optional<Usuario> found = usuarioRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Usuario temp = found.get();
		temp.setNombre(TextoUtil.limpiar(newData.getNombre()));
		temp.setApellido(TextoUtil.limpiar(newData.getApellido()));
		temp.setTelefono(TextoUtil.limpiar(newData.getTelefono()));
		usuarioRepo.save(temp);
		auditoriaServ.registrar("ACTUALIZAR", "Usuario", "Actualizó los datos de " + temp.getUsername());
		return 0;
	}

	/**
	 * Obtiene un usuario por su ID.
	 *
	 * @param id ID del usuario a obtener
	 * @return DTO del usuario si existe, null en caso contrario
	 */
	public UsuarioDTO getById(Long id) {
		Optional<Usuario> found = usuarioRepo.findById(id);
		if (found.isPresent()) {
			return aDTO(found.get());
		} else {
			return null;
		}
	}

	/**
	 * Obtiene un usuario por su correo.
	 *
	 * @param username Correo del usuario
	 * @return DTO del usuario si existe, null en caso contrario
	 */
	public UsuarioDTO getByUsername(String username) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (found.isPresent()) {
			return aDTO(found.get());
		} else {
			return null;
		}
	}

	/**
	 * Actualiza el perfil (nombre, apellido y teléfono) del usuario con la sesión
	 * iniciada.
	 *
	 * @param username Correo del usuario
	 * @param newData  DTO con los nuevos datos
	 * @return 0 si la actualización fue exitosa, 1 si el usuario no existe
	 */
	public int updateMe(String username, UsuarioDTO newData) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent()) {
			return 1;
		}
		Usuario temp = found.get();
		temp.setNombre(TextoUtil.limpiar(newData.getNombre()));
		temp.setApellido(TextoUtil.limpiar(newData.getApellido()));
		temp.setTelefono(TextoUtil.limpiar(newData.getTelefono()));
		usuarioRepo.save(temp);
		auditoriaServ.registrar(username, "ACTUALIZAR", "Usuario", "Actualizó su perfil");
		return 0;
	}

	/**
	 * Cambia la contraseña del usuario con la sesión iniciada.
	 *
	 * @param username Correo del usuario
	 * @param data     DTO con la contraseña actual y la nueva
	 * @return 0 si el cambio fue exitoso, 1 si la contraseña actual no es correcta,
	 *         2 si la nueva es igual a la actual, 3 si el usuario no existe
	 */
	public int changePassword(String username, CambioPasswordDTO data) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent()) {
			return 3;
		}
		Usuario temp = found.get();
		if (!passwordEncoder.matches(data.getPasswordActual(), temp.getPassword())) {
			return 1;
		}
		if (data.getPasswordActual().equals(data.getPasswordNueva())) {
			return 2;
		}
		temp.setPassword(passwordEncoder.encode(data.getPasswordNueva()));
		usuarioRepo.save(temp);
		auditoriaServ.registrar(username, "ACTUALIZAR", "Usuario", "Cambió su contraseña");
		return 0;
	}

	/**
	 * Reemplaza los intereses del usuario con la sesión iniciada.
	 *
	 * @param username  Correo del usuario
	 * @param intereses Lista de intereses
	 * @return 0 si se guardaron, 1 si algún interés no es válido, 2 si el usuario
	 *         no existe
	 */
	public int updateIntereses(String username, List<String> intereses) {
		List<String> limpios = new ArrayList<>();
		for (String interes : intereses) {
			String valor = TextoUtil.limpiar(interes).toUpperCase();
			if (!INTERESES_VALIDOS.contains(valor)) {
				return 1;
			}
			if (!limpios.contains(valor)) {
				limpios.add(valor);
			}
		}
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent()) {
			return 2;
		}
		Usuario temp = found.get();
		String guardados = "";
		for (int i = 0; i < limpios.size(); i++) {
			guardados = i == 0 ? limpios.get(i) : guardados + "," + limpios.get(i);
		}
		temp.setInteresesGuardados(guardados);
		usuarioRepo.save(temp);
		auditoriaServ.registrar(username, "ACTUALIZAR", "Usuario", "Actualizó sus intereses");
		return 0;
	}

	/**
	 * Asigna una unidad a un residente. El residente pasa al conjunto de la unidad.
	 *
	 * @param id       ID del usuario
	 * @param unidadId ID de la unidad
	 * @return 0 si la asignación fue exitosa, 1 si el usuario no existe, 2 si la
	 *         unidad no existe, 3 si el usuario es administrador
	 */
	public int asignarUnidad(Long id, Long unidadId) {
		Optional<Usuario> found = usuarioRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Optional<Unidad> unidad = unidadRepo.findById(unidadId);
		if (!unidad.isPresent()) {
			return 2;
		}
		Usuario temp = found.get();
		if (temp.getRole() == Role.ADMIN) {
			return 3;
		}
		temp.setUnidadId(unidad.get().getId());
		temp.setConjuntoId(unidad.get().getConjuntoId());
		usuarioRepo.save(temp);
		auditoriaServ.registrar("ACTUALIZAR", "Usuario",
				"Asignó " + unidad.get().getDescripcion() + " a " + temp.getNombreCompleto());
		return 0;
	}

	/**
	 * Activa o desactiva la cuenta de un residente.
	 *
	 * @param id     ID del usuario
	 * @param activo true para activar, false para desactivar
	 * @return 0 si el cambio fue exitoso, 1 si el usuario no existe, 3 si el
	 *         usuario es administrador
	 */
	public int cambiarEstado(Long id, boolean activo) {
		Optional<Usuario> found = usuarioRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Usuario temp = found.get();
		if (temp.getRole() == Role.ADMIN) {
			return 3;
		}
		temp.setActivo(activo);
		usuarioRepo.save(temp);
		auditoriaServ.registrar("ACTUALIZAR", "Usuario",
				(activo ? "Activó" : "Desactivó") + " la cuenta de " + temp.getUsername());
		return 0;
	}

	/**
	 * Verifica el correo de un usuario con el código que se le envió.
	 *
	 * @param data DTO con el correo y el código
	 * @return 0 si la cuenta quedó verificada, 1 si ya estaba verificada, 2 si el
	 *         código no es correcto, 3 si venció, 4 si superó los intentos, 5 si no
	 *         tiene un código activo
	 */
	public int verificar(CodigoDTO data) {
		Optional<Usuario> found = usuarioRepo.findByUsername(TextoUtil.correo(data.getUsername()));
		if (!found.isPresent()) {
			return 2;
		}
		Usuario temp = found.get();
		if (temp.isVerificado()) {
			return 1;
		}
		int resultado = codigoServ.validar(temp.getUsername(), data.getCodigo(), Proposito.VERIFICACION);
		if (resultado != 0) {
			return resultado;
		}
		temp.setVerificado(true);
		usuarioRepo.save(temp);
		codigoServ.eliminar(temp.getUsername(), Proposito.VERIFICACION);
		auditoriaServ.registrar(temp.getUsername(), "VERIFICACION", "Usuario", "Verificó su correo");
		return 0;
	}

	/**
	 * Reenvía el código de verificación a un usuario que aún no verificó su cuenta.
	 * Si el correo no está registrado o ya está verificado, no hace nada, para no
	 * revelar qué correos existen.
	 *
	 * @param username Correo del usuario
	 * @return 0 si se procesó la solicitud, 1 si debe esperar un minuto, 2 si el
	 *         correo no se pudo enviar
	 */
	public int reenviarCodigo(String username) {
		Optional<Usuario> found = usuarioRepo.findByUsername(TextoUtil.correo(username));
		if (!found.isPresent() || found.get().isVerificado()) {
			return 0;
		}
		return codigoServ.generarYEnviar(found.get(), Proposito.VERIFICACION);
	}

	/**
	 * Envía el código para restablecer la contraseña. Si el correo no está
	 * registrado o la cuenta está desactivada, no hace nada, para no revelar qué
	 * correos existen.
	 *
	 * @param username Correo del usuario
	 * @return 0 si se procesó la solicitud, 1 si debe esperar un minuto, 2 si el
	 *         correo no se pudo enviar
	 */
	public int olvidePassword(String username) {
		Optional<Usuario> found = usuarioRepo.findByUsername(TextoUtil.correo(username));
		if (!found.isPresent() || !found.get().isActivo()) {
			return 0;
		}
		return codigoServ.generarYEnviar(found.get(), Proposito.RESTABLECER);
	}

	/**
	 * Restablece la contraseña con el código enviado al correo. La cuenta queda
	 * verificada, porque el usuario demostró que el correo es suyo.
	 *
	 * @param data DTO con el correo, el código y la nueva contraseña
	 * @return 0 si la contraseña se cambió, 2 si el código no es correcto, 3 si
	 *         venció, 4 si superó los intentos, 5 si no tiene un código activo
	 */
	public int restablecerPassword(CodigoDTO data) {
		Optional<Usuario> found = usuarioRepo.findByUsername(TextoUtil.correo(data.getUsername()));
		if (!found.isPresent()) {
			return 2;
		}
		Usuario temp = found.get();
		int resultado = codigoServ.validar(temp.getUsername(), data.getCodigo(), Proposito.RESTABLECER);
		if (resultado != 0) {
			return resultado;
		}
		temp.setPassword(passwordEncoder.encode(data.getNuevaPassword()));
		temp.setVerificado(true);
		usuarioRepo.save(temp);
		codigoServ.eliminarTodos(temp.getUsername());
		auditoriaServ.registrar(temp.getUsername(), "RESTABLECER_PASSWORD", "Usuario", "Restableció su contraseña");
		return 0;
	}

	/**
	 * Verifica si un nombre de usuario ya está en uso.
	 *
	 * @param username Nombre de usuario a verificar
	 * @return true si el nombre de usuario ya está en uso, false en caso contrario
	 */
	public boolean findUsernameAlreadyTaken(String username) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		return found.isPresent();
	}

	/**
	 * Valida las credenciales de un usuario.
	 *
	 * @param username Nombre de usuario
	 * @param password Contraseña sin encriptar
	 * @return 0 si las credenciales son válidas, 1 si son inválidas
	 */
	public int validateCredentials(String username, String password) {
		Optional<Usuario> userOpt = usuarioRepo.findByUsername(username);
		if (userOpt.isPresent()) {
			Usuario user = userOpt.get();
			if (passwordEncoder.matches(password, user.getPassword())) {
				return 0;
			}
		}
		return 1;
	}

	/**
	 * Indica si un usuario cumple el filtro de la lista de administración.
	 *
	 * @param usuario Usuario a revisar
	 * @param filtro  Filtro elegido
	 * @return true si lo cumple
	 */
	private boolean cumpleFiltro(Usuario usuario, String filtro) {
		String valor = TextoUtil.vacio(filtro) ? "TODOS" : filtro.trim().toUpperCase();
		if (valor.equals("SIN_UNIDAD")) {
			return usuario.getRole() == Role.USER && usuario.getUnidadId() == null && usuario.isActivo();
		} else if (valor.equals("RESIDENTES")) {
			return usuario.getUnidadId() != null;
		} else if (valor.equals("INACTIVOS")) {
			return !usuario.isActivo();
		} else {
			return true;
		}
	}

	/**
	 * Indica si un usuario coincide con el texto buscado.
	 *
	 * @param usuario  Usuario a revisar
	 * @param busqueda Texto buscado
	 * @return true si coincide o si no hay búsqueda
	 */
	private boolean coincideBusqueda(Usuario usuario, String busqueda) {
		if (TextoUtil.vacio(busqueda)) {
			return true;
		}
		return TextoUtil.contiene(usuario.getNombreCompleto(), busqueda)
				|| TextoUtil.contiene(usuario.getUsername(), busqueda)
				|| TextoUtil.contiene(usuario.getUnidadSolicitada(), busqueda);
	}

	/**
	 * Convierte un usuario en DTO con sus intereses y los nombres de su conjunto y
	 * su unidad.
	 *
	 * @param usuario Usuario a convertir
	 * @return DTO del usuario
	 */
	private UsuarioDTO aDTO(Usuario usuario) {
		UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
		dto.setPassword(null);
		dto.setIntereses(usuario.interesesComoLista());
		if (usuario.getConjuntoId() != null) {
			Optional<Conjunto> conjunto = conjuntoRepo.findById(usuario.getConjuntoId());
			dto.setConjuntoNombre(conjunto.isPresent() ? conjunto.get().getNombre() : null);
		}
		if (usuario.getUnidadId() != null) {
			Optional<Unidad> unidad = unidadRepo.findById(usuario.getUnidadId());
			dto.setUnidadDescripcion(unidad.isPresent() ? unidad.get().getDescripcion() : null);
		}
		return dto;
	}
}
