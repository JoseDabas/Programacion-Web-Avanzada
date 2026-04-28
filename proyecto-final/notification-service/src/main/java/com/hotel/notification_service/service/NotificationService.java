package com.hotel.notification_service.service;

import com.hotel.notification_service.model.BookingInvoiceRequest;
import com.hotel.notification_service.model.RegistrationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio centralizado para manejar envios de correos y generacion de reportes
 * PDF.
 */
@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envia un correo electronico de confirmacion de registro al usuario.
     */
    public void sendRegistrationConfirmation(RegistrationRequest request) {
        try {
            String targetEmail = request.email() != null ? request.email() : "";
            String nombre = request.nombre() != null ? request.nombre() : "Huésped";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(targetEmail);
            helper.setSubject("Bienvenido a Aurora - Hotel & Retreat");

            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                            .container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
                            .header { background-color: #1A3A3A; color: #ffffff; padding: 30px 20px; text-align: center; }
                            .header h1 { margin: 0; font-size: 24px; font-weight: 300; letter-spacing: 2px; }
                            .content { padding: 40px 30px; color: #333333; line-height: 1.6; }
                            .content h2 { color: #1A3A3A; font-size: 20px; margin-top: 0; }
                            .details { background-color: #f9f9f9; padding: 15px; border-left: 4px solid #D4AF37; margin: 20px 0; }
                            .btn { display: inline-block; background-color: #D4AF37; color: #ffffff; text-decoration: none; padding: 12px 25px; border-radius: 4px; font-weight: bold; margin-top: 20px; text-align: center; }
                            .footer { background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #888888; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>AURORA</h1>
                                <p style="margin: 5px 0 0; font-size: 12px; letter-spacing: 1px;">HOTEL & RETREAT</p>
                            </div>
                            <div class="content">
                                <h2>¡Estimado/a %s, bienvenido/a!</h2>
                                <p>Nos llena de alegría darle la bienvenida a la familia Aurora. Su cuenta ha sido creada exitosamente y ahora tiene acceso a exclusivas propiedades y servicios diseñados para brindarle una experiencia inolvidable.</p>

                                <div class="details">
                                    <strong>Sus credenciales de acceso:</strong><br>
                                    Usuario: %s<br>
                                </div>

                                <p>Puede ingresar a nuestra plataforma y comenzar a explorar todo lo que tenemos preparado para usted.</p>

                                <div style="text-align: center;">
                                    <a href="http://localhost:5173/login" class="btn">Acceder a mi cuenta</a>
                                </div>
                            </div>
                            <div class="footer">
                                <p>Si no ha solicitado esta cuenta, por favor ignore este correo.</p>
                                <p>&copy; 2026 Aurora - Hotel & Retreat. Todos los derechos reservados.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """
                    .formatted(nombre, targetEmail);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Correo de confirmacion enviado exitosamente a: " + targetEmail);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de registro: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte transaccional en PDF con JasperReports y lo envia como
     * adjunto por correo.
     */
    public void sendBookingInvoice(BookingInvoiceRequest request) {
        try {
            // 1. Cargar la plantilla compilable del reporte Jasper (JRXML)
            InputStream reportStream = getClass().getResourceAsStream("/factura_reserva.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se encontro la plantilla factura_reserva.jrxml");
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // 2. Mapear los parametros provistos hacia el formulario Jasper
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("clienteNombre", request.clienteNombre());
            parameters.put("clienteEmail", request.clienteEmail());
            parameters.put("propiedadNombre", request.propiedadNombre());
            parameters.put("fechaEntrada", request.fechaEntrada());
            parameters.put("fechaSalida", request.fechaSalida());
            parameters.put("costoTotal", request.costoTotal());
            parameters.put("impuestos", request.impuestos());

            // 3. Llenar el reporte utilizando campos directos por parametro
            // (se usa un DataSource vacio pues no requerimos llenar filas de repeticion
            // aqui)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // 4. Exportar el contenido del layout final a crudo formato binario (PDF)
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // 5. Contruir y enviar el correo con JavaMail (con archivo .pdf en anexo)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(request.clienteEmail());
            helper.setSubject("Confirmación de Reserva y Factura - Aurora");

            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                            .container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
                            .header { background-color: #1A3A3A; color: #ffffff; padding: 30px 20px; text-align: center; }
                            .header h1 { margin: 0; font-size: 24px; font-weight: 300; letter-spacing: 2px; }
                            .content { padding: 40px 30px; color: #333333; line-height: 1.6; }
                            .content h2 { color: #1A3A3A; font-size: 20px; margin-top: 0; }
                            .highlight { background-color: #f9f9f9; padding: 20px; border-radius: 4px; border-left: 4px solid #D4AF37; margin: 25px 0; }
                            .footer { background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #888888; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>AURORA</h1>
                                <p style="margin: 5px 0 0; font-size: 12px; letter-spacing: 1px;">HOTEL & RETREAT</p>
                            </div>
                            <div class="content">
                                <h2>¡Estimado/a %s, su reserva está confirmada!</h2>
                                <p>Nos complace informarle que su reserva en <strong>%s</strong> se ha procesado con éxito. Su experiencia inolvidable ya está en marcha.</p>

                                <div class="highlight">
                                    <p style="margin: 0;">Adjunto en este correo encontrará su <strong>factura oficial</strong> en formato PDF, la cual incluye todos los detalles de las fechas seleccionadas, tasas e impuestos aplicados.</p>
                                </div>

                                <p>Cualquier modificación o solicitud especial, por favor no dude en contactarnos. Nos estamos preparando para brindarle el mejor servicio durante su estadía.</p>
                                <p>Saludos cordiales,<br>El equipo de Aurora</p>
                            </div>
                            <div class="footer">
                                <p>Si tiene alguna pregunta, puede responder directamente a este correo.</p>
                                <p>&copy; 2026 Aurora - Hotel & Retreat. Todos los derechos reservados.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """
                    .formatted(request.clienteNombre(), request.propiedadNombre());

            helper.setText(htmlContent, true);
            helper.addAttachment("Factura_Reserva_Aurora.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Factura pdf generada y enviada a: " + request.clienteEmail());

        } catch (Exception e) {
            System.err.println("Error critico al generar o enviar la factura: " + e.getMessage());
        }
    }
}
