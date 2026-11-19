package co.edu.unbosque.GreenBuilding.repository;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Evento;
import co.edu.unbosque.GreenBuilding.model.Evento.Estado;

/**
 * Repositorio para la entidad Evento.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y métodos para buscar eventos por conjunto y espacio.
 */
public interface EventoRepository extends JpaRepository<Evento, Long> {

	/**
	 * Busca los eventos de un conjunto del más próximo al más lejano.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Lista de eventos
	 */
	public List<Evento> findByConjuntoIdOrderByFechaAscHoraInicioAsc(Long conjuntoId);

	/**
	 * Busca los eventos de un conjunto del más reciente al más antiguo.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Lista de eventos
	 */
	public List<Evento> findByConjuntoIdOrderByFechaDescHoraInicioDesc(Long conjuntoId);

	/**
	 * Busca todos los eventos del más reciente al más antiguo.
	 *
	 * @return Lista de eventos
	 */
	public List<Evento> findAllByOrderByFechaDescHoraInicioDesc();

	/**
	 * Busca los eventos que usan un espacio.
	 *
	 * @param espacioId Identificador del espacio
	 * @return Lista de eventos
	 */
	public List<Evento> findByEspacioId(Long espacioId);

	/**
	 * Cuenta los eventos con un estado desde una fecha.
	 *
	 * @param estado El estado
	 * @param fecha La fecha mínima
	 * @return Número de eventos
	 */
	public long countByEstadoAndFechaGreaterThanEqual(Estado estado, LocalDate fecha);

	/**
	 * Busca los eventos de un espacio en un día.
	 *
	 * @param espacioId Identificador del espacio
	 * @param fecha Día a revisar
	 * @return Lista de eventos del día en ese espacio
	 */
	public List<Evento> findByEspacioIdAndFecha(Long espacioId, LocalDate fecha);

	/**
	 * Busca un evento por el código de su enlace de invitación.
	 *
	 * @param codigoInvitacion Código del enlace
	 * @return Un Optional con el evento si existe
	 */
	public Optional<Evento> findByCodigoInvitacion(String codigoInvitacion);
}
