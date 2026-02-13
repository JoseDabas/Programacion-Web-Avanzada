package practica1.mockup_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.repositories.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // Buscar usuario en la base de datos
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

                // Convertir User (Entidad) a UserDetails (Spring Security)
                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .authorities(user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                                .collect(Collectors.toList()))
                                .disabled(!user.isActive())
                                .build();
        }
}