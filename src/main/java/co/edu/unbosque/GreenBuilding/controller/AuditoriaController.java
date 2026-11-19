package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.PaginaAuditoriaDTO;
import co.edu.unbosque.GreenBuilding.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para consultar la auditoría del sistema. Todos los endpoints requieren
 * ROLE_ADMIN.
 *
 * @author Daniel Montoya
 * @version 1.0
 */
@RestController
@RequestMapping("/auditoria")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Auditoría", description = "Registro de acciones de los usuarios")
@SecurityRequirement(name = "bearerAuth")
public class AuditoriaController {

	/** Servicio de auditoría. */
	@Autowired private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public AuditoriaController() {}

	/**
	 * Consulta la auditoría con filtros y paginación. Requiere rol ADMIN.
	 *
	 * @param usuario Texto a buscar en el correo o nombre
	 * @param accion Acción a filtrar
	 * @param role Tipo de usuario a filtrar: ADMIN, USER o PUBLICO
	 * @param motivo Texto a buscar en el motivo (el porqué de la acción)
	 * @param desde Fecha inicial AAAA-MM-DD
	 * @param hasta Fecha final AAAA-MM-DD
	 * @param pagina Número de página, desde 0
	 * @param tamano Tamaño de la página
	 * @return ResponseEntity con la página de registros
	 */
	@Operation(
			summary = "Consultar auditoría",
			description =
					"""
			Este endpoint devuelve los registros de auditoría del más reciente al más antiguo, paginados.

			**Acciones:** LOGIN, LOGOUT, REGISTRO, VERIFICACION, RESTABLECER_PASSWORD, CREAR, ACTUALIZAR, ELIMINAR, RESERVA, EVENTO, ASISTENTE.

			**Filtros:** persona (correo o nombre), acción, tipo de usuario (ADMIN, USER o PUBLICO para quien no inició sesión), motivo (texto que contenga) y rango de fechas.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Página de registros",
						content =
								@Content(
										mediaType = "application/json",
										examples =
												@ExampleObject(
														value =
																"""
						{
						  "content": [
						    {
						      "id": 10,
						      "username": "admin@greenbuilding.com",
						      "nombreUsuario": "Administrador Green Building",
						      "role": "ADMIN",
						      "fecha": "2026-11-10T19:39:41",
						      "accion": "LOGIN",
						      "entidad": "Usuario",
						      "motivo": "Inicio de sesión",
						      "ubicacion": "4.70000, -74.10000",
						      "ip": "127.0.0.1"
						    }
						  ],
						  "totalElements": 1,
						  "totalPages": 1,
						  "number": 0,
						  "size": 20
						}
					""")))
			})
	@GetMapping("/getall")
	ResponseEntity<PaginaAuditoriaDTO> getAll(
			@Parameter(description = "Correo o nombre del usuario", example = "admin")
					@RequestParam(required = false)
					String usuario,
			@Parameter(description = "Acción", example = "LOGIN") @RequestParam(required = false)
					String accion,
			@Parameter(description = "Tipo de usuario: ADMIN, USER o PUBLICO", example = "USER")
					@RequestParam(required = false)
					String role,
			@Parameter(description = "Texto que contiene el motivo", example = "reserva")
					@RequestParam(required = false)
					String motivo,
			@Parameter(description = "Fecha inicial", example = "2026-11-01") @RequestParam(required = false)
					String desde,
			@Parameter(description = "Fecha final", example = "2026-11-30") @RequestParam(required = false)
					String hasta,
			@Parameter(description = "Página, desde 0", example = "0") @RequestParam(defaultValue = "0")
					int pagina,
			@Parameter(description = "Tamaño de la página", example = "20")
					@RequestParam(defaultValue = "20")
					int tamano) {
		return new ResponseEntity<>(
				auditoriaServ.getAll(usuario, accion, role, motivo, desde, hasta, pagina, tamano),
				HttpStatus.ACCEPTED);
	}

	/**
	 * Cuenta los registros de auditoría. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con el número de registros
	 */
	@Operation(
			summary = "Contar registros de auditoría",
			description =
					"""
			Este endpoint devuelve el número total de registros de auditoría.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Conteo exitoso",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "150"))),
				@ApiResponse(
						responseCode = "204",
						description = "No hay registros",
						content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "0")))
			})
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = auditoriaServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}
}
