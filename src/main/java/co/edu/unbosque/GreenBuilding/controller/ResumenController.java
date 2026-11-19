package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.ResumenDTO;
import co.edu.unbosque.GreenBuilding.service.ResumenService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST con los indicadores del panel de administración. Requiere ROLE_ADMIN.
 *
 * @author Juan Arango
 * @version 1.0
 */
@RestController
@RequestMapping("/resumen")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(name = "Resumen", description = "Indicadores del panel de administración")
@SecurityRequirement(name = "bearerAuth")
public class ResumenController {

	/** Servicio que calcula los indicadores. */
	@Autowired private ResumenService resumenServ;

	/** Constructor por defecto. */
	public ResumenController() {}

	/**
	 * Obtiene los indicadores del panel de administración. Requiere rol ADMIN.
	 *
	 * @return ResponseEntity con los totales
	 */
	@Operation(
			summary = "Resumen del administrador",
			description =
					"""
			Este endpoint devuelve los totales del panel: conjuntos, unidades, residentes, espacios,
			eventos próximos, reservas pendientes y usuarios que esperan unidad.

			**Nota:** Este endpoint requiere autenticación y rol ADMIN.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Indicadores calculados",
						content =
								@Content(
										mediaType = "application/json",
										schema = @Schema(implementation = ResumenDTO.class),
										examples =
												@ExampleObject(
														value =
																"""
						{
						  "totalConjuntos": 4,
						  "totalUnidades": 7,
						  "totalResidentes": 1,
						  "totalEspacios": 5,
						  "eventosProximos": 3,
						  "reservasPendientes": 0,
						  "usuariosSinUnidad": 1
						}
					""")))
			})
	@GetMapping("/getadmin")
	ResponseEntity<ResumenDTO> getAdmin() {
		return new ResponseEntity<>(resumenServ.getAdmin(), HttpStatus.ACCEPTED);
	}
}
