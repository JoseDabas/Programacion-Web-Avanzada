package com.hotel.booking_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class PaypalClient {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private final String BASE_URL = "https://api-m.sandbox.paypal.com";

    // 1. Obtener Token de Acceso desde PayPal
    private String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(encodedAuth);
        // Fallback robusto para Basic Auth si el helper superior falla
        headers.set("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                BASE_URL + "/v1/oauth2/token", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        }
        throw new RuntimeException("Error autenticando con PayPal. Revisa las credenciales.");
    }

    // 2. Crear Orden
    public Map<String, Object> createOrder(Double amount) {
        String token = getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Estructuramos el Payload oficial de la documentacion de PayPal v2
        Map<String, Object> orderRequest = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(
                        Map.of(
                                "amount", Map.of(
                                        "currency_code", "USD",
                                        "value", String.format("%.2f", amount).replace(",", ".")
                                )
                        )
                ),
                "application_context", Map.of(
                        "return_url", "http://localhost:5173/my-bookings",
                        "cancel_url", "http://localhost:5173/my-bookings"
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                BASE_URL + "/v2/checkout/orders", request, Map.class);

        return response.getBody();
    }

    // 3. Capturar Orden previamente aprobada
    public Map<String, Object> captureOrder(String orderId) {
        String token = getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                BASE_URL + "/v2/checkout/orders/" + orderId + "/capture", request, Map.class);

        return response.getBody();
    }
}
