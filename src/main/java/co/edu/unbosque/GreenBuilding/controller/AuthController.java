package co.edu.unbosque.GreenBuilding.controller;

import co.edu.unbosque.GreenBuilding.dto.CodigoDTO;
import co.edu.unbosque.GreenBuilding.dto.UsuarioDTO;
import co.edu.unbosque.GreenBuilding.exception.EmailNotValidException;
import co.edu.unbosque.GreenBuilding.exception.EmptyFieldException;
import co.edu.unbosque.GreenBuilding.exception.InvalidCharactersException;
import co.edu.unbosque.GreenBuilding.exception.LengthNotValidException;
import co.edu.unbosque.GreenBuilding.exception.OptionNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PasswordNotValidException;
import co.edu.unbosque.GreenBuilding.exception.PhoneNotValidException;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.security.JwtUtil;
import co.edu.unbosque.GreenBuilding.service.AuditoriaService;
import co.edu.unbosque.GreenBuilding.service.UsuarioService;
import co.edu.unbosque.GreenBuilding.util.ExceptionChecker;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la autenticación de usuarios. Proporciona endpoints públicos para iniciar
 * sesión, registrarse, verificar el correo y recuperar la contraseña.
 *
 * @author Daniel Montoya
 * @version 1.1
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@Transactional
@Tag(
		name = "Autenticación",
		description = "API para autenticación de usuarios (login, registro, verificación y contraseña)")
public class AuthController {

	/** Mensaje de credenciales incorrectas. */
	private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos.";

	/** Mensaje de inicio de sesión bloqueado por demasiados intentos fallidos. */
	private static final String LOGIN_BLOQUEADO =
			"Superaste el número de intentos. Espera "
					+ UsuarioService.MINUTOS_BLOQUEO_LOGIN
					+ " minutos e intenta de nuevo.";

	/** Mensaje de solicitud de código procesada, sin revelar si el correo existe. */
	private static final String CODIGO_ENVIADO =
			"Si el correo está registrado, te enviamos un código. Revisa tu bandeja de entrada.";

	/** Gestor de autenticación de Spring Security. */
	private final AuthenticationManager authenticationManager;

	/** Utilidad para generar tokens JWT. */
	private final JwtUtil jwtUtil;

	/** Servicio de usuarios. */
	private final UsuarioService usuarioServ;

	/** Servicio de auditoría. */
	private final AuditoriaService auditoriaServ;

	/**
	 * Constructor que inicializa las dependencias necesarias para el controlador, igual que el
	 * AuthController del proyecto de clase.
	 *
	 * @param authenticationManager Gestor de autenticación
	 * @param jwtUtil Utilidad para tokens JWT
	 * @param usuarioServ Servicio de usuarios
	 * @param auditoriaServ Servicio de auditoría
	 */
	public AuthController(
			AuthenticationManager authenticationManager,
			JwtUtil jwtUtil,
			UsuarioService usuarioServ,
			AuditoriaService auditoriaServ) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.usuarioServ = usuarioServ;
		this.auditoriaServ = auditoriaServ;
	}

	/**
	 * Inicia sesión con correo y contraseña.
	 *
	 * @param loginRequest DTO con el correo y la contraseña
	 * @return ResponseEntity con el token JWT y el rol, o un mensaje de error
	 */
	@Operation(
			summary = "Iniciar sesión",
			description =
					"""
			Este endpoint permite a los usuarios iniciar sesión con su correo y contraseña.

			**¿Qué hace?** Valida las credenciales y devuelve un token JWT con el rol del usuario.

			**Posibles resultados:**

			* Token y rol del usuario
			* Correo o contraseña incorrectos (401)
			* Cuenta sin verificar (403): el front lleva a la pantalla del código
			* Cuenta desactivada (401)
			* Acceso bloqueado por 15 minutos después de 5 intentos fallidos seguidos (429)

			**Uso del token:** inclúyelo en el encabezado `Authorization: Bearer tu_token_jwt`.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "200",
						description = "Inicio de sesión exitoso",
						content =
								@Content(
										mediaType = "application/json",
										examples =
												@ExampleObject(
														value =
																"""
						{
						  "token": "eyJhbGciOiJIUzI1NiJ9...",
						  "role": "USER"
						}
					"""))),
				@ApiResponse(
						responseCode = "401",
						description = "Credenciales incorrectas o cuenta desactivada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Correo o contraseña incorrectos."))),
				@ApiResponse(
						responseCode = "403",
						description = "Cuenta sin verificar",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(
														value =
																"Tu cuenta aún no está verificada. Escribe el código que te"
																		+ " enviamos al correo."))),
				@ApiResponse(
						responseCode = "429",
						description = "Demasiados intentos fallidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(
														value =
																"Superaste el número de intentos. Espera 15 minutos e intenta"
																		+ " de nuevo.")))
			})
	@PostMapping("/login")
	public ResponseEntity<?> login(
			@Parameter(
							description = "Credenciales del usuario",
							required = true,
							schema = @Schema(implementation = UsuarioDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "username": "residente@greenbuilding.com",
					  "password": "Residente123*"
					}
				"""))
					@RequestBody
					UsuarioDTO loginRequest) {
		String username = TextoUtil.correo(loginRequest.getUsername());
		String password = "";
		if (loginRequest.getPassword() != null) {
			password = loginRequest.getPassword();
		}
		if (usuarioServ.loginBloqueado(username)) {
			auditoriaServ.registrar(
					username, "LOGIN", "Usuario", "Intento de inicio de sesión con el acceso bloqueado");
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(LOGIN_BLOQUEADO);
		}
		try {
			Authentication authentication =
					authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(username, password));
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			usuarioServ.reiniciarIntentos(userDetails.getUsername());
			String jwt = jwtUtil.generateToken(userDetails);
			String role = null;
			if (userDetails instanceof Usuario) {
				Usuario usuario = (Usuario) userDetails;
				role = usuario.getRole().name();
			}
			auditoriaServ.registrar(userDetails.getUsername(), "LOGIN", "Usuario", "Inicio de sesión");
			return ResponseEntity.ok(new AuthResponse(jwt, role));
		} catch (LockedException e) {
			if (usuarioServ.validateCredentials(username, password) == 0) {
				auditoriaServ.registrar(
						username, "LOGIN", "Usuario", "Intento de inicio de sesión con cuenta desactivada");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(
								"Tu cuenta está desactivada. Comunícate con la administración de tu conjunto.");
			}
			return intentoFallido(username);
		} catch (DisabledException e) {
			if (usuarioServ.validateCredentials(username, password) == 0) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(
								"Tu cuenta aún no está verificada. Escribe el código que te enviamos al correo.");
			}
			return intentoFallido(username);
		} catch (AuthenticationException e) {
			auditoriaServ.registrar(null, "LOGIN", "Usuario", "Intento fallido con " + username);
			return intentoFallido(username);
		}
	}

	/**
	 * Suma un intento fallido de inicio de sesión y arma la respuesta. Si el usuario llegó al máximo
	 * de intentos, avisa que el acceso quedó bloqueado.
	 *
	 * @param username Correo con el que se intentó iniciar sesión
	 * @return ResponseEntity con el mensaje de credenciales incorrectas o de acceso bloqueado
	 */
	private ResponseEntity<?> intentoFallido(String username) {
		int status = usuarioServ.registrarIntentoFallido(username);
		if (status == 1) {
			auditoriaServ.registrar(
					username, "LOGIN", "Usuario", "Inicio de sesión bloqueado por intentos fallidos");
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(LOGIN_BLOQUEADO);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CREDENCIALES_INVALIDAS);
	}

	/**
	 * Registra un nuevo residente y le envía el código de verificación al correo.
	 *
	 * @param registerRequest DTO con los datos del nuevo residente
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Registrar usuario",
			description =
					"""
			Este endpoint permite crear una nueva cuenta de residente.

			**¿Qué hace?** Crea la cuenta sin verificar, con rol USER, y envía un código de 6 dígitos al correo.
			El administrador asigna después la unidad.

			**Posibles resultados:**

			* Usuario registrado exitosamente
			* El correo ya está registrado (409)
			* El conjunto no existe (404)
			* Datos inválidos (400)
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "201",
						description = "Usuario registrado exitosamente",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(
														value =
																"Te registraste. Revisa tu correo: te enviamos un código de"
																		+ " verificación."))),
				@ApiResponse(
						responseCode = "409",
						description = "El correo ya está registrado",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Ya existe una cuenta con ese correo."))),
				@ApiResponse(
						responseCode = "400",
						description = "Datos inválidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Escribe un correo electrónico válido.")))
			})
	@PostMapping("/register")
	public ResponseEntity<?> register(
			@Parameter(
							description = "Información del nuevo residente",
							required = true,
							schema = @Schema(implementation = UsuarioDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "nombre": "Ana",
					  "apellido": "Pérez",
					  "username": "ana@correo.com",
					  "telefono": "3001234567",
					  "password": "Segura123*",
					  "conjuntoId": 1,
					  "unidadSolicitada": "Torre 2 - 501"
					}
				"""))
					@RequestBody
					UsuarioDTO registerRequest) {
		try {
			ExceptionChecker.checkEmpty("nombre", registerRequest.getNombre());
			ExceptionChecker.checkEmpty("apellido", registerRequest.getApellido());
			ExceptionChecker.checkEmpty("teléfono", registerRequest.getTelefono());
			ExceptionChecker.checkEmpty("unidad solicitada", registerRequest.getUnidadSolicitada());
			ExceptionChecker.checkCharacters(registerRequest.getNombre());
			ExceptionChecker.checkCharacters(registerRequest.getApellido());
			ExceptionChecker.checkCharacters(registerRequest.getUnidadSolicitada());
			ExceptionChecker.checkLength("nombre", registerRequest.getNombre(), ExceptionChecker.MAX_TEXTO);
			ExceptionChecker.checkLength(
					"apellido", registerRequest.getApellido(), ExceptionChecker.MAX_TEXTO);
			ExceptionChecker.checkLength(
					"unidad solicitada", registerRequest.getUnidadSolicitada(), ExceptionChecker.MAX_TEXTO);
			ExceptionChecker.checkPhone(registerRequest.getTelefono());
			ExceptionChecker.checkEmail(registerRequest.getUsername());
			ExceptionChecker.checkPassword(registerRequest.getPassword());
			ExceptionChecker.checkSelected("Elige tu conjunto.", registerRequest.getConjuntoId());
		} catch (EmptyFieldException
				| InvalidCharactersException
				| LengthNotValidException
				| PhoneNotValidException
				| EmailNotValidException
				| PasswordNotValidException
				| OptionNotValidException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		int result = usuarioServ.create(registerRequest);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Te registraste. Revisa tu correo: te enviamos un código de verificación.");
		} else if (result == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una cuenta con ese correo.");
		} else if (result == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El conjunto elegido no existe.");
		} else {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(
							"Te registraste, pero no pudimos enviar el código. Usa la opción de reenviar código.");
		}
	}

	/**
	 * Verifica la cuenta con el código enviado al correo.
	 *
	 * @param request DTO con el correo y el código
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Verificar cuenta",
			description =
					"""
			Este endpoint permite verificar el correo con el código de 6 dígitos.

			**¿Qué hace?** Si el código es correcto, la cuenta queda verificada y ya se puede iniciar sesión.
			El código vence en 15 minutos y admite 5 intentos.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Cuenta verificada",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(
														value = "Tu cuenta quedó verificada. Ya puedes iniciar sesión."))),
				@ApiResponse(
						responseCode = "400",
						description = "Código incorrecto, vencido o sin intentos",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "El código no es correcto.")))
			})
	@PostMapping("/verify")
	public ResponseEntity<String> verify(
			@Parameter(
							description = "Correo y código",
							required = true,
							schema = @Schema(implementation = CodigoDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "username": "ana@correo.com",
					  "codigo": "123456"
					}
				"""))
					@RequestBody
					CodigoDTO request) {
		try {
			ExceptionChecker.checkEmpty("correo", request.getUsername());
			ExceptionChecker.checkEmpty("código", request.getCodigo());
		} catch (EmptyFieldException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = usuarioServ.verificar(request);
		if (status == 0) {
			return new ResponseEntity<>(
					"Tu cuenta quedó verificada. Ya puedes iniciar sesión.", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>(
					"Tu cuenta ya estaba verificada. Ya puedes iniciar sesión.", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(mensajeCodigo(status), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Reenvía el código de verificación al correo.
	 *
	 * @param request DTO con el correo
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Reenviar código de verificación",
			description =
					"""
			Este endpoint permite pedir un código de verificación nuevo.

			**Nota:** la respuesta es la misma aunque el correo no exista, para no revelar qué correos están registrados.
			Hay que esperar un minuto entre cada envío.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Solicitud procesada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = CODIGO_ENVIADO))),
				@ApiResponse(
						responseCode = "429",
						description = "Debe esperar un minuto",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Espera un minuto antes de pedir otro código.")))
			})
	@PostMapping("/resendcode")
	public ResponseEntity<String> resendCode(
			@Parameter(
							description = "Correo del usuario",
							required = true,
							schema = @Schema(implementation = CodigoDTO.class),
							examples = @ExampleObject(value = "{ \"username\": \"ana@correo.com\" }"))
					@RequestBody
					CodigoDTO request) {
		return respuestaEnvioCodigo(usuarioServ.reenviarCodigo(request.getUsername()));
	}

	/**
	 * Envía el código para restablecer la contraseña.
	 *
	 * @param request DTO con el correo
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Olvidé mi contraseña",
			description =
					"""
			Este endpoint envía un código para restablecer la contraseña.

			**Nota:** la respuesta es la misma aunque el correo no exista, para no revelar qué correos están registrados.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Solicitud procesada",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = CODIGO_ENVIADO))),
				@ApiResponse(
						responseCode = "429",
						description = "Debe esperar un minuto",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "Espera un minuto antes de pedir otro código.")))
			})
	@PostMapping("/forgotpassword")
	public ResponseEntity<String> forgotPassword(
			@Parameter(
							description = "Correo del usuario",
							required = true,
							schema = @Schema(implementation = CodigoDTO.class),
							examples = @ExampleObject(value = "{ \"username\": \"ana@correo.com\" }"))
					@RequestBody
					CodigoDTO request) {
		return respuestaEnvioCodigo(usuarioServ.olvidePassword(request.getUsername()));
	}

	/**
	 * Restablece la contraseña con el código enviado al correo.
	 *
	 * @param request DTO con el correo, el código y la nueva contraseña
	 * @return ResponseEntity con mensaje de éxito o error
	 */
	@Operation(
			summary = "Restablecer contraseña",
			description =
					"""
			Este endpoint cambia la contraseña con el código recibido en el correo.

			**¿Qué hace?** Valida el código y guarda la nueva contraseña, que debe cumplir el estándar de seguridad.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Contraseña actualizada",
						content =
								@Content(
										mediaType = "text/plain",
										examples =
												@ExampleObject(
														value = "Tu contraseña se actualizó. Ya puedes iniciar sesión."))),
				@ApiResponse(
						responseCode = "400",
						description = "Código o contraseña inválidos",
						content =
								@Content(
										mediaType = "text/plain",
										examples = @ExampleObject(value = "El código venció. Solicita uno nuevo.")))
			})
	@PostMapping("/resetpassword")
	public ResponseEntity<String> resetPassword(
			@Parameter(
							description = "Correo, código y nueva contraseña",
							required = true,
							schema = @Schema(implementation = CodigoDTO.class),
							examples =
									@ExampleObject(
											value =
													"""
					{
					  "username": "ana@correo.com",
					  "codigo": "123456",
					  "nuevaPassword": "NuevaClave1*"
					}
				"""))
					@RequestBody
					CodigoDTO request) {
		try {
			ExceptionChecker.checkEmpty("correo", request.getUsername());
			ExceptionChecker.checkEmpty("código", request.getCodigo());
			ExceptionChecker.checkPassword(request.getNuevaPassword());
		} catch (EmptyFieldException | PasswordNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = usuarioServ.restablecerPassword(request);
		if (status == 0) {
			return new ResponseEntity<>(
					"Tu contraseña se actualizó. Ya puedes iniciar sesión.", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(mensajeCodigo(status), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Registra el cierre de sesión en la auditoría. El token se borra en el front.
	 *
	 * @return ResponseEntity con mensaje de éxito
	 */
	@Operation(
			summary = "Cerrar sesión",
			description =
					"""
			Este endpoint registra en la auditoría que el usuario cerró sesión.

			**Nota:** el token JWT no se guarda en el servidor; el front lo elimina.
		""")
	@ApiResponses(
			value = {
				@ApiResponse(
						responseCode = "202",
						description = "Sesión cerrada",
						content =
								@Content(mediaType = "text/plain", examples = @ExampleObject(value = "Sesión cerrada.")))
			})
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			auditoriaServ.registrar(authentication.getName(), "LOGOUT", "Usuario", "Cerró sesión");
		}
		return new ResponseEntity<>("Sesión cerrada.", HttpStatus.ACCEPTED);
	}

	/**
	 * Convierte el resultado de enviar un código en la respuesta HTTP.
	 *
	 * @param status Código de resultado del servicio
	 * @return ResponseEntity con el mensaje correspondiente
	 */
	private ResponseEntity<String> respuestaEnvioCodigo(int status) {
		if (status == 0) {
			return new ResponseEntity<>(CODIGO_ENVIADO, HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>(
					"Espera un minuto antes de pedir otro código.", HttpStatus.TOO_MANY_REQUESTS);
		} else {
			return new ResponseEntity<>(
					"No pudimos enviar el correo. Intenta más tarde.", HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Devuelve el mensaje de error de la validación de un código.
	 *
	 * @param status Código de resultado del servicio
	 * @return Mensaje para el usuario
	 */
	private String mensajeCodigo(int status) {
		if (status == 3) {
			return "El código venció. Solicita uno nuevo.";
		} else if (status == 4) {
			return "Superaste el número de intentos. Solicita un código nuevo.";
		} else if (status == 5) {
			return "No tienes un código activo. Solicita uno nuevo.";
		} else {
			return "El código no es correcto.";
		}
	}

	/**
	 * Clase interna para representar la respuesta de autenticación. Contiene el token JWT y el rol
	 * del usuario autenticado.
	 */
	private static class AuthResponse {

		/** Token JWT generado para el usuario autenticado. */
		private final String token;

		/** Rol del usuario autenticado. */
		private final String role;

		/**
		 * Constructor con token y rol.
		 *
		 * @param token Token JWT generado
		 * @param role Rol del usuario
		 */
		public AuthResponse(String token, String role) {
			this.token = token;
			this.role = role;
		}

		/**
		 * Obtiene el token JWT.
		 *
		 * @return Token JWT
		 */
		public String getToken() {
			return token;
		}

		/**
		 * Obtiene el rol del usuario.
		 *
		 * @return Rol del usuario
		 */
		public String getRole() {
			return role;
		}
	}
}
