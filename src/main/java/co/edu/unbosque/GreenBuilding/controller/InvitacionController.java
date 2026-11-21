package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.CorreosInvitacionDTO;
import co.edu.unbosque.GreenBuilding.dto.InvitacionDTO;
import co.edu.unbosque.GreenBuilding.dto.InvitadoDTO;
import co.edu.unbosque.GreenBuilding.exception.EmailNotValidException;
import co.edu.unbosque.GreenBuilding.service.EventoService;
import co.edu.unbosque.GreenBuilding.service.InvitacionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de las invitaciones. Cualquiera puede abrir una invitación
 * con su código; el administrador invita a los eventos (ROLE_ADMIN) y cada
 * residente invita a sus reservas aprobadas (ROLE_USER).
 *
 * @author Daniel Montoya
 * @version 1.0
 */
@RestController
@RequestMapping("/invitacion")
@CrossOrigin(origins = { "https://greenbuildingmanager.netlify.app/" })
@Transactional
@Tag(name = "Invitaciones", description = "Enlaces públicos e invitaciones por correo a eventos y reservas")
@SecurityRequirement(name = "bearerAuth")
public class InvitacionController {

	/** Servicio de invitaciones. */
	@Autowired
	private InvitacionService invitacionServ;

	/** Servicio de eventos, para saber si un evento existe. */
	@Autowired
	private EventoService eventoServ;

	/** Constructor por defecto. */
	public InvitacionController() {
	}

	/**
	 * Obtiene la invitación de un enlace público. No requiere sesión.
	 *
	 * @param codigo Código del enlace
	 * @return ResponseEntity con la invitación o un DTO vacío si el código no
	 *         existe
	 */
	@Operation(summary = "Abrir una invitación", description = """
				Este endpoint devuelve lo que ve un invitado al abrir el enlace: el plan, quién invita, la fecha,
				el lugar y las coordenadas del conjunto para el mapa y la ruta.

				**Nota:** no requiere iniciar sesión. No devuelve datos de los residentes.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Invitación encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvitacionDTO.class), examples = @ExampleObject(value = """
						{
							"tipo": "EVENTO",
							"titulo": "Fiesta de fin de año",
							"anfitrion": "Reservas de la Sabana",
							"fecha": "2026-11-27",
							"horaInicio": "19:00",
							"horaFin": "23:00",
							"plantilla": "FIESTA",
							"estado": "VIGENTE",
							"conjuntoId": 1,
							"conjuntoNombre": "Reservas de la Sabana",
							"direccion": "Calle 9 # 11-40",
							"municipio": "Chía",
							"latitud": 4.8617,
							"longitud": -74.0581,
							"espacioNombre": "Salón social"
						}
					"""))),
			@ApiResponse(responseCode = "404", description = "El enlace no existe", content = @Content(mediaType = "application/json")) })
	@GetMapping("/getbycodigo/{codigo}")
	ResponseEntity<InvitacionDTO> getByCodigo(
			@Parameter(description = "Código del enlace", required = true, example = "k3Jd9QmZpX7aB2cRtV5wYh4N") @PathVariable String codigo) {
		InvitacionDTO found = invitacionServ.getByCodigo(codigo);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new InvitacionDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtiene el enlace público de un evento. Requiere rol ADMIN.
	 *
	 * @param id ID del evento
	 * @return ResponseEntity con el enlace
	 */
	@Operation(summary = "Obtener el enlace de un evento", description = """
				Este endpoint devuelve el enlace público de la invitación del evento, para copiarlo y compartirlo.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Enlace del evento", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "http://localhost:4200/invitacion/k3Jd9QmZpX7aB2cRtV5wYh4N"))),
			@ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(mediaType = "text/plain")) })
	@GetMapping("/getenlaceevento/{id}")
	ResponseEntity<String> getEnlaceEvento(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable Long id) {
		String enlace = invitacionServ.getEnlaceEvento(id);
		if (enlace != null) {
			return new ResponseEntity<>(enlace, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Envía la invitación de un evento a una lista de correos. Requiere rol ADMIN.
	 *
	 * @param id   ID del evento
	 * @param data DTO con los correos de los invitados
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Invitar a un evento por correo (JSON)", description = """
				Este endpoint envía la invitación del evento, con su plantilla y el botón hacia el enlace público,
				a cada correo de la lista. Los correos quedan guardados para avisarles si el evento se cancela.

				**Posibles resultados:**

				* Invitaciones enviadas (202)
				* Lista vacía, más de 30 correos o correos inválidos (400)
				* Evento no encontrado (404)
				* Evento cancelado o finalizado (409)

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Invitaciones enviadas", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Enviamos la invitación a 3 correo(s)."))),
			@ApiResponse(responseCode = "400", description = "Correos inválidos", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "409", description = "Evento cancelado o finalizado", content = @Content(mediaType = "text/plain")) })
	@PostMapping(path = "/enviarevento/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> enviarEvento(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable Long id,
			@Parameter(description = "Correos de los invitados", required = true, schema = @Schema(implementation = CorreosInvitacionDTO.class), examples = @ExampleObject(value = "{ \"correos\": [\"ana@correo.com\", \"luis@correo.com\"] }")) @RequestBody CorreosInvitacionDTO data) {
		try {
			ExceptionChecker.checkEmails(data.getCorreos());
		} catch (EmailNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int enviados = invitacionServ.enviarEvento(id, data.getCorreos());
		if (enviados >= 0) {
			return respuestaEnvio(enviados);
		} else if (enviados == -1) {
			return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("El evento ya fue cancelado o finalizó.", HttpStatus.CONFLICT);
		}
	}

	/**
	 * Obtiene los correos invitados a un evento. Requiere rol ADMIN.
	 *
	 * @param id ID del evento
	 * @return ResponseEntity con la lista de invitados
	 */
	@Operation(summary = "Obtener los invitados de un evento", description = """
				Este endpoint devuelve los correos invitados al evento y la fecha del último envío.

				**Nota:** Este endpoint requiere autenticación y rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de invitados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[ { \"correo\": \"ana@correo.com\", \"fechaEnvio\": \"2026-11-17 10:30\" } ]"))),
			@ApiResponse(responseCode = "204", description = "Aún no hay invitados", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(mediaType = "application/json")) })
	@GetMapping("/getinvitadosevento/{id}")
	ResponseEntity<List<InvitadoDTO>> getInvitadosEvento(
			@Parameter(description = "ID del evento", required = true, example = "1") @PathVariable Long id) {
		if (!eventoServ.exist(id)) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		}
		return respuestaInvitados(invitacionServ.getInvitadosEvento(id));
	}

	/**
	 * Obtiene el enlace público de una reserva propia y aprobada. Requiere rol
	 * USER.
	 *
	 * @param id ID de la reserva
	 * @return ResponseEntity con el enlace o un mensaje de error
	 */
	@Operation(summary = "Obtener el enlace de mi reserva", description = """
				Este endpoint devuelve el enlace público para invitar personas a una reserva aprobada del residente.

				**Posibles resultados:**

				* Enlace de la reserva (202)
				* La reserva es de otro residente (403)
				* Reserva no encontrada (404)
				* La reserva no está aprobada o ya empezó (409)

				**Nota:** Este endpoint requiere autenticación y rol USER.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Enlace de la reserva", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "http://localhost:4200/invitacion/p8Hn2KqW5zXbC7dRfT3vYm9L"))),
			@ApiResponse(responseCode = "403", description = "La reserva es de otro residente", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "409", description = "La reserva no está aprobada o ya empezó", content = @Content(mediaType = "text/plain")) })
	@GetMapping("/getenlacereserva/{id}")
	ResponseEntity<String> getEnlaceReserva(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable Long id) {
		int status = invitacionServ.revisarReserva(SecurityContextHolder.getContext().getAuthentication().getName(),
				id);
		if (status == 0) {
			return new ResponseEntity<>(invitacionServ.getEnlaceReserva(id), HttpStatus.ACCEPTED);
		} else {
			return respuestaReserva(status);
		}
	}

	/**
	 * Envía la invitación de una reserva propia y aprobada a una lista de correos.
	 * Requiere rol USER.
	 *
	 * @param id   ID de la reserva
	 * @param data DTO con los correos de los invitados
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(summary = "Invitar a mi reserva por correo (JSON)", description = """
				Este endpoint envía la invitación de la reserva, con el botón hacia el enlace público, a cada correo
				de la lista. Los correos quedan guardados para avisarles si el residente cancela la reserva.

				**Posibles resultados:**

				* Invitaciones enviadas (202)
				* Lista vacía, más de 30 correos o correos inválidos (400)
				* La reserva es de otro residente (403)
				* Reserva no encontrada (404)
				* La reserva no está aprobada o ya empezó (409)

				**Nota:** Este endpoint requiere autenticación y rol USER.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Invitaciones enviadas", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Enviamos la invitación a 3 correo(s)."))),
			@ApiResponse(responseCode = "400", description = "Correos inválidos", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "403", description = "La reserva es de otro residente", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "409", description = "La reserva no está aprobada o ya empezó", content = @Content(mediaType = "text/plain")) })
	@PostMapping(path = "/enviarreserva/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> enviarReserva(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable Long id,
			@Parameter(description = "Correos de los invitados", required = true, schema = @Schema(implementation = CorreosInvitacionDTO.class), examples = @ExampleObject(value = "{ \"correos\": [\"ana@correo.com\", \"luis@correo.com\"] }")) @RequestBody CorreosInvitacionDTO data) {
		try {
			ExceptionChecker.checkEmails(data.getCorreos());
		} catch (EmailNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		int status = invitacionServ.revisarReserva(username, id);
		if (status == 0) {
			return respuestaEnvio(invitacionServ.enviarReserva(username, id, data.getCorreos()));
		} else {
			return respuestaReserva(status);
		}
	}

	/**
	 * Obtiene los correos invitados a una reserva propia. Requiere rol USER.
	 *
	 * @param id ID de la reserva
	 * @return ResponseEntity con la lista de invitados
	 */
	@Operation(summary = "Obtener los invitados de mi reserva", description = """
				Este endpoint devuelve los correos invitados a una reserva del residente y la fecha del último envío.

				**Nota:** Este endpoint requiere autenticación y rol USER.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de invitados", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "204", description = "Aún no hay invitados", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "403", description = "La reserva es de otro residente", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "application/json")) })
	@GetMapping("/getinvitadosreserva/{id}")
	ResponseEntity<List<InvitadoDTO>> getInvitadosReserva(
			@Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable Long id) {
		int status = invitacionServ.revisarReserva(SecurityContextHolder.getContext().getAuthentication().getName(),
				id);
		if (status == 1) {
			return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>(List.of(), HttpStatus.FORBIDDEN);
		} else {
			return respuestaInvitados(invitacionServ.getInvitadosReserva(id));
		}
	}

	/**
	 * Arma la respuesta de un envío de invitaciones.
	 *
	 * @param enviados Número de correos enviados
	 * @return ResponseEntity con el mensaje del envío
	 */
	private ResponseEntity<String> respuestaEnvio(int enviados) {
		if (enviados > 0) {
			return new ResponseEntity<>("Enviamos la invitación a " + enviados + " correo(s).", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("No pudimos enviar las invitaciones. Intenta más tarde.",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Convierte los errores de revisión de una reserva en la respuesta HTTP.
	 *
	 * @param status Código de resultado de la revisión
	 * @return ResponseEntity con el mensaje correspondiente
	 */
	private ResponseEntity<String> respuestaReserva(int status) {
		if (status == 1) {
			return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("Solo puedes invitar a tus propias reservas.", HttpStatus.FORBIDDEN);
		} else {
			return new ResponseEntity<>("Solo puedes invitar a reservas aprobadas que aún no empiezan.",
					HttpStatus.CONFLICT);
		}
	}

	/**
	 * Arma la respuesta de una lista de invitados.
	 *
	 * @param invitados Lista de invitados
	 * @return ResponseEntity con la lista, 202 si tiene datos o 204 si está vacía
	 */
	private ResponseEntity<List<InvitadoDTO>> respuestaInvitados(List<InvitadoDTO> invitados) {
		if (invitados.isEmpty()) {
			return new ResponseEntity<>(invitados, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(invitados, HttpStatus.ACCEPTED);
		}
	}
}
