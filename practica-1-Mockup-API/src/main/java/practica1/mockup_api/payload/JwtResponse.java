package practica1.mockup_api.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la respuesta de autenticación exitosa.
 * Devuelve el token JWT y los detalles básicos del usuario.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    /**
     * El token JWT generado.
     */
    private String token;

    /**
     * El tipo de token (generalmente "Bearer").
     */
    @Builder.Default
    private String type = "Bearer";

    private Long id;

    private String username;

    private List<String> roles;
}
