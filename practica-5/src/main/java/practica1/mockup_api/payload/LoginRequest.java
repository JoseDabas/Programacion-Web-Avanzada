package practica1.mockup_api.payload;

import lombok.Data;

/**
 * DTO (Data Transfer Object) para la solicitud de inicio de sesión.
 * Contiene el nombre de usuario y la contraseña enviados por el cliente.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
