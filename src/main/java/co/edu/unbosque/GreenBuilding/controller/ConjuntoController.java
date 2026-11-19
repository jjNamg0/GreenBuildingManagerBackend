package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.ConjuntoDTO;
import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.service.ConjuntoService;
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
 * Controlador REST para la gestión de conjuntos residenciales. El endpoint /getall es público
 * (mapa y registro), /getbyid, /count y /exists requieren sesión, y crear, actualizar y eliminar
 * requieren ROLE_ADMIN.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/conjunto")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Gestión de Conjuntos", description = "Conjuntos residenciales de Cundinamarca")
@SecurityRequirement(name = "bearerAuth")
public class ConjuntoController {

	/** Servicio para operaciones relacionadas con conjuntos. */
	@Autowired private ConjuntoService conjuntoServ;

	/** Constructor por defecto. */
	public ConjuntoController() {}

	/**
	 * Crea un nuevo conjunto. Requiere rol ADMIN.
	 *
	 * @param newConjunto DTO con los datos del conjunto
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Crear conjunto (JSON)",
			description =
					"""
			Este endpoint permite crear un conjunto residencial.

			**¿Qué hace?** Guarda el conjunto con su ubicación, que debe estar dentro de Cundinamarca.

			**Posibles resultados:**

			* Conjunto creado exitosamente
			* Ya existe un conjunto con ese nombre en el municipio (409)
			* Ubicación fuera de Cundinamarca o datos inválidos (400)

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "201",
						description = "Conjunto creado exitosamente",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Conjunto creado exitosamente"))),
				@ApiResponse(
						responseCode = "409",
						description = "Conjunto repetido",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(value = "Ya existe un conjunto con ese nombre en el municipio."))),
				@ApiResponse(
						responseCode = "400",
						description = "Datos inválidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "La ubicación debe estar dentro de Cundinamarca.")))
			})
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> createNewWithJSON(
			@Parameter(
							description = "Datos del nuevo conjunto",
							required = true,
							schema = @Schema(implementation = ConjuntoDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "nombre": "Reservas de la Sabana",
					  "tipo": "APARTAMENTOS",
					  "direccion": "Calle 9 # 11-40",
					  "municipio": "Chía",
					  "latitud": 4.8617,
					  "longitud": -74.0581,
					  "descripcion": "Conjunto con piscina y salón social",
					  "imagenUrl": null
					}
				"""))
					@RequestBody
					ConjuntoDTO newConjunto) {
		String error = validar(newConjunto);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = conjuntoServ.create(newConjunto);
		if (status == 0) {
			return new ResponseEntity<>("Conjunto creado exitosamente", HttpStatus.CREATED);
		} else if (status == 1) {
			return new ResponseEntity<>(
					"Ya existe un conjunto con ese nombre en el municipio.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>(
					"La ubicación debe estar dentro de Cundinamarca.", HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Obtiene los conjuntos con una búsqueda y un tipo. Es público.
	 *
	 * @param busqueda Texto a buscar en el nombre, municipio o dirección
	 * @param tipo APARTAMENTOS o CASAS
	 * @return ResponseEntity con la lista de conjuntos
	 */
	@Operation(
			summary = "Obtener todos los conjuntos",
			description =
					"""
			Este endpoint devuelve los conjuntos registrados con el total de unidades y espacios.

			**¿Qué hace?** Filtra por texto (sin importar tildes) y por tipo. Lo usan el mapa y el registro.

			**Nota:** Este endpoint es público.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Lista de conjuntos recuperada correctamente",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = ConjuntoDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay conjuntos",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getall")
	ResponseEntity<List<ConjuntoDTO>> getAll(
			@Parameter(description = "Texto a buscar", example = "chia") @RequestParam(required = false)
					String busqueda,
			@Parameter(description = "Tipo de conjunto", example = "APARTAMENTOS")
					@RequestParam(required = false)
					String tipo) {
		List<ConjuntoDTO> conjuntos = conjuntoServ.getAll(busqueda, tipo);
		if (conjuntos.isEmpty()) {
			return new ResponseEntity<>(conjuntos, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(conjuntos, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cuenta el número total de conjuntos.
	 *
	 * @return ResponseEntity con el número de conjuntos
	 */
	@Operation(
			summary = "Contar conjuntos",
			description =
					"""
			Este endpoint devuelve el número total de conjuntos.

			**Nota:** Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conteo exitoso",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "4"))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay conjuntos",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0")))
			})
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = conjuntoServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Verifica si existe un conjunto con el ID especificado.
	 *
	 * @param id ID del conjunto
	 * @return ResponseEntity con true si existe, false si no
	 */
	@Operation(
			summary = "Verificar existencia de conjunto",
			description =
					"""
			Este endpoint permite verificar si existe un conjunto con el ID especificado.

			**Nota:** Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conjunto encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "true"))),
				@ApiResponse(
						responseCode = "204",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "false")))
			})
	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(
			@Parameter(description = "ID del conjunto", required = true, example = "1") @PathVariable
					Long id) {
		boolean found = conjuntoServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * Obtiene un conjunto por su ID.
	 *
	 * @param id ID del conjunto
	 * @return ResponseEntity con el conjunto o un objeto vacío si no existe
	 */
	@Operation(
			summary = "Obtener conjunto por ID",
			description =
					"""
			Este endpoint devuelve un conjunto con el total de unidades y espacios.

			**Nota:** Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conjunto encontrado",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = ConjuntoDTO.class))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}")))
			})
	@GetMapping("/getbyid/{id}")
	ResponseEntity<ConjuntoDTO> getById(
			@Parameter(description = "ID del conjunto", required = true, example = "1") @PathVariable
					Long id) {
		ConjuntoDTO found = conjuntoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new ConjuntoDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Actualiza un conjunto existente. Requiere rol ADMIN.
	 *
	 * @param id ID del conjunto
	 * @param newConjunto DTO con los nuevos datos
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Actualizar conjunto (JSON)",
			description =
					"""
			Este endpoint permite actualizar un conjunto mediante su ID.

			**Posibles resultados:**

			* Conjunto actualizado exitosamente
			* Ya existe otro conjunto con ese nombre en el municipio (409)
			* Conjunto no encontrado (404)
			* Ubicación fuera de Cundinamarca o datos inválidos (400)

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conjunto actualizado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Conjunto actualizado exitosamente"))),
				@ApiResponse(
						responseCode = "409",
						description = "Conjunto repetido",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content =
								@Content(mediaType = "text/plain", examples = @ExampleObject(value = "Conjunto no encontrado")))
			})
	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(
			@Parameter(description = "ID del conjunto a actualizar", required = true, example = "1")
					@RequestParam
					Long id,
			@Parameter(
							description = "Nuevos datos del conjunto",
							required = true,
							schema = @Schema(implementation = ConjuntoDTO.class))
					@RequestBody
					ConjuntoDTO newConjunto) {
		String error = validar(newConjunto);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = conjuntoServ.updateById(id, newConjunto);
		if (status == 0) {
			return new ResponseEntity<>("Conjunto actualizado exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>(
					"Ya existe un conjunto con ese nombre en el municipio.", HttpStatus.CONFLICT);
		} else if (status == 2) {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					"La ubicación debe estar dentro de Cundinamarca.", HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Elimina un conjunto por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID del conjunto
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Eliminar conjunto por ID",
			description =
					"""
			Este endpoint elimina un conjunto con sus unidades, espacios, eventos y reservas.

			**Advertencia:** Esta acción no se puede deshacer. Los usuarios del conjunto quedan sin conjunto ni unidad.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conjunto eliminado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Conjunto eliminado exitosamente"))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Error al eliminar")))
			})
	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(
			@Parameter(description = "ID del conjunto a eliminar", required = true, example = "1")
					@PathVariable
					Long id) {
		int status = conjuntoServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Conjunto eliminado exitosamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Revisa los datos obligatorios de un conjunto.
	 *
	 * @param data DTO a revisar
	 * @return Mensaje de error, o null si los datos son válidos
	 */
	private String validar(ConjuntoDTO data) {
		try {
			ExceptionChecker.checkEmpty("nombre", data.getNombre());
			ExceptionChecker.checkEmpty("dirección", data.getDireccion());
			ExceptionChecker.checkEmpty("municipio", data.getMunicipio());
			ExceptionChecker.checkCharacters(data.getNombre());
			ExceptionChecker.checkCharacters(data.getDireccion());
			ExceptionChecker.checkCharacters(data.getMunicipio());
			ExceptionChecker.checkCharacters(data.getDescripcion());
			ExceptionChecker.checkSelected("Elige el tipo de conjunto.", data.getTipo());
			ExceptionChecker.checkLocation(data.getLatitud(), data.getLongitud());
		} catch (EmptyFieldException
				| InvalidCharactersException
				| OptionNotValidException
				| CoordinateNotValidException e) {
			return e.getMessage();
		}
		return null;
	}
}
