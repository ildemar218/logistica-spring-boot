package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.config.Roles;
import co.edu.unipacifico.logistica.dto.UsuarioDTO;
import co.edu.unipacifico.logistica.dto.UsuarioRegistroDTO;
import co.edu.unipacifico.logistica.exception.RegistroDuplicadoException;
import co.edu.unipacifico.logistica.mapper.UsuariosMapper;
import co.edu.unipacifico.logistica.model.Rol;
import co.edu.unipacifico.logistica.model.Usuarios;
import co.edu.unipacifico.logistica.repository.RolRepository;
import co.edu.unipacifico.logistica.repository.UsuariosRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroUsuarioService {

    private final UsuariosRepository usuariosRepository;
    private final RolRepository rolRepository;
    private final UsuariosMapper usuariosMapper;
    private final PasswordEncoder passwordEncoder;

    public RegistroUsuarioService(UsuariosRepository usuariosRepository,
                                  RolRepository rolRepository,
                                  UsuariosMapper usuariosMapper,
                                  PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.rolRepository = rolRepository;
        this.usuariosMapper = usuariosMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * El formulario público nunca elige privilegios. Si el sistema aún no tiene administrador,
     * el primer usuario registrado lo es; los demás quedan sin rol hasta que el administrador
     * les asigne uno en el módulo Usuarios.
     */
    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO dto) {
        if (usuariosRepository.existsByLogin(dto.getLogin())) {
            throw new RegistroDuplicadoException("login", "El login ya está registrado");
        }
        if (usuariosRepository.existsByEmail(dto.getEmail())) {
            throw new RegistroDuplicadoException("email", "El correo ya está registrado");
        }

        String nombreRol = usuariosRepository.existeAlgunoConRol(Roles.ADMIN) ? Roles.SIN_ROL : Roles.ADMIN;
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalStateException("Los roles no están configurados. Ejecute sql/01_esquema.sql"));

        Usuarios usuario = usuariosMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEnabled(true);
        usuario.getRoles().add(rol);

        return usuariosMapper.toDto(usuariosRepository.save(usuario));
    }
}
