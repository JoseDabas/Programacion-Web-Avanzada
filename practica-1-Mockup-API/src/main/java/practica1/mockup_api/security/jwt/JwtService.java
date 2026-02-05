package practica1.mockup_api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio encargado de la gestión de tokens JWT (JSON Web Tokens).
 * Proporciona métodos para generar, validar y extraer información de los
 * tokens.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:86400000}") // 1 día por defecto
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     *
     * @param token El token JWT del cual extraer la información.
     * @return El nombre de usuario contenido en el token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método genérico para extraer un "claim" específico del token.
     *
     * @param token          El token JWT.
     * @param claimsResolver Función para resolver el claim deseado.
     * @param <T>            El tipo de dato del claim.
     * @return El valor del claim extraído.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para un usuario autenticado sin claims extra.
     *
     * @param userDetails Los detalles del usuario autenticado.
     * @return El token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales personalizados.
     *
     * @param extraClaims Mapa de claims adicionales a incluir en el token.
     * @param userDetails Los detalles del usuario autenticado.
     * @return El token JWT generado.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Construye el token JWT configurando sus propiedades (claims, sujeto, fecha,
     * expiración, firma).
     *
     * @param extraClaims Mapa de claims adicionales.
     * @param userDetails Detalle del usuario.
     * @param expiration  Tiempo de expiración en milisegundos.
     * @return El token JWT compacto y firmado.
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token es correcto y pertenece al usuario proporcionado.
     *
     * @param token       El token JWT a validar.
     * @param userDetails Los detalles del usuario contra el cual validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param token El token JWT.
     * @return true si ha expirado, false si sigue vigente.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     *
     * @param token El token JWT.
     * @return La fecha de expiración.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parsea y obtiene todos los claims del cuerpo del token.
     * Se valida la firma digital del token en este proceso.
     *
     * @param token El token JWT.
     * @return Objeto Claims con toda la información.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma decodificada desde la configuración.
     *
     * @return La clave criptográfica para firmar/verificar tokens.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
