package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.ChatDTO;
import co.edu.unbosque.GreenBuilding.dto.RespuestaChatDTO;
import co.edu.unbosque.GreenBuilding.service.AuditoriaService;
import co.edu.unbosque.GreenBuilding.service.IAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST del chat de ayuda. Requiere haber iniciado sesión (ROLE_USER o ROLE_ADMIN). La
 * API key del modelo vive en el back y el front nunca la ve.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/ia")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Asistente", description = "Chat de ayuda con inteligencia artificial")
@SecurityRequirement(name = "bearerAuth")
public class IAController {

	/** Servicio que habla con el modelo de IA. */
	@Autowired private IAService iaServ;

	/** Servicio de auditoría: cada pregunta al asistente queda registrada. */
	@Autowired private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public IAController() {}

	/**
	 * Responde una pregunta del chat de ayuda.
	 *
	 * @param chat DTO con la pregunta y la conversación anterior
	 * @return ResponseEntity con la respuesta del asistente o un mensaje de error
	 */
	@Operation(
			summary = "Preguntar al asistente (JSON)",
			description =
					"""
			Este endpoint responde una pregunta sobre el uso de Green Building con un modelo de NVIDIA.

			**Posibles resultados:**

			* Respuesta del asistente (202)
			* Mensaje vacío o demasiado largo (400)
			* Preguntas demasiado seguidas (429)
			* Asistente sin configurar o sin servicio (503)

			**Nota:** El historial lo envía el front en cada pregunta; no se guarda nada en la base de datos.
			Este endpoint requiere autenticación.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Respuesta del asistente",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = RespuestaChatDTO.class),
										examples =
												@ExampleObject(
														value =
																"""
						{
							"respuesta": "Para reservar un espacio entra a Reservar, elige el espacio y la franja."
						}
					"""))),
				@ApiResponse(
						responseCode = "400",
						description = "Mensaje vacío o demasiado largo",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "429",
						description = "Preguntas demasiado seguidas",
						content = @Content(mediaType = "text/plain")),
				@ApiResponse(
						responseCode = "503",
						description = "Asistente sin configurar o sin servicio",
						content = @Content(mediaType = "text/plain"))
			})
	@PostMapping(path = "/preguntar", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> preguntar(
			@RequestBody ChatDTO chat) {
		int status = iaServ.validar(SecurityContextHolder.getContext().getAuthentication().getName(), chat);
		if (status == 0) {
			auditoriaServ.registrar(
					SecurityContextHolder.getContext().getAuthentication().getName(),
					"ASISTENTE",
					"Asistente",
					"Preguntó al asistente: " + resumir(chat.getMensaje()));
			String respuesta = iaServ.responder(chat);
			if (respuesta != null) {
				return new ResponseEntity<>(new RespuestaChatDTO(respuesta), HttpStatus.ACCEPTED);
			}
			return new ResponseEntity<>(
					"El asistente no está disponible en este momento. Intenta más tarde.",
					HttpStatus.SERVICE_UNAVAILABLE);
		} else if (status == 1) {
			return new ResponseEntity<>("Escribe tu pregunta.", HttpStatus.BAD_REQUEST);
		} else if (status == 2) {
			return new ResponseEntity<>("La pregunta es demasiado larga.", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(
					"Espera un momento antes de preguntar de nuevo.", HttpStatus.TOO_MANY_REQUESTS);
		}
	}

	/**
	 * Acorta la pregunta para guardarla en la auditoría.
	 *
	 * @param mensaje Pregunta del usuario
	 * @return Los primeros 120 caracteres de la pregunta
	 */
	private String resumir(String mensaje) {
		String pregunta = mensaje.trim();
		if (pregunta.length() > 120) {
			return pregunta.substring(0, 120) + "…";
		}
		return pregunta;
	}
}
