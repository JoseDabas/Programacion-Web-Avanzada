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
 * Servicio centralizado para manejar envios de correos y generacion de reportes PDF.
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
            String nombre = request.nombre() != null ? request.nombre() : "Usuario";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(targetEmail);
            helper.setSubject("Bienvenido a Discover Cibao - Accesos del Sistema");
            helper.setText("Hola " + nombre + ",\n\nGracias por registrarte en Discover Cibao.\n\n" +
                           "Tus datos de acceso son:\n" +
                           "Usuario (Email): " + targetEmail + "\n\n" +
                           "Puedes ingresar a la plataforma a traves del siguiente enlace:\n" +
                           "http://localhost:5173/login\n\n" +
                           "¡Esperamos que disfrutes tu estadia!");
            mailSender.send(message);
            System.out.println("Correo de confirmacion enviado exitosamente a: " + targetEmail);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de registro: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte transaccional en PDF con JasperReports y lo envia como adjunto por correo.
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
            // (se usa un DataSource vacio pues no requerimos llenar filas de repeticion aqui)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // 4. Exportar el contenido del layout final a crudo formato binario (PDF)
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // 5. Contruir y enviar el correo con JavaMail (con archivo .pdf en anexo)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(request.clienteEmail());
            helper.setSubject("Factura de su Reserva - Discover Cibao");
            helper.setText("Hola " + request.clienteNombre() + ",\n\nAdjunto encontrara la factura detallada de su reserva en " + request.propiedadNombre() + ".");
            helper.addAttachment("Factura_Reserva.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Factura pdf generada y enviada a: " + request.clienteEmail());

        } catch (Exception e) {
            System.err.println("Error critico al generar o enviar la factura: " + e.getMessage());
        }
    }
}
