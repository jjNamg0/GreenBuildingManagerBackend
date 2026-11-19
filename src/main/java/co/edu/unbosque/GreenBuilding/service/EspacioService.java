package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.EspacioDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.FechaUtil;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona operaciones CRUD para los espacios comunes de los conjuntos.
 * Implementa la interfaz CRUDOperation.
 */
@Service
public class EspacioService implements CRUDOperation<EspacioDTO> {

	/** Repositorio para acceder a los espacios. */
	@Autowired private EspacioRepository espacioRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a las reservas. */
	@Autowired private ReservaRepository reservaRepo;

	/** Repositorio para acceder a los eventos. */
	@Autowired private EventoRepository eventoRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired private UsuarioRepository usuarioRepo;

	/** Mapeador para convertir entre entidades y DTOs. */
	@Autowired private ModelMapper modelMapper;

	/** Servicio de auditoría. */
	@Autowired private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public EspacioService() {}

	/**
	 * Cuenta el número total de espacios.
	 *
	 * @return Número total de espacios
	 */
	@Override
	public long count() {
		return espacioRepo.count();
	}

	/**
	 * Verifica si existe un espacio con el ID especificado.
	 *
	 * @param id ID del espacio
	 * @return true si existe, false en caso contrario
	 */
	@Override
	public boolean exist(Long id) {
		return espacioRepo.existsById(id);
	}

	/**
	 * Crea un nuevo espacio en un conjunto.
	 *
	 * @param data DTO con los datos del espacio
	 * @return 0 si la creación fue exitosa, 1 si el conjunto no existe
	 */
	@Override
	public int create(EspacioDTO data) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(data.getConjuntoId());
		if (!conjunto.isPresent()) {
			return 1;
		}
		Espacio entity = new Espacio();
		aplicar(entity, data, conjunto.get());
		espacioRepo.save(entity);
		auditoriaServ.registrar(
				"CREAR",
				"Espacio",
				"Creó el espacio " + entity.getNombre() + " en " + conjunto.get().getNombre());
		return 0;
	}

	/**
	 * Obtiene todos los espacios.
	 *
	 * @return Lista de DTOs de espacios
	 */
	@Override
	public List<EspacioDTO> getAll() {
		List<EspacioDTO> dtoList = new ArrayList<>();
		espacioRepo.findAll().forEach((entity) -> dtoList.add(aDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene los espacios de un conjunto ordenados por nombre.
	 *
	 * @param conjuntoId ID del conjunto
	 * @return Lista de DTOs de espacios, o null si el conjunto no existe
	 */
	public List<EspacioDTO> getByConjunto(Long conjuntoId) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(conjuntoId);
		if (!conjunto.isPresent()) {
			return null;
		}
		List<EspacioDTO> dtoList = new ArrayList<>();
		espacioRepo
				.findByConjuntoIdOrderByNombreAsc(conjuntoId)
				.forEach((entity) -> dtoList.add(aDTO(entity)));
		return dtoList;
	}

	/**
	 * Elimina un espacio y sus reservas. Los eventos que lo usaban quedan sin espacio.
	 *
	 * @param id ID del espacio
	 * @return 0 si la eliminación fue exitosa, 1 si el espacio no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Espacio> found = espacioRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		for (Evento evento : eventoRepo.findByEspacioId(id)) {
			evento.setEspacioId(null);
		}
		for (Reserva reserva : reservaRepo.findByEspacioId(id)) {
			reservaRepo.delete(reserva);
		}
		espacioRepo.delete(found.get());
		auditoriaServ.registrar("ELIMINAR", "Espacio", "Eliminó el espacio " + found.get().getNombre());
		return 0;
	}

	/**
	 * Actualiza un espacio existente.
	 *
	 * @param id ID del espacio
	 * @param newData DTO con los nuevos datos
	 * @return 0 si la actualización fue exitosa, 2 si el espacio no existe, 3 si el conjunto no
	 *     existe
	 */
	@Override
	public int updateById(Long id, EspacioDTO newData) {
		Optional<Espacio> found = espacioRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Optional<Conjunto> conjunto = conjuntoRepo.findById(newData.getConjuntoId());
		if (!conjunto.isPresent()) {
			return 3;
		}
		Espacio temp = found.get();
		aplicar(temp, newData, conjunto.get());
		espacioRepo.save(temp);
		auditoriaServ.registrar("ACTUALIZAR", "Espacio", "Actualizó el espacio " + temp.getNombre());
		return 0;
	}

	/**
	 * Obtiene un espacio por su ID.
	 *
	 * @param id ID del espacio
	 * @return DTO del espacio si existe, null en caso contrario
	 */
	public EspacioDTO getById(Long id) {
		Optional<Espacio> found = espacioRepo.findById(id);
		if (found.isPresent()) {
			return aDTO(found.get());
		} else {
			return null;
		}
	}

	/**
	 * Indica si un usuario puede ver la información de un conjunto: los administradores ven todos y
	 * los residentes solo el suyo.
	 *
	 * @param username Correo del usuario
	 * @param conjuntoId ID del conjunto
	 * @return true si puede verlo
	 */
	public boolean puedeVerConjunto(String username, Long conjuntoId) {
		Optional<Usuario> found = usuarioRepo.findByUsername(username);
		if (!found.isPresent()) {
			return false;
		}
		Usuario usuario = found.get();
		if (usuario.getRole() == Role.ADMIN) {
			return true;
		}
		return usuario.getConjuntoId() != null && usuario.getConjuntoId().equals(conjuntoId);
	}

	/**
	 * Convierte un espacio en DTO con sus horas en formato HH:mm.
	 *
	 * @param espacio Espacio a convertir
	 * @return DTO del espacio
	 */
	public EspacioDTO aDTO(Espacio espacio) {
		EspacioDTO dto = modelMapper.map(espacio, EspacioDTO.class);
		dto.setHoraApertura(FechaUtil.hora(espacio.getHoraApertura()));
		dto.setHoraCierre(FechaUtil.hora(espacio.getHoraCierre()));
		return dto;
	}

	/**
	 * Copia los datos del DTO en la entidad.
	 *
	 * @param entity Espacio a modificar
	 * @param data Datos nuevos
	 * @param conjunto Conjunto del espacio
	 */
	private void aplicar(Espacio entity, EspacioDTO data, Conjunto conjunto) {
		entity.setConjuntoId(conjunto.getId());
		entity.setNombre(TextoUtil.limpiar(data.getNombre()));
		entity.setTipo(data.getTipo());
		entity.setDescripcion(TextoUtil.limpiar(data.getDescripcion()));
		entity.setCapacidad(data.getCapacidad());
		entity.setHoraApertura(FechaUtil.leerHora(data.getHoraApertura()));
		entity.setHoraCierre(FechaUtil.leerHora(data.getHoraCierre()));
		entity.setRequiereReserva(data.isRequiereReserva());
		entity.setDisponible(data.isDisponible());
		entity.setImagenUrl(TextoUtil.opcional(data.getImagenUrl()));
	}
}
