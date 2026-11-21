package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.RecomendacionConjuntoDTO;
import co.edu.unbosque.GreenBuilding.dto.RecomendacionEspacioDTO;
import co.edu.unbosque.GreenBuilding.dto.RecomendacionEventoDTO;
import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.service.RecomendacionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para las recomendaciones. Los eventos y espacios
 * recomendados son para residentes (ROLE_USER); los conjuntos cercanos son
 * públicos.
 *
 * @author Daniel Montoya
 * @version 1.0
 */
@RestController
@RequestMapping("/recomendacion")

@CrossOrigin(origins = { "https://greenbuildingmanager.netlify.app/" })
@Transactional
@Tag(name = "Recomendaciones", description = "Eventos, espacios y conjuntos sugeridos")
@SecurityRequirement(name = "bearerAuth")
public class RecomendacionController {

	/** Servicio que calcula las recomendaciones. */
	@Autowired
	private RecomendacionService recomendacionServ;

	/** Constructor por defecto. */
	public RecomendacionController() {
	}

	/**
	 * Recomienda eventos al residente con la sesión iniciada. Requiere rol USER.
	 *
	 * @return ResponseEntity con la lista de recomendaciones
	 */
	@Operation(summary = "Eventos recomendados", description = """
				Este endpoint recomienda eventos próximos del conjunto del residente.

				**¿Cómo se calcula?** Suma puntos si el tipo coincide con sus intereses, si es un plan para niños y le
				interesan, si ya asistió a eventos parecidos y si es esta semana.

				**Nota:** Este endpoint requiere autenticación y rol USER.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de recomendaciones", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecomendacionEventoDTO.class))),
			@ApiResponse(responseCode = "204", description = "No hay recomendaciones", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))) })
	@GetMapping("/geteventos")
	ResponseEntity<List<RecomendacionEventoDTO>> getEventos() {
		List<RecomendacionEventoDTO> lista = recomendacionServ
				.getEventos(SecurityContextHolder.getContext().getAuthentication().getName());
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Recomienda espacios al residente con la sesión iniciada. Requiere rol USER.
	 *
	 * @return ResponseEntity con la lista de recomendaciones
	 */
	@Operation(summary = "Espacios recomendados", description = """
				Este endpoint recomienda espacios disponibles del conjunto del residente.

				**¿Cómo se calcula?** Suma puntos si el tipo coincide con sus intereses, si ya lo reservó antes y si es de uso libre.

				**Nota:** Este endpoint requiere autenticación y rol USER.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de recomendaciones", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecomendacionEspacioDTO.class))),
			@ApiResponse(responseCode = "204", description = "No hay recomendaciones", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))) })
	@GetMapping("/getespacios")
	ResponseEntity<List<RecomendacionEspacioDTO>> getEspacios() {
		List<RecomendacionEspacioDTO> lista = recomendacionServ
				.getEspacios(SecurityContextHolder.getContext().getAuthentication().getName());
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Recomienda los conjuntos más cercanos a una ubicación. Es público.
	 *
	 * @param latitud  Latitud del visitante
	 * @param longitud Longitud del visitante
	 * @return ResponseEntity con la lista de conjuntos cercanos
	 */
	@Operation(summary = "Conjuntos cercanos", description = """
				Este endpoint devuelve los conjuntos más cercanos a la ubicación del visitante, en línea recta.

				**Nota:** Este endpoint es público.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de conjuntos cercanos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecomendacionConjuntoDTO.class))),
			@ApiResponse(responseCode = "204", description = "No hay conjuntos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))),
			@ApiResponse(responseCode = "400", description = "Ubicación inválida", content = @Content(mediaType = "application/json")) })
	@GetMapping("/getconjuntoscercanos")
	ResponseEntity<List<RecomendacionConjuntoDTO>> getConjuntosCercanos(
			@Parameter(description = "Latitud", required = true, example = "4.86") @RequestParam double latitud,
			@Parameter(description = "Longitud", required = true, example = "-74.05") @RequestParam double longitud) {
		try {
			ExceptionChecker.checkCoordinates(latitud, longitud);
		} catch (CoordinateNotValidException e) {
			return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
		}
		List<RecomendacionConjuntoDTO> lista = recomendacionServ.getConjuntosCercanos(latitud, longitud);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.ACCEPTED);
		}
	}
}
