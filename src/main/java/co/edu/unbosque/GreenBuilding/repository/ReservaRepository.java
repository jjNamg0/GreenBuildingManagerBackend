package co.edu.unbosque.GreenBuilding.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Reserva;
import co.edu.unbosque.GreenBuilding.model.Reserva.Estado;

/**
 * Repositorio para la entidad Reserva. Proporciona operaciones CRUD básicas
 * heredadas de JpaRepository y métodos para buscar reservas por usuario,
 * espacio, fecha y estado.
 */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	/**
	 * Busca las reservas de un usuario, de la más reciente a la más antigua.
	 *
	 * @param usuarioId Identificador del usuario
	 * @return Lista de reservas
	 */
	public List<Reserva> findByUsuarioIdOrderByFechaDescHoraInicioDesc(Long usuarioId);

	/**
	 * Busca todas las reservas, de la más reciente a la más antigua.
	 *
	 * @return Lista de reservas
	 */
	public List<Reserva> findAllByOrderByFechaDescHoraInicioDesc();

	/**
	 * Busca las reservas con un estado, de la más reciente a la más antigua.
	 *
	 * @param estado El estado
	 * @return Lista de reservas
	 */
	public List<Reserva> findByEstadoOrderByFechaDescHoraInicioDesc(Estado estado);

	/**
	 * Busca las reservas de un espacio en una fecha.
	 *
	 * @param espacioId Identificador del espacio
	 * @param fecha     La fecha
	 * @return Lista de reservas del día
	 */
	public List<Reserva> findByEspacioIdAndFecha(Long espacioId, LocalDate fecha);

	/**
	 * Busca todas las reservas de un espacio.
	 *
	 * @param espacioId Identificador del espacio
	 * @return Lista de reservas
	 */
	public List<Reserva> findByEspacioId(Long espacioId);

	/**
	 * Busca todas las reservas de un usuario.
	 *
	 * @param usuarioId Identificador del usuario
	 * @return Lista de reservas
	 */
	public List<Reserva> findByUsuarioId(Long usuarioId);

	/**
	 * Cuenta las reservas con un estado.
	 *
	 * @param estado El estado
	 * @return Número de reservas
	 */
	public long countByEstado(Estado estado);

	/**
	 * Busca una reserva por el código de su enlace de invitación.
	 *
	 * @param codigoInvitacion Código del enlace
	 * @return Un Optional con la reserva si existe
	 */
	public Optional<Reserva> findByCodigoInvitacion(String codigoInvitacion);
}
