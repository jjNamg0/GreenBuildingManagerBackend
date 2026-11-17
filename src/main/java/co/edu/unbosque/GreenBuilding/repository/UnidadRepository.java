package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Unidad;

/**
 * Repositorio para la entidad Unidad.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y métodos para buscar las unidades de un conjunto.
 */
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

	/**
	 * Busca las unidades de un conjunto ordenadas por agrupación y número.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Lista de unidades
	 */
	public List<Unidad> findByConjuntoIdOrderByAgrupacionAscNumeroAsc(Long conjuntoId);

	/**
	 * Busca una unidad por conjunto, agrupación y número, sin importar mayúsculas.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @param agrupacion La torre o manzana
	 * @param numero El número
	 * @return Un Optional con la unidad si existe
	 */
	public Optional<Unidad> findByConjuntoIdAndAgrupacionIgnoreCaseAndNumeroIgnoreCase(Long conjuntoId,
			String agrupacion, String numero);

	/**
	 * Cuenta las unidades de un conjunto.
	 *
	 * @param conjuntoId Identificador del conjunto
	 * @return Número de unidades
	 */
	public long countByConjuntoId(Long conjuntoId);
}
