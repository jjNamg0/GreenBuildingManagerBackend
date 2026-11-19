package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Invitado;

/**
 * Repositorio para la entidad Invitado. Proporciona operaciones CRUD básicas
 * heredadas de JpaRepository y métodos para buscar los invitados de un evento o
 * de una reserva.
 */
public interface InvitadoRepository extends JpaRepository<Invitado, Long> {

	/**
	 * Busca los invitados de un evento, del envío más reciente al más antiguo.
	 *
	 * @param eventoId Identificador del evento
	 * @return Lista de invitados del evento
	 */
	public List<Invitado> findByEventoIdOrderByFechaEnvioDesc(Long eventoId);

	/**
	 * Busca los invitados de una reserva, del envío más reciente al más antiguo.
	 *
	 * @param reservaId Identificador de la reserva
	 * @return Lista de invitados de la reserva
	 */
	public List<Invitado> findByReservaIdOrderByFechaEnvioDesc(Long reservaId);

	/**
	 * Busca un correo entre los invitados de un evento.
	 *
	 * @param eventoId Identificador del evento
	 * @param correo   Correo del invitado
	 * @return Un Optional con el invitado si ya fue invitado
	 */
	public Optional<Invitado> findByEventoIdAndCorreo(Long eventoId, String correo);

	/**
	 * Busca un correo entre los invitados de una reserva.
	 *
	 * @param reservaId Identificador de la reserva
	 * @param correo    Correo del invitado
	 * @return Un Optional con el invitado si ya fue invitado
	 */
	public Optional<Invitado> findByReservaIdAndCorreo(Long reservaId, String correo);
}
