package com.saas.legit.module.notification.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    private final String senderEmail;

    public EmailService(
            @Value("${resend.api.key}") String apiKey,
            @Value("${resend.sender-email}") String senderEmail
    ) {
        this.resend = new Resend(apiKey);
        this.senderEmail = senderEmail;
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        String html = loadTemplate("templates/email-otp-verification.html")
                .replace("{{OTP_CODE}}", otpCode);

        String uniqueId = String.valueOf(System.currentTimeMillis() % 10000);
        send(toEmail, "Verifica tu cuenta – Código de seguridad #" + uniqueId, html);
    }

    public void sendPasswordResetEmail(String toEmail, String otpCode) {
        String html = loadTemplate("templates/email-password-reset.html")
                .replace("{{OTP_CODE}}", otpCode);

        String uniqueId = String.valueOf(System.currentTimeMillis() % 10000);
        send(toEmail, "Recuperación de contraseña – Código de seguridad #" + uniqueId, html);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void send(String toEmail, String subject, String html) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("AbogHub <" + senderEmail + ">")
                .to(toEmail)
                .subject(subject)
                .html(html)
                .build();

        try {
            resend.emails().send(params);
            LOGGER.info("Email enviado correctamente a: {}", toEmail);
        } catch (Exception e) {
            LOGGER.error("Error al enviar el correo a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo electrónico. Inténtalo más tarde.", e);
        }
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("No se pudo cargar la plantilla de email: {}", path);
            throw new RuntimeException("Error interno al cargar la plantilla de correo.", e);
        }
    }
}