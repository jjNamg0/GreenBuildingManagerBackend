package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Espacio;

/**
 * Repositorio para la entidad Espacio.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y métodos para buscar los espacios de un conjunto.
 */
public interface EspacioRepository extends JpaRepository<Espacio, Long> {

	/**
	 * Busca los espacios de un conjunto ordenados por nombre.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Lista de espacios
	 */
	public List<Espacio> findByConjuntoIdOrderByNombreAsc(Long conjuntoId);

	/**
	 * Cuenta los espacios de un conjunto.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Número de espacios
	 */
	public long countByConjuntoId(Long conjuntoId);
}
