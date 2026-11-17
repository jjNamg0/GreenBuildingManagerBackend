package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.RegistroAuditoria;

/**
 * Repositorio para la entidad RegistroAuditoria.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y un método para buscar los registros del más reciente al más antiguo.
 */
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

	/**
	 * Busca todos los registros de auditoría, del más reciente al más antiguo.
	 *
	 * @return Lista de registros
	 */
	public List<RegistroAuditoria> findAllByOrderByFechaDescIdDesc();
}
