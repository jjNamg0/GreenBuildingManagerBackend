package co.edu.unbosque.GreenBuilding.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion;
import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;

/**
 * Repositorio para la entidad CodigoVerificacion.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y métodos para buscar y eliminar los códigos de un usuario.
 */
public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {

	/**
	 * Busca el último código de un usuario para un propósito.
	 *
	 * @param username El correo del usuario
	 * @param proposito El propósito del código
	 * @return Un Optional con el código más reciente
	 */
	public Optional<CodigoVerificacion> findFirstByUsernameAndPropositoOrderByIdDesc(String username,
			Proposito proposito);

	/**
	 * Elimina los códigos de un usuario para un propósito.
	 *
	 * @param username El correo del usuario
	 * @param proposito El propósito del código
	 */
	public void deleteByUsernameAndProposito(String username, Proposito proposito);

	/**
	 * Elimina todos los códigos de un usuario.
	 *
	 * @param username El correo del usuario
	 */
	public void deleteByUsername(String username);
}
