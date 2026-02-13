package practica1.mockup_api.controllers.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practica1.mockup_api.entities.MockEndpoint;
import practica1.mockup_api.repositories.MockEndpointRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mock_project")
@RequiredArgsConstructor
public class MockRunnerController {

    private final MockEndpointRepository mockRepository;
    private final ObjectMapper objectMapper;

    // Captura CUALQUIER ruta que empiece por /mock_project/ y cualquier metodo
    @RequestMapping(value = "/**")
    public ResponseEntity<?> handleMockRequest(HttpServletRequest request) {

        // Obtener la ruta real (quitando el "/mock_project")
        String fullPath = request.getRequestURI().substring(13);

        // Obtener el método HTTP (GET, POST...)
        String method = request.getMethod();

        // Buscar en BD
        Optional<MockEndpoint> mockOpt = mockRepository.findByMethodAndPath(method, fullPath);

        if (mockOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Mock no encontrado para ruta: " + fullPath + " (" + method + ")\"}");
        }

        MockEndpoint mock = mockOpt.get();

        // Validar Expiración
        if (mock.getExpirationDate() != null && LocalDateTime.now().isAfter(mock.getExpirationDate())) {
            return ResponseEntity.status(HttpStatus.GONE) // 410 Gone
                    .body("{\"error\": \"Este Mock ha expirado.\"}");
        }

        // Simular Delay
        if (mock.getDelayInSeconds() != null && mock.getDelayInSeconds() > 0) {
            try {
                Thread.sleep(mock.getDelayInSeconds() * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Validar JWT
        if (mock.isJwtProtected()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Token JWT requerido.\"}");
            }
        }

        // Construir Headers de respuesta
        HttpHeaders responseHeaders = new HttpHeaders();

        // Agregar Content-Type configurado
        if (mock.getContentType() != null && !mock.getContentType().isEmpty()) {
            responseHeaders.setContentType(MediaType.parseMediaType(mock.getContentType()));
        } else {
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        // Agregar Headers personalizados
        if (mock.getResponseHeaders() != null && !mock.getResponseHeaders().isEmpty()) {
            try {
                // leer el string como un JSON Map {"Key": "Value"}
                Map<String, String> customHeaders = objectMapper.readValue(
                        mock.getResponseHeaders(), new TypeReference<Map<String, String>>() {
                        });
                customHeaders.forEach(responseHeaders::add);
            } catch (Exception e) {
                System.err.println("Error parseando headers del mock: " + e.getMessage());
            }
        }

        // Retornar la Respuesta Final
        return ResponseEntity
                .status(mock.getResponseStatus() != null ? mock.getResponseStatus() : 200)
                .headers(responseHeaders)
                .body(mock.getResponseBody());
    }
}
