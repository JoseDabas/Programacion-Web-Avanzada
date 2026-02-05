package practica1.mockup_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.repositories.UserRepository;

/**
 * Servicio personalizado para la carga de detalles de usuario.
 * Implementa la interfaz UserDetailsService de Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        /**
         * Carga los datos del usuario basándose en el nombre de usuario.
         * Convierte la entidad User de nuestra aplicación a un objeto UserDetails de
         * Spring Security.
         *
         * @param username El nombre de usuario a buscar.
         * @return Los detalles del usuario (UserDetails) necesarios para la
         *         autenticación.
         * @throws UsernameNotFoundException Si el usuario no existe en la base de
         *                                   datos.
         */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // 1. Buscar usuario en la base de datos
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

                // 2. Convertir User (Entidad) a UserDetails (Spring Security)
                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .authorities(user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                                .toList())
                                .disabled(!user.isEnabled())
                                .build();
        }
}
