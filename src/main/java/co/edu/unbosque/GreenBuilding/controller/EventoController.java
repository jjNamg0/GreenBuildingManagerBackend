package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.CancelacionDTO;
import co.edu.unbosque.GreenBuilding.dto.EventoDTO;
import co.edu.unbosque.GreenBuilding.exception.DateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import co.edu.unbosque.GreenBuilding.model.Evento.Tipo;
import co.edu.unbosque.GreenBuilding.service.EspacioService;
import co.edu.unbosque.GreenBuilding.service.EventoService;
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
 * Controlador REST para los eventos de los conjuntos. Los residentes consultan los eventos de su
 * conjunto y confirman asistencia; los administradores crean, editan, cancelan, eliminan y envían
 * invitaciones por correo.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/evento")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Gestión de Eventos", description = "Eventos, asistencia e invitaciones por correo")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

	/** Servicio para operaciones relacionadas con eventos. */
	@Autowired private EventoService eventoServ;

	/** Servicio de espacios, para revisar el acceso al conjunto. */
	@Autowired private EspacioService espacioServ;

	/** Constructor por defecto. */
	public EventoController() {}

	/**
	 * Crea un evento. Requiere rol ADMIN.
	 *
	 * @param newEvento DTO con los datos del evento
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Crear evento (JSON)",
			description =
					"""
			Este endpoint permite programar un evento en un conjunto.

			**Tipos:** FIESTA, INFANTIL, DEPORTIVO, CULTURAL, ASAMBLEA, OTRO.
			**Plantillas de invitación:** CLASICO, ELEGANTE, FIESTA, INFANTIL, DEPORTIVO, ASAMBLEA.

			**Nota:** la fecha debe ser futura y el espacio (opcional) debe ser del mismo conjunto.
			Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "201",
						description = "Evento creado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Evento creado exitosamente"))),
				@ApiResponse(
						responseCode = "400",
						description = "Datos inválidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "La fecha y hora del evento deben ser futuras."))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> createNewWithJSON(
			@Parameter(
							description = "Datos del nuevo evento",
							required = true,
							schema = @Schema(implementation = EventoDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "conjuntoId": 1,
					  "espacioId": 1,
					  "titulo": "Fiesta de fin de año",
					  "descripcion": "Música y comida",
					  "tipo": "FIESTA",
					  "fecha": "2026-12-20",
					  "horaInicio": "19:00",
					  "horaFin": "23:00",
					  "paraNinos": false,
					  "cupoMaximo": 60,
					  "plantilla": "FIESTA"
					}
				"""))
					@RequestBody
					EventoDTO newEvento) {
		String error = validar(newEvento);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = eventoServ.create(newEvento);
		if (status == 0) {
			return new ResponseEntity<>("Evento creado exitosamente", HttpStatus.CREATED);
		} else {
			return respuestaDatos(status);
		}
	}

	/**
	 * Obtiene los eventos de un conjunto de hoy en adelante, con filtros.
	 *
	 * @param id ID del conjunto
	 * @param tipo Tipo de evento
	 * @param paraNinos true para ver solo planes para niños
	 * @return ResponseEntity con la lista de eventos
	 */
	@Operation(
			summary = "Obtener eventos de un conjunto",
			description =
					"""
			Este endpoint devuelve los eventos del conjunto de hoy en adelante, indicando si el usuario asistirá.

			**Nota:** los residentes solo pueden ver los eventos de su propio conjunto.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Lista recuperada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = EventoDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay eventos",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))),
				@ApiResponse(
						responseCode = "403",
						description = "Conjunto de otro residente",
						content = @Content(mediaType = "application/json"))
			})
	@GetMapping("/getbyconjunto/{id}")
	ResponseEntity<List<EventoDTO>> getByConjunto(
			@Parameter(description = "ID del conjunto", required = true, example = "1") @PathVariable
					Long id,
			@Parameter(description = "Tipo de evento", example = "FIESTA") @RequestParam(required = false)
					Tipo tipo,
			@Parameter(description = "Solo planes para niños", example = "false")
					@RequestParam(defaultValue = "false")
					boolean paraNinos) {
		if (!espacioServ.puedeVerConjunto(SecurityContextHolder.getContext().getAuthentication().getName(), id)) {
			return new ResponseEntity<>(List.of(), HttpStatus.FORBIDDEN);
		}
		List<EventoDTO> eventos = eventoServ.getByConjunto(SecurityContextHolder.getContext().getAuthentication().getName(), id, tipo, paraNinos);
		if (eventos == null) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		} else if (eventos.isEmpty()) {
			return new ResponseEntity<>(eventos, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(eventos, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene todos los eventos, o los de un conjunto. Requiere rol ADMIN.
	 *
	 * @param conjuntoId ID del conjunto
	 * @return ResponseEntity con la lista de eventos
	 */
	@Operation(
			summary = "Obtener todos los eventos",
			description =
					"""
			Este endpoint devuelve los eventos de todos los conjuntos, o de uno si se envía conjuntoId.

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
										schema = @Schema(implementation = EventoDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay eventos",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getall")
	ResponseEntity<List<EventoDTO>> getAll(
			@Parameter(description = "ID del conjunto", example = "1") @RequestParam(required = false)
					Long conjuntoId) {
		List<EventoDTO> eventos = eventoServ.getAll(conjuntoId);
		if (eventos == null) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		} else if (eventos.isEmpty()) {
			return new ResponseEntity<>(eventos, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(eventos, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cuenta el número total de eventos. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con el número de eventos
	 */
	@Operation(
			summary = "Contar eventos",
			description =
					"""
			Este endpoint devuelve el número total de eventos.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conteo exitoso",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "3"))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay eventos",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0")))
			})
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = eventoServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene un evento por su ID.
	 *
	 * @param id ID del evento
	 * @return ResponseEntity con el evento o un objeto vacío si no existe
	 */
	@Operation(
			summary = "Obtener evento por ID",
			description =
					"""
			Este endpoint devuelve un evento.

			**Nota:** Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Evento encontrado",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = EventoDTO.class))),
				@ApiResponse(
						responseCode = "404",
						description = "Evento no encontrado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}")))
			})
	@GetMapping("/getbyid/{id}")
	ResponseEntity<EventoDTO> getById(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable
					Long id) {
		EventoDTO found = eventoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new EventoDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Actualiza un evento programado. Requiere rol ADMIN.
	 *
	 * @param id ID del evento
	 * @param newEvento DTO con los nuevos datos
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Actualizar evento (JSON)",
			description =
					"""
			Este endpoint permite editar un evento programado.

			**Nota:** el cupo no puede quedar por debajo de los inscritos.
			Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Evento actualizado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Evento actualizado exitosamente"))),
				@ApiResponse(
						responseCode = "404",
						description = "Evento no encontrado",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "409",
						description = "El evento fue cancelado o ya finalizó",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(
			@Parameter(description = "ID del evento a actualizar", required = true, example = "1")
					@RequestParam
					Long id,
			@Parameter(
							description = "Nuevos datos del evento",
							required = true,
							schema = @Schema(implementation = EventoDTO.class))
					@RequestBody
					EventoDTO newEvento) {
		String error = validar(newEvento);
		if (error != null) {
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		int status = eventoServ.updateById(id, newEvento);
		if (status == 0) {
			return new ResponseEntity<>("Evento actualizado exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 4) {
			return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 5) {
			return new ResponseEntity<>("El evento ya fue cancelado o finalizó.", HttpStatus.CONFLICT);
		} else if (status == 6) {
			return new ResponseEntity<>(
					"El cupo no puede ser menor que los asistentes inscritos.", HttpStatus.BAD_REQUEST);
		} else {
			return respuestaDatos(status);
		}
	}

	/**
	 * Cancela un evento programado. Requiere rol ADMIN.
	 *
	 * @param id ID del evento
	 * @param data DTO con el motivo
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Cancelar evento",
			description =
					"""
			Este endpoint cancela un evento programado y avisa por correo a los inscritos.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Evento cancelado",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(value = "Evento cancelado. Avisamos a los inscritos y a los invitados."))),
				@ApiResponse(
						responseCode = "409",
						description = "Ya estaba cancelado o finalizó",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping(path = "/cancelar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> cancelar(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable Long id,
			@Parameter(
							description = "Motivo de la cancelación",
							required = true,
							schema = @Schema(implementation = CancelacionDTO.class),
							examples = @ExampleObject(value = "{ \"motivo\": \"Lluvia\" }"))
					@RequestBody
					CancelacionDTO data) {
		try {
			ExceptionChecker.checkEmpty("motivo", data.getMotivo());
			ExceptionChecker.checkCharacters(data.getMotivo());
		} catch (EmptyFieldException | InvalidCharactersException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = eventoServ.cancelar(id, data.getMotivo());
		if (status == 0) {
			return new ResponseEntity<>(
					"Evento cancelado. Avisamos a los inscritos y a los invitados.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("El evento ya fue cancelado o finalizó.", HttpStatus.CONFLICT);
		}
	}

	/**
	 * Elimina un evento por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID del evento
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Eliminar evento por ID",
			description =
					"""
			Este endpoint elimina un evento y la asistencia de sus residentes.

			**Advertencia:** Esta acción no se puede deshacer.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Evento eliminado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Evento eliminado exitosamente"))),
				@ApiResponse(
						responseCode = "404",
						description = "Evento no encontrado",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Error al eliminar")))
			})
	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(
			@Parameter(description = "ID del evento a eliminar", required = true, example = "1")
					@PathVariable
					Long id) {
		int status = eventoServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Evento eliminado exitosamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Inscribe al residente con la sesión iniciada en un evento. Requiere rol USER.
	 *
	 * @param id ID del evento
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Asistir a un evento",
			description =
					"""
			Este endpoint inscribe al residente en un evento programado de su conjunto.

			**Nota:** Este endpoint requiere autenticación y rol USER.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Inscripción exitosa",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "¡Listo! Quedaste inscrito en el evento."))),
				@ApiResponse(
						responseCode = "409",
						description = "Evento cancelado, finalizado o lleno",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "403",
						description = "No es residente del conjunto",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping("/asistir/{id}")
	ResponseEntity<String> asistir(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable
					Long id) {
		int status = eventoServ.asistir(SecurityContextHolder.getContext().getAuthentication().getName(), id);
		if (status == 0) {
			return new ResponseEntity<>("¡Listo! Quedaste inscrito en el evento.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("El evento ya fue cancelado o finalizó.", HttpStatus.CONFLICT);
		} else if (status == 3) {
			return new ResponseEntity<>(
					"Solo los residentes del conjunto pueden inscribirse a este evento.", HttpStatus.FORBIDDEN);
		} else if (status == 4) {
			return new ResponseEntity<>("El evento ya completó su cupo.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>("Ya estabas inscrito en este evento.", HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cancela la asistencia del residente con la sesión iniciada. Requiere rol USER.
	 *
	 * @param id ID del evento
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "No asistir a un evento",
			description =
					"""
			Este endpoint cancela la inscripción del residente en un evento.

			**Nota:** Este endpoint requiere autenticación y rol USER.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Asistencia cancelada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Cancelaste tu asistencia al evento."))),
				@ApiResponse(
						responseCode = "409",
						description = "Evento cancelado o finalizado",
						content = @Content(mediaType = "text/plain"))
			})
	@DeleteMapping("/noasistir/{id}")
	ResponseEntity<String> noAsistir(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable
					Long id) {
		int status = eventoServ.noAsistir(SecurityContextHolder.getContext().getAuthentication().getName(), id);
		if (status == 0) {
			return new ResponseEntity<>("Cancelaste tu asistencia al evento.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("El evento ya fue cancelado o finalizó.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>(
					"Solo los residentes del conjunto pueden inscribirse a este evento.", HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Devuelve el HTML de la invitación sin guardar el evento. Requiere rol ADMIN.
	 *
	 * @param data DTO con los datos del evento
	 * @return ResponseEntity con el HTML del correo
	 */
	@Operation(
			summary = "Vista previa de la invitación",
			description =
					"""
			Este endpoint arma el HTML del correo de invitación con la plantilla elegida, sin guardar el evento.

			**Nota:** el texto escrito por el administrador se escapa. Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "200",
						description = "HTML de la invitación",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "<!DOCTYPE html><html lang=\"es\">..."))),
				@ApiResponse(
						responseCode = "404",
						description = "Conjunto no encontrado",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping(
			path = "/vistaprevia",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> vistaPrevia(
			@Parameter(
							description = "Datos del evento",
							required = true,
							schema = @Schema(implementation = EventoDTO.class))
					@RequestBody
					EventoDTO data) {
		try {
			ExceptionChecker.checkSelected("Elige el conjunto.", data.getConjuntoId());
		} catch (OptionNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		String html = eventoServ.vistaPrevia(data);
		if (html != null) {
			return new ResponseEntity<>(html, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Revisa los datos obligatorios de un evento.
	 *
	 * @param data DTO a revisar
	 * @return Mensaje de error, o null si los datos son válidos
	 */
	private String validar(EventoDTO data) {
		try {
			ExceptionChecker.checkEmpty("título", data.getTitulo());
			ExceptionChecker.checkCharacters(data.getTitulo());
			ExceptionChecker.checkCharacters(data.getDescripcion());
			ExceptionChecker.checkDate(data.getFecha());
			ExceptionChecker.checkTimeRange(data.getHoraInicio(), data.getHoraFin());
			ExceptionChecker.checkSelected("Elige el conjunto.", data.getConjuntoId());
			ExceptionChecker.checkSelected(
					"Elige el tipo de evento y la plantilla de la invitación.", data.getTipo());
			ExceptionChecker.checkSelected(
					"Elige el tipo de evento y la plantilla de la invitación.", data.getPlantilla());
			ExceptionChecker.checkOptionalPositive("cupo", data.getCupoMaximo());
		} catch (EmptyFieldException
				| InvalidCharactersException
				| DateNotValidException
				| TimeNotValidException
				| OptionNotValidException
				| NumberNotValidException e) {
			return e.getMessage();
		}
		return null;
	}

	/**
	 * Convierte los errores de datos del servicio en la respuesta HTTP.
	 *
	 * @param status Código de resultado del servicio
	 * @return ResponseEntity con el mensaje correspondiente
	 */
	private ResponseEntity<String> respuestaDatos(int status) {
		if (status == 1) {
			return new ResponseEntity<>("Conjunto no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>(
					"El espacio elegido no existe o no pertenece a ese conjunto.", HttpStatus.BAD_REQUEST);
		} else if (status == 7) {
			return new ResponseEntity<>(
					"El espacio ya está reservado u ocupado por otro evento en ese horario.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>(
					"La fecha y hora del evento deben ser futuras.", HttpStatus.BAD_REQUEST);
		}
	}
}
