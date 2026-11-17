package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Asistencia;

/**
 * Repositorio para la entidad Asistencia.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y métodos para buscar las asistencias por evento y por residente.
 */
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

	/**
	 * Busca las asistencias confirmadas a un evento.
	 *
	 * @param eventoId Identificador del evento
	 * @return Lista de asistencias del evento
	 */
	public List<Asistencia> findByEventoId(Long eventoId);

	/**
	 * Busca las asistencias confirmadas por un residente.
	 *
	 * @param usuarioId Identificador del residente
	 * @return Lista de asistencias del residente
	 */
	public List<Asistencia> findByUsuarioId(Long usuarioId);

	/**
	 * Busca la asistencia de un residente a un evento.
	 *
	 * @param eventoId Identificador del evento
	 * @param usuarioId Identificador del residente
	 * @return Un Optional con la asistencia si el residente está inscrito
	 */
	public Optional<Asistencia> findByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);

	/**
	 * Cuenta los residentes inscritos a un evento.
	 *
	 * @param eventoId Identificador del evento
	 * @return Número de inscritos
	 */
	public long countByEventoId(Long eventoId);
}
