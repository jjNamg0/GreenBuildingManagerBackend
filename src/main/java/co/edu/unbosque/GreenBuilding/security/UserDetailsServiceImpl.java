package co.edu.unbosque.GreenBuilding.security;

import co.edu.unbosque.GreenBuilding.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio UserDetailsService de Spring Security. Esta clase se encarga de
 * cargar los detalles del usuario desde la base de datos para la autenticación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/** Repositorio para acceder a los datos de usuarios. */
	private final UsuarioRepository usuarioRepository;

	/**
	 * Constructor que inicializa el servicio con el repositorio de usuarios.
	 *
	 * @param usuarioRepository Repositorio para acceder a los datos de usuarios
	 */
	public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	/**
	 * Carga un usuario por su nombre de usuario (correo).
	 *
	 * @param username Nombre de usuario a buscar
	 * @return Detalles del usuario encontrado
	 * @throws UsernameNotFoundException Si el usuario no existe en la base de datos
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepository
				.findByUsername(username)
				.orElseThrow(
						() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
	}
}
