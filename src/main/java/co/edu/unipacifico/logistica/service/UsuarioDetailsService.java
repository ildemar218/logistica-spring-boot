package co.edu.unipacifico.logistica.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unipacifico.logistica.model.Usuarios;
import co.edu.unipacifico.logistica.repository.UsuariosRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuariosRepository usuariosRepository;

    public UsuarioDetailsService(UsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuarios usuario = usuariosRepository.findByLoginConRoles(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<SimpleGrantedAuthority> autoridades = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .toList();

        return User.withUsername(usuario.getLogin())
                .password(usuario.getPassword())
                .disabled(!Boolean.TRUE.equals(usuario.getEnabled()))
                .authorities(autoridades)
                .build();
    }
}