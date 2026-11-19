package co.edu.unbosque.GreenBuilding.service;

import co.edu.unbosque.GreenBuilding.model.CodigoVerificacion.Proposito;
import co.edu.unbosque.GreenBuilding.model.Usuario;
import co.edu.unbosque.GreenBuilding.util.TextoUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio que envía los correos de la aplicación con JavaMail. Si no hay un servidor SMTP
 * configurado (variable MAIL_HOST vacía), el correo se simula y se escribe en la consola, para
 * poder probar el registro sin SMTP.
 */
@Service
public class CorreoService {

	/** Registro de mensajes en la consola. */
	private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

	/** Enviador de correos de Spring. Solo existe cuando hay un servidor de correo configurado. */
	@Autowired(required = false) private JavaMailSender enviador;

	/** Servicio que arma el HTML de los correos. */
	@Autowired private PlantillaCorreoService plantillaServ;

	/** Servidor SMTP configurado. */
	@Value("${spring.mail.host:}")
	private String servidor;

	/** Dirección que aparece como remitente. */
	@Value("${app.correo.remitente:no-responder@greenbuilding.com}")
	private String remitente;

	/** Constructor por defecto. */
	public CorreoService() {}

	/**
	 * Indica si hay un servidor SMTP configurado.
	 *
	 * @return true si se pueden enviar correos reales
	 */
	public boolean estaConfigurado() {
		return !TextoUtil.vacio(servidor) && enviador != null;
	}

	/**
	 * Envía el código de verificación o de restablecimiento al correo del usuario.
	 *
	 * @param usuario Usuario que recibe el código
	 * @param codigo Código de 6 dígitos
	 * @param proposito Para qué sirve el código
	 * @return true si el correo se envió (o se simuló), false si falló
	 */
	public boolean enviarCodigo(Usuario usuario, String codigo, Proposito proposito) {
		if (!estaConfigurado()) {
			log.info(
					"[CORREO SIMULADO] Código de {} para {}: {}", proposito, usuario.getUsername(), codigo);
			return true;
		}
		String asunto = "Restablece tu contraseña de Green Building";
		if (proposito == Proposito.VERIFICACION) {
			asunto = "Verifica tu cuenta de Green Building";
		}
		return enviarHtml(
				usuario.getUsername(), asunto, plantillaServ.codigo(usuario.getNombre(), codigo, proposito));
	}

	/**
	 * Envía un correo con contenido HTML.
	 *
	 * @param destino Correo de destino
	 * @param asunto Asunto del correo
	 * @param html Contenido HTML
	 * @return true si el correo se envió (o se simuló), false si falló
	 */
	public boolean enviarHtml(String destino, String asunto, String html) {
		if (!estaConfigurado()) {
			log.info("[CORREO SIMULADO] Para: {} | Asunto: {}", destino, asunto);
			return true;
		}
		try {
			MimeMessage mensaje = enviador.createMimeMessage();
			MimeMessageHelper ayudante = new MimeMessageHelper(mensaje, true, "UTF-8");
			ayudante.setFrom(remitente);
			ayudante.setTo(destino);
			ayudante.setSubject(asunto);
			ayudante.setText(html, true);
			enviador.send(mensaje);
			return true;
		} catch (MessagingException | MailException e) {
			log.error("No se pudo enviar el correo a {}", destino, e);
			return false;
		}
	}
}
