package practica1.mockup_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import practica1.mockup_api.security.jwt.JwtAuthenticationFilter;

/**
 * Configuración principal de Spring Security.
 * Define la cadena de filtros de seguridad, políticas de sesión y beans de
 * autenticación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Define la cadena de filtros de seguridad (Security Filter Chain).
     * Configura qué rutas son públicas, cuales requieren autenticación y cómo se
     * gestionan las sesiones.
     *
     * @param http Configuración de seguridad HTTP.
     * @return El bean SecurityFilterChain construido.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuración de autorización de peticiones
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos públicos
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // Consola de base de datos H2 pública (solo dev)
                        .requestMatchers("/h2-console/**").permitAll()
                        // Endpoints de autenticación (/auth/login) públicos
                        .requestMatchers("/auth/**").permitAll()
                        // Endpoints mocks públicos por defecto (se asegurarán condicionalmente)
                        .requestMatchers("/m/**").permitAll()
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated())
                // 2. Gestión de sesiones sin estado (Stateless) para API REST con JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. Proveedor de autenticación personalizado
                .authenticationProvider(authenticationProvider())
                // 4. Agregar filtro JWT antes del filtro de usuario/contraseña estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 5. Deshabilitar formulario de login por defecto (usamos JWT)
                .formLogin(AbstractHttpConfigurer::disable)
                // 6. Configuración de logout
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(
                                (request, response, authentication) -> SecurityContextHolder.clearContext()))
                // 7. Deshabilitar CSRF (no necesario para APIs stateless con JWT)
                .csrf(AbstractHttpConfigurer::disable)
                // 8. Permitir Frames para la consola H2
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    /**
     * Define el proveedor de autenticación que usa UserDetailsService y
     * PasswordEncoder.
     *
     * @return El AuthenticationProvider configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expone el AuthenticationManager como un bean para ser usado en el
     * AuthController.
     *
     * @param authenticationConfiguration Configuración de autenticación de Spring.
     * @return El AuthenticationManager.
     * @throws Exception Si no se puede obtener el manejador.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define el encoder de contraseñas (BCrypt).
     *
     * @return El PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
