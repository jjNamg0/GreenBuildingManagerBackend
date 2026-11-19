package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.CambioPasswordDTO;
import co.edu.unbosque.GreenBuilding.dto.UsuarioDTO;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.service.UsuarioService;
import co.edu.unbosque.GreenBuilding.util.ExceptionChecker;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de usuarios. Los endpoints /getme, /updateme
 * y /changepassword son para cualquier usuario con sesión, /updateintereses es
 * para residentes (ROLE_USER) y todos los demás requieren ROLE_ADMIN.
 *
 * @author Daniel Montoya
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080" })
@Transactional
@Tag(name = "Gestión de Usuarios", description = "Perfil propio y administración de residentes")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

	/** Servicio para operaciones relacionadas con usuarios. */
	@Autowired
	private UsuarioService usuarioServ;

	/** Constructor por defecto. */
	public UsuarioController() {
	}

	/**
	 * Obtiene los datos del usuario con la sesión iniciada.
	 *
	 * @return ResponseEntity con los datos del usuario
	 */
	@Operation(summary = "Obtener mi usuario", description = """
				Este endpoint devuelve los datos del usuario que tiene la sesión iniciada.

				**¿Qué hace?** Incluye su conjunto, su unidad (si ya tiene) y sus intereses. Nunca devuelve la contraseña.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class), examples = @ExampleObject(value = """
						{
						  "id": 3,
						  "nombre": "Laura",
						  "apellido": "Gómez",
						  "username": "residente@greenbuilding.com",
						  "role": "USER",
						  "conjuntoNombre": "Reservas de la Sabana",
						  "unidadDescripcion": "Torre 1 · Apto 302",
						  "intereses": ["FIESTAS", "NINOS"]
						}
					"""))),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}"))) })
	@GetMapping("/getme")
	ResponseEntity<UsuarioDTO> getMe() {
		UsuarioDTO found = usuarioServ.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UsuarioDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Actualiza el perfil del usuario con la sesión iniciada.
	 *
	 * @param newUser DTO con el nombre, apellido y teléfono
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Actualizar mi perfil", description = """
				Este endpoint permite al usuario cambiar su nombre, apellido y teléfono.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Perfil actualizado", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Tu perfil se actualizó correctamente."))),
			@ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "El campo nombre es obligatorio."))) })
	@PutMapping(path = "/updateme", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateMe(
			@Parameter(description = "Nuevos datos del perfil", required = true, schema = @Schema(implementation = UsuarioDTO.class), examples = @ExampleObject(value = """
						{
						  "nombre": "Laura",
						  "apellido": "Gómez",
						  "telefono": "3001234567"
						}
					""")) @RequestBody UsuarioDTO newUser) {
		try {
			ExceptionChecker.checkEmpty("nombre", newUser.getNombre());
			ExceptionChecker.checkEmpty("apellido", newUser.getApellido());
			ExceptionChecker.checkEmpty("teléfono", newUser.getTelefono());
			ExceptionChecker.checkCharacters(newUser.getNombre());
			ExceptionChecker.checkCharacters(newUser.getApellido());
		} catch (EmptyFieldException | InvalidCharactersException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = usuarioServ.updateMe(SecurityContextHolder.getContext().getAuthentication().getName(), newUser);
		if (status == 0) {
			return new ResponseEntity<>("Tu perfil se actualizó correctamente.", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Cambia la contraseña del usuario con la sesión iniciada.
	 *
	 * @param data DTO con la contraseña actual y la nueva
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Cambiar mi contraseña", description = """
				Este endpoint permite cambiar la contraseña escribiendo la actual.

				**Nota:** la nueva contraseña debe cumplir el estándar de seguridad y ser distinta a la actual.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Contraseña cambiada", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Tu contraseña se cambió correctamente."))),
			@ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o nueva inválida", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "La contraseña actual no es correcta."))) })
	@PutMapping(path = "/changepassword", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> changePassword(
			@Parameter(description = "Contraseña actual y nueva", required = true, schema = @Schema(implementation = CambioPasswordDTO.class), examples = @ExampleObject(value = """
						{
						  "passwordActual": "Residente123*",
						  "passwordNueva": "NuevaClave1*"
						}
					""")) @RequestBody CambioPasswordDTO data) {
		try {
			ExceptionChecker.checkEmpty("contraseña actual", data.getPasswordActual());
			ExceptionChecker.checkPassword(data.getPasswordNueva());
		} catch (EmptyFieldException | PasswordNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = usuarioServ.changePassword(SecurityContextHolder.getContext().getAuthentication().getName(), data);
		if (status == 0) {
			return new ResponseEntity<>("Tu contraseña se cambió correctamente.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("La contraseña actual no es correcta.", HttpStatus.BAD_REQUEST);
		} else if (status == 2) {
			return new ResponseEntity<>("La nueva contraseña debe ser diferente a la actual.", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Guarda los intereses del residente con la sesión iniciada.
	 *
	 * @param data DTO con la lista de intereses
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Actualizar mis intereses", description = """
				Este endpoint guarda los intereses del residente, que se usan para las recomendaciones.

				**Valores válidos:** FIESTAS, NINOS, DEPORTE, CULTURA, ASAMBLEAS, PISCINA, GIMNASIO, BBQ, NATURALEZA, MASCOTAS.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Intereses guardados", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Guardamos tus intereses. Tus recomendaciones se actualizarán."))),
			@ApiResponse(responseCode = "400", description = "Interés inválido", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Algún interés no es válido."))) })
	@PutMapping(path = "/updateintereses", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateIntereses(
			@Parameter(description = "Lista de intereses", required = true, schema = @Schema(implementation = UsuarioDTO.class), examples = @ExampleObject(value = "{ \"intereses\": [\"FIESTAS\", \"NINOS\"] }")) @RequestBody UsuarioDTO data) {
		try {
			ExceptionChecker.checkSelected("Envía la lista de intereses.", data.getIntereses());
		} catch (OptionNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = usuarioServ.updateIntereses(SecurityContextHolder.getContext().getAuthentication().getName(),
				data.getIntereses());
		if (status == 0) {
			return new ResponseEntity<>("Guardamos tus intereses. Tus recomendaciones se actualizarán.",
					HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Algún interés no es válido.", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtiene los usuarios con un filtro y una búsqueda. Requiere rol ADMIN.
	 *
	 * @param filtro   TODOS, SIN_UNIDAD, RESIDENTES o INACTIVOS
	 * @param busqueda Texto a buscar
	 * @return ResponseEntity con la lista de usuarios
	 */
	@Operation(summary = "Obtener todos los usuarios", description = """
				Este endpoint permite obtener la lista de usuarios registrados.

				**Filtros:** TODOS, SIN_UNIDAD (esperan que les asignen unidad), RESIDENTES (con unidad) e INACTIVOS.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de usuarios recuperada correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class))),
			@ApiResponse(responseCode = "204", description = "No hay usuarios que cumplan el filtro", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))),
			@ApiResponse(responseCode = "400", description = "Filtro inválido", content = @Content(mediaType = "application/json")) })
	@GetMapping("/getall")
	ResponseEntity<List<UsuarioDTO>> getAll(
			@Parameter(description = "Filtro de la lista", example = "SIN_UNIDAD") @RequestParam(required = false) String filtro,
			@Parameter(description = "Texto a buscar", example = "laura") @RequestParam(required = false) String busqueda) {
		try {
			ExceptionChecker.checkOption(
					TextoUtil.vacio(filtro) || UsuarioService.FILTROS_VALIDOS.contains(filtro.trim().toUpperCase()));
		} catch (OptionNotValidException e) {
			return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
		}
		List<UsuarioDTO> users = usuarioServ.getAll(filtro, busqueda);
		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cuenta el número total de usuarios. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con el número de usuarios
	 */
	@Operation(summary = "Contar usuarios", description = """
				Este endpoint devuelve el número total de usuarios registrados.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Conteo exitoso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "5"))),
			@ApiResponse(responseCode = "204", description = "No hay usuarios", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0"))) })
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = usuarioServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Verifica si existe un usuario con el ID especificado. Requiere rol ADMIN.
	 *
	 * @param id ID del usuario a verificar
	 * @return ResponseEntity con true si existe, false si no
	 */
	@Operation(summary = "Verificar existencia de usuario", description = """
				Este endpoint permite verificar si existe un usuario con el ID especificado.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "true"))),
			@ApiResponse(responseCode = "204", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "false"))) })
	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(
			@Parameter(description = "ID del usuario a verificar", required = true, example = "1") @PathVariable Long id) {
		boolean found = usuarioServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * Obtiene un usuario por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID del usuario a buscar
	 * @return ResponseEntity con el usuario encontrado o un objeto vacío si no
	 *         existe
	 */
	@Operation(summary = "Obtener usuario por ID", description = """
				Este endpoint permite obtener la información de un usuario mediante su ID.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class))),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}"))) })
	@GetMapping("/getbyid/{id}")
	ResponseEntity<UsuarioDTO> getById(
			@Parameter(description = "ID del usuario a buscar", required = true, example = "1") @PathVariable Long id) {
		UsuarioDTO found = usuarioServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UsuarioDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Actualiza el nombre, apellido y teléfono de un usuario. Requiere rol ADMIN.
	 *
	 * @param id      ID del usuario a actualizar
	 * @param newUser DTO con los nuevos datos del usuario
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Actualizar usuario (JSON)", description = """
				Este endpoint permite al administrador corregir el nombre, apellido y teléfono de un usuario.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario actualizado correctamente", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Usuario actualizado exitosamente"))),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Usuario no encontrado"))) })
	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(
			@Parameter(description = "ID del usuario a actualizar", required = true, example = "1") @RequestParam Long id,
			@Parameter(description = "Nuevos datos del usuario", required = true, schema = @Schema(implementation = UsuarioDTO.class)) @RequestBody UsuarioDTO newUser) {
		try {
			ExceptionChecker.checkEmpty("nombre", newUser.getNombre());
			ExceptionChecker.checkEmpty("apellido", newUser.getApellido());
			ExceptionChecker.checkCharacters(newUser.getNombre());
			ExceptionChecker.checkCharacters(newUser.getApellido());
		} catch (EmptyFieldException | InvalidCharactersException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = usuarioServ.updateById(id, newUser);
		if (status == 0) {
			return new ResponseEntity<>("Usuario actualizado exitosamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Asigna una unidad a un residente. Requiere rol ADMIN.
	 *
	 * @param id       ID del usuario
	 * @param unidadId ID de la unidad
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Asignar unidad", description = """
				Este endpoint asigna una unidad (apartamento o casa) a un residente.

				**¿Qué hace?** El residente queda en el conjunto de la unidad y ya puede reservar e inscribirse a eventos.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Unidad asignada", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Unidad asignada exitosamente"))),
			@ApiResponse(responseCode = "404", description = "Usuario o unidad no encontrados", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Unidad no encontrada"))),
			@ApiResponse(responseCode = "403", description = "El usuario es administrador", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Las cuentas de administrador solo se gestionan desde el back."))) })
	@PutMapping("/asignarunidad")
	ResponseEntity<String> asignarUnidad(
			@Parameter(description = "ID del usuario", required = true, example = "5") @RequestParam Long id,
			@Parameter(description = "ID de la unidad", required = true, example = "2") @RequestParam Long unidadId) {
		int status = usuarioServ.asignarUnidad(id, unidadId);
		if (status == 0) {
			return new ResponseEntity<>("Unidad asignada exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("Unidad no encontrada", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Las cuentas de administrador solo se gestionan desde el back.",
					HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Activa o desactiva la cuenta de un residente. Requiere rol ADMIN.
	 *
	 * @param id     ID del usuario
	 * @param activo true para activar, false para desactivar
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Cambiar estado de usuario", description = """
				Este endpoint activa o desactiva la cuenta de un residente.

				**¿Qué hace?** Una cuenta desactivada no puede iniciar sesión y pierde el acceso aunque tenga un token vigente.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Estado cambiado", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "La cuenta quedó desactivada."))),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Usuario no encontrado"))),
			@ApiResponse(responseCode = "403", description = "El usuario es administrador", content = @Content(mediaType = "text/plain")) })
	@PutMapping("/cambiarestado")
	ResponseEntity<String> cambiarEstado(
			@Parameter(description = "ID del usuario", required = true, example = "5") @RequestParam Long id,
			@Parameter(description = "Nuevo estado", required = true, example = "false") @RequestParam boolean activo) {
		int status = usuarioServ.cambiarEstado(id, activo);
		if (status == 0 && activo) {
			return new ResponseEntity<>("La cuenta quedó activa.", HttpStatus.ACCEPTED);
		} else if (status == 0) {
			return new ResponseEntity<>("La cuenta quedó desactivada.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Las cuentas de administrador solo se gestionan desde el back.",
					HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Elimina un usuario por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID del usuario a eliminar
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Eliminar usuario por ID", description = """
				Este endpoint permite eliminar un residente mediante su ID.

				**¿Qué hace?** Elimina el usuario, sus reservas y su asistencia a eventos.

				**Advertencia:** Esta acción no se puede deshacer. Las cuentas de administrador no se pueden eliminar.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario eliminado correctamente", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Usuario eliminado exitosamente"))),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Error al eliminar"))),
			@ApiResponse(responseCode = "403", description = "El usuario es administrador", content = @Content(mediaType = "text/plain")) })
	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(
			@Parameter(description = "ID del usuario a eliminar", required = true, example = "1") @PathVariable Long id) {
		int status = usuarioServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Las cuentas de administrador solo se gestionan desde el back.",
					HttpStatus.FORBIDDEN);
		}
	}
}
