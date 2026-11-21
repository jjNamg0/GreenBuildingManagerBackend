package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.dto.ConjuntoDTO;
import co.edu.unbosque.GreenBuilding.model.Conjunto;
import co.edu.unbosque.GreenBuilding.model.Asistencia;
import co.edu.unbosque.GreenBuilding.model.Espacio;
import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Unidad;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.repository.AsistenciaRepository;
import co.edu.unbosque.GreenBuilding.repository.ConjuntoRepository;
import co.edu.unbosque.GreenBuilding.repository.EspacioRepository;
import co.edu.unbosque.GreenBuilding.repository.EventoRepository;
import co.edu.unbosque.GreenBuilding.repository.ReservaRepository;
import co.edu.unbosque.GreenBuilding.repository.UnidadRepository;
import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona operaciones CRUD para los conjuntos residenciales de Cundinamarca.
 * Implementa la interfaz CRUDOperation.
 */
@Service
public class ConjuntoService implements CRUDOperation<ConjuntoDTO> {

	/** Latitud mínima de Cundinamarca (los mismos límites del mapa del front). */
	private static final double LATITUD_MINIMA = 3.7;

	/** Latitud máxima de Cundinamarca. */
	private static final double LATITUD_MAXIMA = 5.85;

	/** Longitud mínima de Cundinamarca. */
	private static final double LONGITUD_MINIMA = -74.9;

	/** Longitud máxima de Cundinamarca. */
	private static final double LONGITUD_MAXIMA = -73.0;

	/** Repositorio para acceder a los conjuntos. */
	@Autowired private ConjuntoRepository conjuntoRepo;

	/** Repositorio para acceder a las unidades. */
	@Autowired private UnidadRepository unidadRepo;

	/** Repositorio para acceder a los espacios. */
	@Autowired private EspacioRepository espacioRepo;

	/** Repositorio para acceder a los eventos. */
	@Autowired private EventoRepository eventoRepo;

	/** Repositorio para acceder a las reservas. */
	@Autowired private ReservaRepository reservaRepo;

	/** Repositorio para acceder a los usuarios. */
	@Autowired private UsuarioRepository usuarioRepo;

	/** Repositorio para acceder a las asistencias a eventos. */
	@Autowired private AsistenciaRepository asistenciaRepo;

	/** Mapeador para convertir entre entidades y DTOs. */
	@Autowired private ModelMapper modelMapper;

	/** Servicio de auditoría. */
	@Autowired private AuditoriaService auditoriaServ;

	/** Constructor por defecto. */
	public ConjuntoService() {}

	/**
	 * Cuenta el número total de conjuntos.
	 *
	 * @return Número total de conjuntos
	 */
	@Override
	public long count() {
		return conjuntoRepo.count();
	}

	/**
	 * Verifica si existe un conjunto con el ID especificado.
	 *
	 * @param id ID del conjunto
	 * @return true si existe, false en caso contrario
	 */
	@Override
	public boolean exist(Long id) {
		return conjuntoRepo.existsById(id);
	}

	/**
	 * Crea un nuevo conjunto.
	 *
	 * @param data DTO con los datos del conjunto
	 * @return 0 si la creación fue exitosa, 1 si ya existe un conjunto con ese nombre en el mismo
	 *     municipio, 2 si la ubicación está fuera de Cundinamarca
	 */
	@Override
	public int create(ConjuntoDTO data) {
		if (!dentroDeCundinamarca(data)) {
			return 2;
		}
		if (conjuntoRepo
				.findByNombreIgnoreCaseAndMunicipioIgnoreCase(data.getNombre().trim(), data.getMunicipio().trim())
				.isPresent()) {
			return 1;
		}
		Conjunto entity = modelMapper.map(data, Conjunto.class);
		entity.setId(null);
		limpiar(entity);
		conjuntoRepo.save(entity);
		auditoriaServ.registrar("CREAR", "Conjunto", "Creó el conjunto " + entity.getNombre());
		return 0;
	}

	/**
	 * Obtiene todos los conjuntos ordenados por nombre.
	 *
	 * @return Lista de DTOs de conjuntos
	 */
	@Override
	public List<ConjuntoDTO> getAll() {
		return getAll(null, null);
	}

	/**
	 * Obtiene los conjuntos que coinciden con una búsqueda y un tipo.
	 *
	 * @param busqueda Texto a buscar en el nombre, municipio o dirección (opcional)
	 * @param tipo APARTAMENTOS o CASAS (opcional)
	 * @return Lista de DTOs de conjuntos
	 */
	public List<ConjuntoDTO> getAll(String busqueda, String tipo) {
		List<ConjuntoDTO> dtoList = new ArrayList<>();
		for (Conjunto conjunto : conjuntoRepo.findAllByOrderByNombreAsc()) {
			boolean delTipo = TextoUtil.vacio(tipo) || conjunto.getTipo().name().equals(tipo.trim().toUpperCase());
			boolean coincide =
					TextoUtil.vacio(busqueda)
							|| TextoUtil.contiene(conjunto.getNombre(), busqueda)
							|| TextoUtil.contiene(conjunto.getMunicipio(), busqueda)
							|| TextoUtil.contiene(conjunto.getDireccion(), busqueda);
			if (delTipo && coincide) {
				dtoList.add(aDTO(conjunto));
			}
		}
		return dtoList;
	}

	/**
	 * Elimina un conjunto con sus unidades, espacios, eventos y reservas. Los usuarios del conjunto
	 * quedan sin conjunto ni unidad.
	 *
	 * @param id ID del conjunto a eliminar
	 * @return 0 si la eliminación fue exitosa, 1 si el conjunto no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Conjunto> found = conjuntoRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Conjunto conjunto = found.get();
		for (Usuario usuario : usuarioRepo.findByConjuntoId(id)) {
			usuario.setUnidadId(null);
			usuario.setConjuntoId(null);
		}
		for (Evento evento : eventoRepo.findByConjuntoIdOrderByFechaAscHoraInicioAsc(id)) {
			for (Asistencia asistencia : asistenciaRepo.findByEventoId(evento.getId())) {
				asistenciaRepo.delete(asistencia);
			}
			eventoRepo.delete(evento);
		}
		for (Espacio espacio : espacioRepo.findByConjuntoIdOrderByNombreAsc(id)) {
			for (Reserva reserva : reservaRepo.findByEspacioId(espacio.getId())) {
				reservaRepo.delete(reserva);
			}
			espacioRepo.delete(espacio);
		}
		for (Unidad unidad : unidadRepo.findByConjuntoIdOrderByAgrupacionAscNumeroAsc(id)) {
			unidadRepo.delete(unidad);
		}
		conjuntoRepo.delete(conjunto);
		auditoriaServ.registrar("ELIMINAR", "Conjunto", "Eliminó el conjunto " + conjunto.getNombre());
		return 0;
	}

	/**
	 * Actualiza un conjunto existente.
	 *
	 * @param id ID del conjunto a actualizar
	 * @param newData DTO con los nuevos datos
	 * @return 0 si la actualización fue exitosa, 1 si ya existe otro conjunto con ese nombre en el
	 *     mismo municipio, 2 si el conjunto no existe, 3 si la ubicación está fuera de Cundinamarca
	 */
	@Override
	public int updateById(Long id, ConjuntoDTO newData) {
		Optional<Conjunto> found = conjuntoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		if (!dentroDeCundinamarca(newData)) {
			return 3;
		}
		Optional<Conjunto> repetido =
				conjuntoRepo.findByNombreIgnoreCaseAndMunicipioIgnoreCase(
						newData.getNombre().trim(), newData.getMunicipio().trim());
		if (repetido.isPresent() && !repetido.get().getId().equals(id)) {
			return 1;
		}
		Conjunto temp = found.get();
		temp.setNombre(newData.getNombre());
		temp.setTipo(newData.getTipo());
		temp.setDireccion(newData.getDireccion());
		temp.setMunicipio(newData.getMunicipio());
		temp.setLatitud(newData.getLatitud());
		temp.setLongitud(newData.getLongitud());
		temp.setDescripcion(newData.getDescripcion());
		temp.setImagenUrl(newData.getImagenUrl());
		limpiar(temp);
		conjuntoRepo.save(temp);
		auditoriaServ.registrar("ACTUALIZAR", "Conjunto", "Actualizó el conjunto " + temp.getNombre());
		return 0;
	}

	/**
	 * Obtiene un conjunto por su ID.
	 *
	 * @param id ID del conjunto
	 * @return DTO del conjunto si existe, null en caso contrario
	 */
	public ConjuntoDTO getById(Long id) {
		Optional<Conjunto> found = conjuntoRepo.findById(id);
		if (found.isPresent()) {
			return aDTO(found.get());
		} else {
			return null;
		}
	}

	/**
	 * Convierte un conjunto en DTO con el total de unidades y espacios.
	 *
	 * @param conjunto Conjunto a convertir
	 * @return DTO del conjunto
	 */
	public ConjuntoDTO aDTO(Conjunto conjunto) {
		ConjuntoDTO dto = modelMapper.map(conjunto, ConjuntoDTO.class);
		dto.setTotalUnidades(unidadRepo.countByConjuntoId(conjunto.getId()));
		dto.setTotalEspacios(espacioRepo.countByConjuntoId(conjunto.getId()));
		return dto;
	}

	/**
	 * Indica si la ubicación del conjunto está dentro de Cundinamarca.
	 *
	 * @param data DTO con la latitud y la longitud
	 * @return true si está dentro
	 */
	private boolean dentroDeCundinamarca(ConjuntoDTO data) {
		return data.getLatitud() >= LATITUD_MINIMA
				&& data.getLatitud() <= LATITUD_MAXIMA
				&& data.getLongitud() >= LONGITUD_MINIMA
				&& data.getLongitud() <= LONGITUD_MAXIMA;
	}

	/**
	 * Quita los espacios sobrantes de los textos del conjunto.
	 *
	 * @param conjunto Conjunto a limpiar
	 */
	private void limpiar(Conjunto conjunto) {
		conjunto.setNombre(TextoUtil.limpiar(conjunto.getNombre()));
		conjunto.setDireccion(TextoUtil.limpiar(conjunto.getDireccion()));
		conjunto.setMunicipio(TextoUtil.limpiar(conjunto.getMunicipio()));
		conjunto.setDescripcion(TextoUtil.limpiar(conjunto.getDescripcion()));
		conjunto.setImagenUrl(TextoUtil.opcional(conjunto.getImagenUrl()));
	}
}
