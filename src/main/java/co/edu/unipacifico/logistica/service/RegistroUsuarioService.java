package co.edu.unipacifico.logistica.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unipacifico.logistica.dto.UsuarioDTO;
import co.edu.unipacifico.logistica.dto.UsuarioRegistroDTO;
import co.edu.unipacifico.logistica.exception.RegistroDuplicadoException;
import co.edu.unipacifico.logistica.mapper.UsuariosMapper;
import co.edu.unipacifico.logistica.model.Rol;
import co.edu.unipacifico.logistica.model.Usuarios;
import co.edu.unipacifico.logistica.repository.RolRepository;
import co.edu.unipacifico.logistica.repository.UsuariosRepository;

@Service
public class RegistroUsuarioService {

    private static final String ROL_BASICO = "ROLE_USER";

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

    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO dto) {
        if (usuariosRepository.existsByLogin(dto.getLogin())) {
            throw new RegistroDuplicadoException("login", "El login ya está registrado");
        }
        if (usuariosRepository.existsByEmail(dto.getEmail())) {
            throw new RegistroDuplicadoException("email", "El correo ya está registrado");
        }

        Rol rol = rolRepository.findByNombre(ROL_BASICO)
                .orElseThrow(() -> new IllegalStateException("El rol básico no está configurado"));

        Usuarios usuario = usuariosMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEnabled(true);
        usuario.getRoles().add(rol);

        return usuariosMapper.toDto(usuariosRepository.save(usuario));
    }
}