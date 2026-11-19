package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.EspacioDTO;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import co.edu.unbosque.GreenBuilding.service.EspacioService;
import co.edu.unbosque.GreenBuilding.util.ExceptionChecker;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de espacios comunes. Los residentes solo ven los espacios de su
 * conjunto; crear, actualizar y eliminar requieren ROLE_ADMIN.
 *
 * @author Daniel Montoya
 * @version 1.0
 */
@RestController
@RequestMapping("/espacio")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Gestión de Espacios", description = "Zonas comunes de cada conjunto")
@SecurityRequirement(name = "bearerAuth")
public class EspacioController {

	/** Mensaje cuando un residente pide información de otro conjunto. */
	private static final String SIN_ACCESO = "Solo puedes ver la información de tu conjunto.";

	/** Servicio para operaciones relacionadas con espacios. */
	@Autowired private EspacioService espacioServ;

	/** Constructor por defecto. */
	public EspacioController() {}

	/**
	 * Crea un espacio común en un conjunto. Requiere rol ADMIN.
	 *
	 * @param newEspacio DTO con los datos del espacio
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Crear espacio (JSON)",
			description =
					"""
			Este endpoint permite crear un espacio común (salón, piscina, BBQ, etc.).

			**Nota:** las horas van con formato HH:mm y la apertura debe ser antes del cierre.
			Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "201",
						description = "Espacio creado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Espacio creado exitosamente"))),
				@ApiResponse(
						responseCode = "400",
						description = "Datos inválidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(value = "La hora de inicio debe ser antes de la hora de fin."))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> createNewWithJSON(
			@Parameter(
							description = "Datos del nuevo espacio",
							required = true,
							schema = @Schema(implementation = EspacioDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "conjuntoId": 1,
					  "nombre": "Salón social",
					  "tipo": "SALON_SOCIAL",
					  "descripcion": "Salón con cocina y sonido",
					  "capacidad": 60,
					  "horaApertura": "08:00",
					  "horaCierre": "22:00",
					  "requiereReserva": true,
					  "disponible": true,
					  "imagenUrl": null
					}
				"""))
					@RequestBody
					EspacioDTO newEspacio) {
		String error = validar(newEspacio);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = espacioServ.create(newEspacio);
		if (status == 0) {
			return new ResponseEntity<>("Espacio creado exitosamente", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtiene los espacios de un conjunto.
	 *
	 * @param conjuntoId ID del conjunto
	 * @return ResponseEntity con la lista de espacios
	 */
	@Operation(
			summary = "Obtener espacios de un conjunto",
			description =
					"""
			Este endpoint devuelve los espacios comunes de un conjunto ordenados por nombre.

			**Nota:** los residentes solo pueden ver los espacios de su propio conjunto.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Lista recuperada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = EspacioDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "El conjunto no tiene espacios",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))),
				@ApiResponse(
						responseCode = "403",
						description = "Conjunto de otro residente",
						content = @Content(mediaType = "application/json")),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "application/json"))
			})
	@GetMapping("/getbyconjunto/{conjuntoId}")
	ResponseEntity<List<EspacioDTO>> getByConjunto(
			@Parameter(description = "ID del conjunto", required = true, example = "1") @PathVariable
					Long conjuntoId) {
		if (!espacioServ.puedeVerConjunto(SecurityContextHolder.getContext().getAuthentication().getName(), conjuntoId)) {
			return new ResponseEntity<>(List.of(), HttpStatus.FORBIDDEN);
		}
		List<EspacioDTO> espacios = espacioServ.getByConjunto(conjuntoId);
		if (espacios == null) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		} else if (espacios.isEmpty()) {
			return new ResponseEntity<>(espacios, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(espacios, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene todos los espacios.
	 *
	 * @return ResponseEntity con la lista de espacios
	 */
	@Operation(
			summary = "Obtener todos los espacios",
			description =
					"""
			Este endpoint devuelve todos los espacios de todos los conjuntos.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Lista recuperada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = EspacioDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay espacios",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getall")
	ResponseEntity<List<EspacioDTO>> getAll() {
		List<EspacioDTO> espacios = espacioServ.getAll();
		if (espacios.isEmpty()) {
			return new ResponseEntity<>(espacios, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(espacios, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cuenta el número total de espacios.
	 *
	 * @return ResponseEntity con el número de espacios
	 */
	@Operation(
			summary = "Contar espacios",
			description =
					"""
			Este endpoint devuelve el número total de espacios.

			**Nota:** Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conteo exitoso",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "5"))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay espacios",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0")))
			})
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = espacioServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Verifica si existe un espacio con el ID especificado.
	 *
	 * @param id ID del espacio
	 * @return ResponseEntity con true si existe, false si no
	 */
	@Operation(
			summary = "Verificar existencia de espacio",
			description =
					"""
			Este endpoint permite verificar si existe un espacio con el ID especificado.

			**Nota:** Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Espacio encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "true"))),
				@ApiResponse(
						responseCode = "204",
						description = "Espacio no encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "false")))
			})
	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(
			@Parameter(description = "ID del espacio", required = true, example = "1") @PathVariable
					Long id) {
		boolean found = espacioServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * Obtiene un espacio por su ID.
	 *
	 * @param id ID del espacio
	 * @return ResponseEntity con el espacio o un objeto vacío si no existe
	 */
	@Operation(
			summary = "Obtener espacio por ID",
			description =
					"""
			Este endpoint devuelve un espacio común.

			**Nota:** los residentes solo pueden ver los espacios de su propio conjunto.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Espacio encontrado",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = EspacioDTO.class))),
				@ApiResponse(
						responseCode = "403",
						description = "Espacio de otro conjunto",
						content = @Content(mediaType = "application/json")),
				@ApiResponse(
						responseCode = "404",
						description = "Espacio no encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}")))
			})
	@GetMapping("/getbyid/{id}")
	ResponseEntity<EspacioDTO> getById(
			@Parameter(description = "ID del espacio", required = true, example = "1") @PathVariable
					Long id) {
		EspacioDTO found = espacioServ.getById(id);
		if (found == null) {
			return new ResponseEntity<>(new EspacioDTO(), HttpStatus.NOT_FOUND);
		} else if (!espacioServ.puedeVerConjunto(SecurityContextHolder.getContext().getAuthentication().getName(), found.getConjuntoId())) {
			return new ResponseEntity<>(new EspacioDTO(), HttpStatus.FORBIDDEN);
		} else {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Actualiza un espacio existente. Requiere rol ADMIN.
	 *
	 * @param id ID del espacio
	 * @param newEspacio DTO con los nuevos datos
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Actualizar espacio (JSON)",
			description =
					"""
			Este endpoint permite actualizar un espacio mediante su ID.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Espacio actualizado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Espacio actualizado exitosamente"))),
				@ApiResponse(
						responseCode = "404",
						description = "Espacio o conjunto no encontrados",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(
			@Parameter(description = "ID del espacio a actualizar", required = true, example = "1")
					@RequestParam
					Long id,
			@Parameter(
							description = "Nuevos datos del espacio",
							required = true,
							schema = @Schema(implementation = EspacioDTO.class))
					@RequestBody
					EspacioDTO newEspacio) {
		String error = validar(newEspacio);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = espacioServ.updateById(id, newEspacio);
		if (status == 0) {
			return new ResponseEntity<>("Espacio actualizado exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 2) {
			return new ResponseEntity<>("Espacio no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Elimina un espacio por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID del espacio
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Eliminar espacio por ID",
			description =
					"""
			Este endpoint elimina un espacio y sus reservas. Los eventos que lo usaban quedan sin espacio.

			**Advertencia:** Esta acción no se puede deshacer.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Espacio eliminado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Espacio eliminado exitosamente"))),
				@ApiResponse(
						responseCode = "404",
						description = "Espacio no encontrado",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Error al eliminar")))
			})
	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(
			@Parameter(description = "ID del espacio a eliminar", required = true, example = "1")
					@PathVariable
					Long id) {
		int status = espacioServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Espacio eliminado exitosamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Revisa los datos obligatorios de un espacio.
	 *
	 * @param data DTO a revisar
	 * @return Mensaje de error, o null si los datos son válidos
	 */
	private String validar(EspacioDTO data) {
		try {
			ExceptionChecker.checkEmpty("nombre", data.getNombre());
			ExceptionChecker.checkCharacters(data.getNombre());
			ExceptionChecker.checkCharacters(data.getDescripcion());
			ExceptionChecker.checkPositive("capacidad", data.getCapacidad());
			ExceptionChecker.checkTimeRange(data.getHoraApertura(), data.getHoraCierre());
			ExceptionChecker.checkSelected("Elige el conjunto.", data.getConjuntoId());
			ExceptionChecker.checkSelected("Elige el tipo de espacio.", data.getTipo());
		} catch (EmptyFieldException
				| InvalidCharactersException
				| NumberNotValidException
				| TimeNotValidException
				| OptionNotValidException e) {
			return e.getMessage();
		}
		return null;
	}
}
