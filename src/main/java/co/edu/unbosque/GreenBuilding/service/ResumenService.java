package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.ResumenDTO;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servicio que calcula los indicadores del panel de administración. */
@Service
public class ResumenService {

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a las unidades. */
	@Autowired
	private UnidadRepository unidadRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired
	private UsuarioRepository usuarioRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired
	private EspacioRepository espacioRepo;

	/** Repositorio para acceder a los eventos. */
	@Autowired
	private EventoRepository eventoRepo;

	/** Repositorio para acceder a las reservas. */
	@Autowired
	private ReservaRepository reservaRepo;

	/** Constructor por defecto. */
	public ResumenService() {
	}

	/**
	 * Calcula los indicadores del panel de administración.
	 *
	 * @return DTO con los totales
	 */
	public ResumenDTO getAdmin() {
		return new ResumenDTO(conjuntoRepo.count(), unidadRepo.count(), usuarioRepo.countByUnidadIdIsNotNull(),
				espacioRepo.count(),
				eventoRepo.countByEstadoAndFechaGreaterThanEqual(Evento.Estado.PROGRAMADO, LocalDate.now()),
				reservaRepo.countByEstado(Reserva.Estado.PENDIENTE),
				usuarioRepo.countByRoleAndUnidadIdIsNullAndActivoTrue(Role.USER));
	}
}
