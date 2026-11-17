package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.model.Usuario.Role;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y métodos personalizados para buscar usuarios por correo, conjunto y unidad.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/**
	 * Busca un usuario por su nombre de usuario (correo).
	 *
	 * @param username El correo a buscar
	 * @return Un Optional que contiene el usuario si existe, o vacío si no existe
	 */
	public Optional<Usuario> findByUsername(String username);

	/**
	 * Elimina un usuario por su nombre de usuario.
	 *
	 * @param username El correo del usuario a eliminar
	 */
	public void deleteByUsername(String username);

	/**
	 * Busca los usuarios asignados a una unidad.
	 *
	 * @param unidadId Identificador de la unidad
	 * @return Lista de residentes de la unidad
	 */
	public List<Usuario> findByUnidadId(Long unidadId);

	/**
	 * Busca los usuarios de un conjunto.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Lista de usuarios del conjunto
	 */
	public List<Usuario> findByConjuntoId(Long conjuntoId);

	/**
	 * Busca los residentes activos y verificados de un conjunto que ya tienen unidad.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Lista de residentes para enviar invitaciones
	 */
	public List<Usuario> findByConjuntoIdAndUnidadIdIsNotNullAndActivoTrueAndVerificadoTrue(Long conjuntoId);

	/**
	 * Cuenta los usuarios que tienen unidad asignada.
	 *
	 * @return Número de residentes
	 */
	public long countByUnidadIdIsNotNull();

	/**
	 * Cuenta los usuarios activos de un rol que aún no tienen unidad.
	 *
	 * @param role El rol
	 * @return Número de usuarios sin unidad
	 */
	public long countByRoleAndUnidadIdIsNullAndActivoTrue(Role role);

	/**
	 * Busca todos los usuarios, del registrado más recientemente al más antiguo.
	 *
	 * @return Lista de usuarios
	 */
	public List<Usuario> findAllByOrderByFechaRegistroDesc();
}
