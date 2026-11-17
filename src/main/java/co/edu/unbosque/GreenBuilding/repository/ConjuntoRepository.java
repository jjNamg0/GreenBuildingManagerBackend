package co.edu.unbosque.GreenBuilding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.GreenBuilding.model.Conjunto;

/**
 * Repositorio para la entidad Conjunto.
 * Proporciona operaciones CRUD básicas heredadas de JpaRepository
 * y un método para buscar conjuntos por nombre y municipio.
 */
public interface ConjuntoRepository extends JpaRepository<Conjunto, Long> {

	/**
	 * Busca un conjunto por su nombre y municipio, sin importar mayúsculas.
	 *
	 * @param nombre El nombre del conjunto
	 * @param municipio El municipio
	 * @return Un Optional con el conjunto si existe
	 */
	public Optional<Conjunto> findByNombreIgnoreCaseAndMunicipioIgnoreCase(String nombre, String municipio);

	/**
	 * Busca todos los conjuntos ordenados por nombre.
	 *
	 * @return Lista de conjuntos
	 */
	public List<Conjunto> findAllByOrderByNombreAsc();
}
