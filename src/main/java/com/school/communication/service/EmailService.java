package com.school.communication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.school.infra.entity.LabReservation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean emailEnabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            log.info("Correo electrónico deshabilitado. Se enviará a: {} - Asunto: {}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Correo electrónico enviado exitosamente a: {}", to);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo electrónico a: {}", to, e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!emailEnabled) {
            log.info("Correo electrónico deshabilitado. Se enviará HTML a: {} - Asunto: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }

    public void sendReservationCreatedEmail(LabReservation reservation) {
        String to = reservation.getTeacher().getEmail();
        if (to == null || to.isBlank()) {
            log.warn("El profesor {} no tiene correo electrónico configurado", reservation.getTeacher().getId());
            return;
        }

        String subject = "Nueva Reserva de Laboratorio - Pendiente de Aprobación";
        String htmlContent = buildReservationCreatedHtml(reservation);
        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendReservationApprovedEmail(LabReservation reservation) {
        String to = reservation.getTeacher().getEmail();
        if (to == null || to.isBlank()) {
            return;
        }

        String subject = "✅ Reserva de Laboratorio APROBADA";
        String htmlContent = buildReservationApprovedHtml(reservation);
        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendReservationRejectedEmail(LabReservation reservation) {
        String to = reservation.getTeacher().getEmail();
        if (to == null || to.isBlank()) {
            return;
        }

        String subject = "❌ Reserva de Laboratorio RECHAZADA";
        String htmlContent = buildReservationRejectedHtml(reservation);
        sendHtmlEmail(to, subject, htmlContent);
    }

    private String buildReservationCreatedHtml(LabReservation reservation) {
        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                                <h2 style="color: #4e73df;">Nueva Reserva de Laboratorio</h2>
                                <p>Estimado/a <strong>%s %s</strong>,</p>
                                <p>Su solicitud de reserva ha sido registrada exitosamente y está <strong>pendiente de aprobación</strong>.</p>

                                <div style="background-color: #f8f9fc; padding: 15px; border-left: 4px solid #4e73df; margin: 20px 0;">
                                    <h3 style="margin-top: 0;">Detalles de la Reserva:</h3>
                                    <p><strong>Laboratorio:</strong> %s</p>
                                    <p><strong>Fecha y Hora:</strong> %s</p>
                                    <p><strong>Duración:</strong> %s</p>
                                    <p><strong>Propósito:</strong> %s</p>
                                </div>

                                <p>Recibirá una notificación cuando su reserva sea aprobada o rechazada.</p>
                                <p style="color: #858796; font-size: 12px; margin-top: 30px;">
                                    Este es un correo automático. Por favor no responda a este mensaje.
                                </p>
                            </div>
                        </body>
                        </html>
                        """,
                reservation.getTeacher().getFirstName(),
                reservation.getTeacher().getLastName(),
                reservation.getRoom().getRoomNumber(),
                reservation.getStartTime(),
                java.time.Duration.between(reservation.getStartTime(), reservation.getEndTime()).toHours() + " horas",
                reservation.getPurpose() != null ? reservation.getPurpose() : "N/A");
    }

    private String buildReservationApprovedHtml(LabReservation reservation) {
        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                                <h2 style="color: #1cc88a;">✅ Reserva APROBADA</h2>
                                <p>Estimado/a <strong>%s %s</strong>,</p>
                                <p>Su reserva de laboratorio ha sido <strong style="color: #1cc88a;">APROBADA</strong>.</p>

                                <div style="background-color: #d1f2eb; padding: 15px; border-left: 4px solid #1cc88a; margin: 20px 0;">
                                    <h3 style="margin-top: 0;">Detalles de la Reserva:</h3>
                                    <p><strong>Laboratorio:</strong> %s</p>
                                    <p><strong>Fecha y Hora:</strong> %s</p>
                                    <p><strong>Aprobado por:</strong> %s</p>
                                </div>

                                <p>Por favor, asegúrese de llegar puntualmente y dejar el laboratorio en las mismas condiciones.</p>
                                <p style="color: #858796; font-size: 12px; margin-top: 30px;">
                                    Este es un correo automático. Por favor no responda a este mensaje.
                                </p>
                            </div>
                        </body>
                        </html>
                        """,
                reservation.getTeacher().getFirstName(),
                reservation.getTeacher().getLastName(),
                reservation.getRoom().getRoomNumber(),
                reservation.getStartTime(),
                reservation.getApprovedBy() != null
                        ? reservation.getApprovedBy().getFirstName() + " " + reservation.getApprovedBy().getLastName()
                        : "Administración");
    }

    private String buildReservationRejectedHtml(LabReservation reservation) {
        return String.format(
                """
                        <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                                <h2 style="color: #e74a3b;">❌ Reserva RECHAZADA</h2>
                                <p>Estimado/a <strong>%s %s</strong>,</p>
                                <p>Lamentamos informarle que su reserva de laboratorio ha sido <strong style="color: #e74a3b;">RECHAZADA</strong>.</p>

                                <div style="background-color: #f8d7da; padding: 15px; border-left: 4px solid #e74a3b; margin: 20px 0;">
                                    <h3 style="margin-top: 0;">Detalles de la Reserva:</h3>
                                    <p><strong>Laboratorio:</strong> %s</p>
                                    <p><strong>Fecha y Hora:</strong> %s</p>
                                    <p><strong>Motivo del Rechazo:</strong> %s</p>
                                </div>

                                <p>Si tiene alguna pregunta, por favor contacte con la administración.</p>
                                <p style="color: #858796; font-size: 12px; margin-top: 30px;">
                                    Este es un correo automático. Por favor no responda a este mensaje.
                                </p>
                            </div>
                        </body>
                        </html>
                        """,
                reservation.getTeacher().getFirstName(),
                reservation.getTeacher().getLastName(),
                reservation.getRoom().getRoomNumber(),
                reservation.getStartTime(),
                reservation.getRejectionReason() != null ? reservation.getRejectionReason() : "No especificado");
    }
}
