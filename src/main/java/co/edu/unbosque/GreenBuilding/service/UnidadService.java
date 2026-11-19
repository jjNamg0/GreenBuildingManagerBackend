package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.UnidadDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona operaciones CRUD para las unidades (apartamentos y
 * casas) de los conjuntos. Implementa la interfaz CRUDOperation.
 */
@Service
public class UnidadService implements CRUDOperation<UnidadDTO> {

	/** Repositorio para acceder a las unidades. */
	@Autowired
	private UnidadRepository unidadRepo;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired
	private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired
	private UsuarioRepository usuarioRepo;

	/** Mapeador para convertir entre entidades y DTOs. */
	@Autowired
	private ModelMapper modelMapper;

	/** Servicio de auditoría. */
	@Autowired
	private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public UnidadService() {
	}

	/**
	 * Cuenta el número total de unidades.
	 *
	 * @return Número total de unidades
	 */
	@Override
	public long count() {
		return unidadRepo.count();
	}

	/**
	 * Verifica si existe una unidad con el ID especificado.
	 *
	 * @param id ID de la unidad
	 * @return true si existe, false en caso contrario
	 */
	@Override
	public boolean exist(Long id) {
		return unidadRepo.existsById(id);
	}

	/**
	 * Crea una nueva unidad en un conjunto.
	 *
	 * @param data DTO con los datos de la unidad
	 * @return 0 si la creación fue exitosa, 1 si la unidad ya existe en el
	 *         conjunto, 2 si el conjunto no existe
	 */
	@Override
	public int create(UnidadDTO data) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(data.getConjuntoId());
		if (!conjunto.isPresent()) {
			return 2;
		}
		if (unidadRepo.findByConjuntoIdAndAgrupacionIgnoreCaseAndNumeroIgnoreCase(conjunto.get().getId(),
				data.getAgrupacion().trim(), data.getNumero().trim()).isPresent()) {
			return 1;
		}
		Unidad entity = new Unidad(conjunto.get().getId(), data.getTipo(), data.getAgrupacion().trim(),
				data.getNumero().trim());
		unidadRepo.save(entity);
		auditoriaServ.registrar("CREAR", "Unidad",
				"Creó " + entity.getDescripcion() + " en " + conjunto.get().getNombre());
		return 0;
	}

	/**
	 * Obtiene todas las unidades.
	 *
	 * @return Lista de DTOs de unidades
	 */
	@Override
	public List<UnidadDTO> getAll() {
		List<UnidadDTO> dtoList = new ArrayList<>();
		unidadRepo.findAll().forEach((entity) -> dtoList.add(aDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene las unidades de un conjunto.
	 *
	 * @param conjuntoId ID del conjunto
	 * @return Lista de DTOs de unidades, o null si el conjunto no existe
	 */
	public List<UnidadDTO> getByConjunto(Long conjuntoId) {
		Optional<Conjunto> conjunto = conjuntoRepo.findById(conjuntoId);
		if (!conjunto.isPresent()) {
			return null;
		}
		List<UnidadDTO> dtoList = new ArrayList<>();
		unidadRepo.findByConjuntoIdOrderByAgrupacionAscNumeroAsc(conjuntoId)
				.forEach((entity) -> dtoList.add(aDTO(entity)));
		return dtoList;
	}

	/**
	 * Elimina una unidad. No se puede eliminar una unidad con residentes.
	 *
	 * @param id ID de la unidad
	 * @return 0 si la eliminación fue exitosa, 1 si la unidad no existe, 2 si tiene
	 *         residentes
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Unidad> found = unidadRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		if (!usuarioRepo.findByUnidadId(id).isEmpty()) {
			return 2;
		}
		unidadRepo.delete(found.get());
		auditoriaServ.registrar("ELIMINAR", "Unidad", "Eliminó " + found.get().getDescripcion());
		return 0;
	}

	/**
	 * Actualiza una unidad existente.
	 *
	 * @param id      ID de la unidad
	 * @param newData DTO con los nuevos datos
	 * @return 0 si la actualización fue exitosa, 1 si ya existe otra unidad igual
	 *         en el conjunto, 2 si la unidad no existe, 3 si el conjunto no existe
	 */
	@Override
	public int updateById(Long id, UnidadDTO newData) {
		Optional<Unidad> found = unidadRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Optional<Conjunto> conjunto = conjuntoRepo.findById(newData.getConjuntoId());
		if (!conjunto.isPresent()) {
			return 3;
		}
		Optional<Unidad> repetida = unidadRepo.findByConjuntoIdAndAgrupacionIgnoreCaseAndNumeroIgnoreCase(
				conjunto.get().getId(), newData.getAgrupacion().trim(), newData.getNumero().trim());
		if (repetida.isPresent() && !repetida.get().getId().equals(id)) {
			return 1;
		}
		Unidad temp = found.get();
		temp.setConjuntoId(conjunto.get().getId());
		temp.setTipo(newData.getTipo());
		temp.setAgrupacion(newData.getAgrupacion().trim());
		temp.setNumero(newData.getNumero().trim());
		unidadRepo.save(temp);
		auditoriaServ.registrar("ACTUALIZAR", "Unidad", "Actualizó " + temp.getDescripcion());
		return 0;
	}

	/**
	 * Obtiene una unidad por su ID.
	 *
	 * @param id ID de la unidad
	 * @return DTO de la unidad si existe, null en caso contrario
	 */
	public UnidadDTO getById(Long id) {
		Optional<Unidad> found = unidadRepo.findById(id);
		if (found.isPresent()) {
			return aDTO(found.get());
		} else {
			return null;
		}
	}

	/**
	 * Convierte una unidad en DTO con los nombres de sus residentes.
	 *
	 * @param unidad Unidad a convertir
	 * @return DTO de la unidad
	 */
	private UnidadDTO aDTO(Unidad unidad) {
		UnidadDTO dto = modelMapper.map(unidad, UnidadDTO.class);
		List<String> residentes = new ArrayList<>();
		for (Usuario usuario : usuarioRepo.findByUnidadId(unidad.getId())) {
			residentes.add(usuario.getNombreCompleto());
		}
		dto.setResidentes(residentes);
		dto.setDescripcion(unidad.getDescripcion());
		return dto;
	}
}
