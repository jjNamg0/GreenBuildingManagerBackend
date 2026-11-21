package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.UnidadDTO;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.LengthNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.service.ConjuntoService;
import co.edu.unbosque.GreenBuilding.service.UnidadService;
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
 * Controlador REST para la gestión de unidades (apartamentos y casas). Todos los endpoints
 * requieren ROLE_ADMIN.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/unidad")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Gestión de Unidades", description = "Apartamentos y casas de cada conjunto")
@SecurityRequirement(name = "bearerAuth")
public class UnidadController {

	/** Servicio para operaciones relacionadas con unidades. */
	@Autowired private UnidadService unidadServ;

	/** Servicio para consultar si existe un conjunto. */
	@Autowired private ConjuntoService conjuntoServ;

	/** Constructor por defecto. */
	public UnidadController() {}

	/**
	 * Crea una unidad en un conjunto. Requiere rol ADMIN.
	 *
	 * @param newUnidad DTO con los datos de la unidad
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Crear unidad (JSON)",
			description =
					"""
			Este endpoint permite crear una unidad en un conjunto.

			**Posibles resultados:**

			* Unidad creada exitosamente
			* La unidad ya existe en el conjunto (409)
			* El conjunto no existe (404)

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "201",
						description = "Unidad creada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Unidad creada exitosamente"))),
				@ApiResponse(
						responseCode = "409",
						description = "Unidad repetida",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Esa unidad ya existe en el conjunto."))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> createNewWithJSON(
			@Parameter(
							description = "Datos de la nueva unidad",
							required = true,
							schema = @Schema(implementation = UnidadDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "conjuntoId": 1,
					  "tipo": "APARTAMENTO",
					  "agrupacion": "Torre 2",
					  "numero": "301"
					}
				"""))
					@RequestBody
					UnidadDTO newUnidad) {
		String error = validar(newUnidad);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = unidadServ.create(newUnidad);
		if (status == 0) {
			return new ResponseEntity<>("Unidad creada exitosamente", HttpStatus.CREATED);
		} else if (status == 1) {
			return new ResponseEntity<>("Esa unidad ya existe en el conjunto.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtiene todas las unidades. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con la lista de unidades
	 */
	@Operation(
			summary = "Obtener todas las unidades",
			description =
					"""
			Este endpoint devuelve todas las unidades con sus residentes.

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
										schema = @Schema(implementation = UnidadDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay unidades",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getall")
	ResponseEntity<List<UnidadDTO>> getAll() {
		List<UnidadDTO> unidades = unidadServ.getAll();
		if (unidades.isEmpty()) {
			return new ResponseEntity<>(unidades, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(unidades, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene las unidades de un conjunto. Requiere rol ADMIN.
	 *
	 * @param conjuntoId ID del conjunto
	 * @return ResponseEntity con la lista de unidades
	 */
	@Operation(
			summary = "Obtener unidades de un conjunto",
			description =
					"""
			Este endpoint devuelve las unidades de un conjunto con los nombres de sus residentes.

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
										examples =
												@ExampleObject(
														value =
																"""
						[
						  {
						    "id": 2,
						    "conjuntoId": 1,
						    "tipo": "APARTAMENTO",
						    "agrupacion": "Torre 1",
						    "numero": "302",
						    "descripcion": "Torre 1 · Apto 302",
						    "residentes": ["Laura Gómez"]
						  }
						]
					"""))),
				@ApiResponse(
						responseCode = "204",
						description = "El conjunto no tiene unidades",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getbyconjunto/{conjuntoId}")
	ResponseEntity<List<UnidadDTO>> getByConjunto(
			@Parameter(description = "ID del conjunto", required = true, example = "1") @PathVariable
					Long conjuntoId) {
		if (!conjuntoServ.exist(conjuntoId)) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		}
		List<UnidadDTO> unidades = unidadServ.getByConjunto(conjuntoId);
		if (unidades.isEmpty()) {
			return new ResponseEntity<>(unidades, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(unidades, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cuenta el número total de unidades. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con el número de unidades
	 */
	@Operation(
			summary = "Contar unidades",
			description =
					"""
			Este endpoint devuelve el número total de unidades.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conteo exitoso",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "7"))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay unidades",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0")))
			})
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = unidadServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Verifica si existe una unidad con el ID especificado. Requiere rol ADMIN.
	 *
	 * @param id ID de la unidad
	 * @return ResponseEntity con true si existe, false si no
	 */
	@Operation(
			summary = "Verificar existencia de unidad",
			description =
					"""
			Este endpoint permite verificar si existe una unidad con el ID especificado.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Unidad encontrada",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "true"))),
				@ApiResponse(
						responseCode = "204",
						description = "Unidad no encontrada",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "false")))
			})
	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(
			@Parameter(description = "ID de la unidad", required = true, example = "1") @PathVariable
					Long id) {
		boolean found = unidadServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * Obtiene una unidad por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID de la unidad
	 * @return ResponseEntity con la unidad o un objeto vacío si no existe
	 */
	@Operation(
			summary = "Obtener unidad por ID",
			description =
					"""
			Este endpoint devuelve una unidad con sus residentes.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Unidad encontrada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = UnidadDTO.class))),
				@ApiResponse(
						responseCode = "404",
						description = "Unidad no encontrada",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}")))
			})
	@GetMapping("/getbyid/{id}")
	ResponseEntity<UnidadDTO> getById(
			@Parameter(description = "ID de la unidad", required = true, example = "1") @PathVariable
					Long id) {
		UnidadDTO found = unidadServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UnidadDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Actualiza una unidad existente. Requiere rol ADMIN.
	 *
	 * @param id ID de la unidad
	 * @param newUnidad DTO con los nuevos datos
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Actualizar unidad (JSON)",
			description =
					"""
			Este endpoint permite actualizar una unidad mediante su ID.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Unidad actualizada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Unidad actualizada exitosamente"))),
				@ApiResponse(
						responseCode = "409",
						description = "Unidad repetida",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "404",
						description = "Unidad o conjunto no encontrados",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(
			@Parameter(description = "ID de la unidad a actualizar", required = true, example = "1")
					@RequestParam
					Long id,
			@Parameter(
							description = "Nuevos datos de la unidad",
							required = true,
							schema = @Schema(implementation = UnidadDTO.class))
					@RequestBody
					UnidadDTO newUnidad) {
		String error = validar(newUnidad);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = unidadServ.updateById(id, newUnidad);
		if (status == 0) {
			return new ResponseEntity<>("Unidad actualizada exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Esa unidad ya existe en el conjunto.", HttpStatus.CONFLICT);
		} else if (status == 2) {
			return new ResponseEntity<>("Unidad no encontrada", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Elimina una unidad por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID de la unidad
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Eliminar unidad por ID",
			description =
					"""
			Este endpoint elimina una unidad que no tenga residentes.

			**Advertencia:** Esta acción no se puede deshacer.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Unidad eliminada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Unidad eliminada exitosamente"))),
				@ApiResponse(
						responseCode = "404",
						description = "Unidad no encontrada",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Error al eliminar"))),
				@ApiResponse(
						responseCode = "409",
						description = "La unidad tiene residentes",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(
														value = "La unidad tiene residentes. Reasígnalos antes de eliminarla.")))
			})
	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(
			@Parameter(description = "ID de la unidad a eliminar", required = true, example = "1")
					@PathVariable
					Long id) {
		int status = unidadServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Unidad eliminada exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					"La unidad tiene residentes. Reasígnalos antes de eliminarla.", HttpStatus.CONFLICT);
		}
	}

	/**
	 * Revisa los datos obligatorios de una unidad.
	 *
	 * @param data DTO a revisar
	 * @return Mensaje de error, o null si los datos son válidos
	 */
	private String validar(UnidadDTO data) {
		try {
			ExceptionChecker.checkEmpty("agrupación", data.getAgrupacion());
			ExceptionChecker.checkEmpty("número", data.getNumero());
			ExceptionChecker.checkCharacters(data.getAgrupacion());
			ExceptionChecker.checkCharacters(data.getNumero());
			ExceptionChecker.checkLength("agrupación", data.getAgrupacion(), ExceptionChecker.MAX_TEXTO);
			ExceptionChecker.checkLength("número", data.getNumero(), ExceptionChecker.MAX_TEXTO);
			ExceptionChecker.checkSelected("Elige el conjunto.", data.getConjuntoId());
			ExceptionChecker.checkSelected("Elige el tipo de unidad.", data.getTipo());
		} catch (EmptyFieldException
				| InvalidCharactersException
				| LengthNotValidException
				| OptionNotValidException e) {
			return e.getMessage();
		}
		return null;
	}
}
