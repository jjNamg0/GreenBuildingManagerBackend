package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.RutaDTO;
import co.edu.unbosque.GreenBuilding.exception.CoordinateNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.service.RutaService;
import co.edu.unbosque.GreenBuilding.util.ExceptionChecker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * Controlador REST para calcular rutas con OSRM. Es público, para que el mapa funcione sin iniciar
 * sesión.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/ruta")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Rutas", description = "Cálculo de rutas con OSRM")
public class RutaController {

	/** Servicio que calcula las rutas. */
	@Autowired private RutaService rutaServ;

	/** Constructor por defecto. */
	public RutaController() {}

	/**
	 * Calcula la ruta entre dos puntos.
	 *
	 * @param origenLatitud Latitud de origen
	 * @param origenLongitud Longitud de origen
	 * @param destinoLatitud Latitud de destino
	 * @param destinoLongitud Longitud de destino
	 * @param modo CARRO, CAMINANDO o BICICLETA
	 * @return ResponseEntity con la ruta
	 */
	@Operation(
			summary = "Calcular ruta",
			description =
					"""
			Este endpoint calcula la ruta entre dos puntos con el servidor público de OSRM.

			**¿Qué hace?** Devuelve la distancia en metros, la duración en segundos, los puntos en formato [lat, lng]
			y las instrucciones paso a paso en español.

			**Nota:** Este endpoint es público.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Ruta calculada",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = RutaDTO.class),
										examples =
												@ExampleObject(
														value =
																"""
						{
						  "distanciaMetros": 8450.2,
						  "duracionSegundos": 912.4,
						  "puntos": [[4.8617, -74.0581], [4.8620, -74.0575]],
						  "pasos": [{ "instruccion": "Sal por Carrera 8A", "distanciaMetros": 120.5, "duracionSegundos": 20.1 }]
						}
					"""))),
				@ApiResponse(
						responseCode = "400",
						description = "Coordenadas o modo inválidos",
						content = @Content(mediaType = "application/json")),
				@ApiResponse(
						responseCode = "502",
						description = "OSRM no encontró ruta o no respondió",
						content = @Content(mediaType = "application/json"))
			})
	@GetMapping("/calcular")
	ResponseEntity<RutaDTO> calcular(
			@Parameter(description = "Latitud de origen", required = true, example = "4.8617") @RequestParam
					double origenLatitud,
			@Parameter(description = "Longitud de origen", required = true, example = "-74.0581")
					@RequestParam
					double origenLongitud,
			@Parameter(description = "Latitud de destino", required = true, example = "4.9186")
					@RequestParam
					double destinoLatitud,
			@Parameter(description = "Longitud de destino", required = true, example = "-74.0277")
					@RequestParam
					double destinoLongitud,
			@Parameter(description = "Modo de transporte", example = "CARRO")
					@RequestParam(defaultValue = "CARRO")
					String modo) {
		String modoLimpio = modo.trim().toUpperCase();
		try {
			ExceptionChecker.checkCoordinates(origenLatitud, origenLongitud);
			ExceptionChecker.checkCoordinates(destinoLatitud, destinoLongitud);
			ExceptionChecker.checkOption(rutaServ.esModoValido(modoLimpio));
		} catch (CoordinateNotValidException | OptionNotValidException e) {
			return new ResponseEntity<>(new RutaDTO(), HttpStatus.BAD_REQUEST);
		}
		RutaDTO ruta =
				rutaServ.calcular(origenLatitud, origenLongitud, destinoLatitud, destinoLongitud, modoLimpio);
		if (ruta != null) {
			return new ResponseEntity<>(ruta, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new RutaDTO(), HttpStatus.BAD_GATEWAY);
		}
	}
}
