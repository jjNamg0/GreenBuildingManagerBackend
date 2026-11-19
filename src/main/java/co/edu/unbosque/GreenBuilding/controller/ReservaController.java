package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.FranjaHorariaDTO;
import co.edu.unbosque.GreenBuilding.dto.ReservaDTO;
import co.edu.unbosque.GreenBuilding.exception.DateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.NumberNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.TimeNotValidException;
import co.edu.unbosque.GreenBuilding.model.Reserva.Estado;
import co.edu.unbosque.GreenBuilding.service.EspacioService;
import co.edu.unbosque.GreenBuilding.service.ReservaService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para las reservas de espacios comunes. Los residentes (ROLE_USER) consultan la
 * disponibilidad, reservan y cancelan; los administradores (ROLE_ADMIN) ven todas las reservas y
 * las aprueban o rechazan.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/reserva")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Gestión de Reservas", description = "Reservas de espacios comunes")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

	/** Servicio para operaciones relacionadas con reservas. */
	@Autowired private ReservaService reservaServ;

	/** Servicio de espacios, para revisar el acceso al conjunto. */
	@Autowired private EspacioService espacioServ;

	/** Constructor por defecto. */
	public ReservaController() {}

	/**
	 * Crea una reserva para el residente con la sesión iniciada. Requiere rol USER.
	 *
	 * @param newReserva DTO con los datos de la reserva
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Crear reserva (JSON)",
			description =
					"""
			Este endpoint permite a un residente reservar un espacio común.

			**¿Qué hace?** Revisa que el residente tenga unidad, que el espacio sea de su conjunto y necesite reserva,
			que la hora esté dentro del horario, que no sea en el pasado, que no supere 60 días de anticipación ni la capacidad,
			y que no se cruce con otra reserva pendiente o aprobada. La reserva queda PENDIENTE.

			**Nota:** Este endpoint requiere autenticación y rol USER.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "201",
						description = "Reserva creada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Tu reserva quedó pendiente de aprobación."))),
				@ApiResponse(
						responseCode = "409",
						description = "Horario ocupado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Ese horario ya está reservado. Elige otro."))),
				@ApiResponse(
						responseCode = "400",
						description = "Datos inválidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(value = "No puedes reservar en una fecha u hora que ya pasó."))),
				@ApiResponse(
						responseCode = "403",
						description = "Sin unidad o espacio de otro conjunto",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> createNewWithJSON(
			@Parameter(
							description = "Datos de la reserva",
							required = true,
							schema = @Schema(implementation = ReservaDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "espacioId": 1,
					  "fecha": "2026-11-20",
					  "horaInicio": "10:00",
					  "horaFin": "12:00",
					  "motivo": "Cumpleaños",
					  "asistentes": 20
					}
				"""))
					@RequestBody
					ReservaDTO newReserva) {
		try {
			ExceptionChecker.checkDate(newReserva.getFecha());
			ExceptionChecker.checkTimeRange(newReserva.getHoraInicio(), newReserva.getHoraFin());
			ExceptionChecker.checkEmpty("motivo", newReserva.getMotivo());
			ExceptionChecker.checkCharacters(newReserva.getMotivo());
			ExceptionChecker.checkPositive("asistentes", newReserva.getAsistentes());
			ExceptionChecker.checkSelected("Elige el espacio.", newReserva.getEspacioId());
		} catch (DateNotValidException
				| TimeNotValidException
				| EmptyFieldException
				| InvalidCharactersException
				| NumberNotValidException
				| OptionNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = reservaServ.create(SecurityContextHolder.getContext().getAuthentication().getName(), newReserva);
		if (status == 0) {
			return new ResponseEntity<>("Tu reserva quedó pendiente de aprobación.", HttpStatus.CREATED);
		} else if (status == 1) {
			return new ResponseEntity<>(
					"Necesitas tener una unidad asignada para reservar.", HttpStatus.FORBIDDEN);
		} else if (status == 2) {
			return new ResponseEntity<>("Espacio no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 3) {
			return new ResponseEntity<>("Solo puedes reservar espacios de tu conjunto.", HttpStatus.FORBIDDEN);
		} else if (status == 4) {
			return new ResponseEntity<>(
					"Este espacio es de uso libre, no necesita reserva.", HttpStatus.BAD_REQUEST);
		} else if (status == 5) {
			return new ResponseEntity<>("Este espacio no está disponible por ahora.", HttpStatus.CONFLICT);
		} else if (status == 6) {
			return new ResponseEntity<>(
					"La reserva está fuera del horario del espacio.", HttpStatus.BAD_REQUEST);
		} else if (status == 7) {
			return new ResponseEntity<>(
					"No puedes reservar en una fecha u hora que ya pasó.", HttpStatus.BAD_REQUEST);
		} else if (status == 8) {
			return new ResponseEntity<>(
					"Solo puedes reservar con "
							+ ReservaService.DIAS_MAXIMOS_ANTICIPACION
							+ " días de anticipación como máximo.",
					HttpStatus.BAD_REQUEST);
		} else if (status == 9) {
			return new ResponseEntity<>(
					"El número de asistentes supera la capacidad del espacio.", HttpStatus.BAD_REQUEST);
		} else if (status == 10) {
			return new ResponseEntity<>("Ese horario ya está reservado. Elige otro.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>(
					"Ese horario está ocupado por un evento del conjunto. Elige otro.", HttpStatus.CONFLICT);
		}
	}

	/**
	 * Obtiene las reservas del residente con la sesión iniciada. Requiere rol USER.
	 *
	 * @return ResponseEntity con la lista de reservas
	 */
	@Operation(
			summary = "Obtener mis reservas",
			description =
					"""
			Este endpoint devuelve las reservas del residente, de la más reciente a la más antigua.

			**Nota:** Este endpoint requiere autenticación y rol USER.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Lista recuperada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = ReservaDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "El residente no tiene reservas",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getmine")
	ResponseEntity<List<ReservaDTO>> getMine() {
		List<ReservaDTO> reservas = reservaServ.getMine(SecurityContextHolder.getContext().getAuthentication().getName());
		if (reservas.isEmpty()) {
			return new ResponseEntity<>(reservas, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(reservas, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene todas las reservas, opcionalmente por estado. Requiere rol ADMIN.
	 *
	 * @param estado Estado de las reservas
	 * @return ResponseEntity con la lista de reservas
	 */
	@Operation(
			summary = "Obtener todas las reservas",
			description =
					"""
			Este endpoint devuelve las reservas de todos los conjuntos. Si no se envía estado, devuelve todas.

			**Estados:** PENDIENTE, APROBADA, RECHAZADA, CANCELADA.

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
										schema = @Schema(implementation = ReservaDTO.class))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay reservas con ese estado",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]")))
			})
	@GetMapping("/getall")
	ResponseEntity<List<ReservaDTO>> getAll(
			@Parameter(description = "Estado de las reservas", example = "PENDIENTE")
					@RequestParam(required = false)
					Estado estado) {
		List<ReservaDTO> reservas = reservaServ.getAll(estado);
		if (reservas.isEmpty()) {
			return new ResponseEntity<>(reservas, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(reservas, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cuenta el número total de reservas. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con el número de reservas
	 */
	@Operation(
			summary = "Contar reservas",
			description =
					"""
			Este endpoint devuelve el número total de reservas.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conteo exitoso",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "12"))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay reservas",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0")))
			})
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = reservaServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene una reserva por su ID. Requiere rol ADMIN.
	 *
	 * @param id ID de la reserva
	 * @return ResponseEntity con la reserva o un objeto vacío si no existe
	 */
	@Operation(
			summary = "Obtener reserva por ID",
			description =
					"""
			Este endpoint devuelve una reserva.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Reserva encontrada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = ReservaDTO.class))),
				@ApiResponse(
						responseCode = "404",
						description = "Reserva no encontrada",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}")))
			})
	@GetMapping("/getbyid/{id}")
	ResponseEntity<ReservaDTO> getById(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable
					Long id) {
		ReservaDTO found = reservaServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new ReservaDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtiene las franjas de una hora de un espacio en un día. Requiere rol USER.
	 *
	 * @param espacioId ID del espacio
	 * @param fecha Fecha AAAA-MM-DD
	 * @return ResponseEntity con la lista de franjas
	 */
	@Operation(
			summary = "Consultar disponibilidad",
			description =
					"""
			Este endpoint arma las franjas de una hora de un espacio en una fecha.

			**¿Qué hace?** Marca como no disponibles las franjas ocupadas por reservas pendientes o aprobadas
			y las que ya pasaron. Un espacio de uso libre no tiene franjas.

			**Nota:** Este endpoint requiere autenticación y rol USER.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Franjas del día",
						content =
								@Content(
										mediaType = "application/json",
										examples =
												@ExampleObject(
														value =
																"""
						[
						  { "horaInicio": "08:00", "horaFin": "09:00", "disponible": true },
						  { "horaInicio": "09:00", "horaFin": "10:00", "disponible": false }
						]
					"""))),
				@ApiResponse(
						responseCode = "204",
						description = "El espacio es de uso libre",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))),
				@ApiResponse(
						responseCode = "400",
						description = "Fecha inválida",
						content = @Content(mediaType = "application/json")),
				@ApiResponse(
						responseCode = "404",
						description = "Espacio no encontrado",
						content = @Content(mediaType = "application/json"))
			})
	@GetMapping("/getdisponibilidad")
	ResponseEntity<List<FranjaHorariaDTO>> getDisponibilidad(
			@Parameter(description = "ID del espacio", required = true, example = "1") @RequestParam
					Long espacioId,
			@Parameter(description = "Fecha AAAA-MM-DD", required = true, example = "2026-11-20")
					@RequestParam
					String fecha) {
		try {
			ExceptionChecker.checkDate(fecha);
		} catch (DateNotValidException e) {
			return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
		}
		if (espacioServ.getById(espacioId) == null) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		}
		if (!espacioServ.puedeVerConjunto(
				SecurityContextHolder.getContext().getAuthentication().getName(), espacioServ.getById(espacioId).getConjuntoId())) {
			return new ResponseEntity<>(List.of(), HttpStatus.FORBIDDEN);
		}
		List<FranjaHorariaDTO> franjas = reservaServ.getDisponibilidad(espacioId, fecha);
		if (franjas.isEmpty()) {
			return new ResponseEntity<>(franjas, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(franjas, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Cancela una reserva del residente con la sesión iniciada. Requiere rol USER.
	 *
	 * @param id ID de la reserva
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Cancelar mi reserva",
			description =
					"""
			Este endpoint permite al residente cancelar una reserva pendiente o aprobada que aún no ha empezado.

			**Nota:** Este endpoint requiere autenticación y rol USER.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Reserva cancelada",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Reserva cancelada."))),
				@ApiResponse(
						responseCode = "403",
						description = "La reserva es de otro residente",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "409",
						description = "La reserva ya no se puede cancelar",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping("/cancelar/{id}")
	ResponseEntity<String> cancelar(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable
					Long id) {
		int status = reservaServ.cancelar(SecurityContextHolder.getContext().getAuthentication().getName(), id);
		if (status == 0) {
			return new ResponseEntity<>("Reserva cancelada.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("Solo puedes cancelar tus propias reservas.", HttpStatus.FORBIDDEN);
		} else {
			return new ResponseEntity<>("Esta reserva ya no se puede cancelar.", HttpStatus.CONFLICT);
		}
	}

	/**
	 * Aprueba una reserva pendiente. Requiere rol ADMIN.
	 *
	 * @param id ID de la reserva
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Aprobar reserva",
			description =
					"""
			Este endpoint aprueba una reserva pendiente y avisa al residente por correo.

			**Nota:** se vuelve a revisar que no se cruce con otra reserva aprobada ni con un evento.
			Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Reserva aprobada",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Reserva aprobada."))),
				@ApiResponse(
						responseCode = "409",
						description = "No está pendiente, ya pasó o se cruza con otra",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping("/aprobar/{id}")
	ResponseEntity<String> aprobar(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable
					Long id) {
		int status = reservaServ.aprobar(id);
		if (status == 0) {
			return new ResponseEntity<>("Reserva aprobada.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("Solo se pueden revisar reservas pendientes.", HttpStatus.CONFLICT);
		} else if (status == 3) {
			return new ResponseEntity<>("La fecha de esta reserva ya pasó.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>(
					"Ya hay otra reserva aprobada o un evento en ese horario.", HttpStatus.CONFLICT);
		}
	}

	/**
	 * Rechaza una reserva pendiente con una observación. Requiere rol ADMIN.
	 *
	 * @param id ID de la reserva
	 * @param data DTO con la observación
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Rechazar reserva",
			description =
					"""
			Este endpoint rechaza una reserva pendiente con el motivo y avisa al residente por correo.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Reserva rechazada",
						content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Reserva rechazada."))),
				@ApiResponse(
						responseCode = "400",
						description = "Falta la observación",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "409",
						description = "No está pendiente o ya pasó",
						content = @Content(mediaType = "text/plain"))
			})
	@PutMapping(path = "/rechazar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> rechazar(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable
					Long id,
			@Parameter(
							description = "Motivo del rechazo",
							required = true,
							schema = @Schema(implementation = ReservaDTO.class),
							examples = @ExampleObject(value = "{ \"observacion\": \"El salón está en mantenimiento\" }"))
					@RequestBody
					ReservaDTO data) {
		try {
			ExceptionChecker.checkEmpty("observación", data.getObservacion());
			ExceptionChecker.checkCharacters(data.getObservacion());
		} catch (EmptyFieldException | InvalidCharactersException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = reservaServ.rechazar(id, data.getObservacion());
		if (status == 0) {
			return new ResponseEntity<>("Reserva rechazada.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("Solo se pueden revisar reservas pendientes.", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>("La fecha de esta reserva ya pasó.", HttpStatus.CONFLICT);
		}
	}
}
