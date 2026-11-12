package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.config.Roles;
import co.edu.unipacifico.logistica.dto.UsuarioAdminDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.model.Rol;
import co.edu.unipacifico.logistica.model.Usuarios;
import co.edu.unipacifico.logistica.repository.RolRepository;
import co.edu.unipacifico.logistica.repository.UsuariosRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Administración de usuarios: asignar roles y activar o desactivar cuentas. Solo para administradores. */
@Service
@Transactional
public class UsuarioAdminService {

    /** Roles que el administrador puede asignar, en el orden en que se muestran. */
    public static final List<String> ROLES_ASIGNABLES =
            List.of(Roles.ADMIN, Roles.COORDINADOR, Roles.AUXILIAR_BODEGA, Roles.TRANSPORTADOR);

    private final UsuariosRepository usuariosRepository;
    private final RolRepository rolRepository;

    public UsuarioAdminService(UsuariosRepository usuariosRepository, RolRepository rolRepository) {
        this.usuariosRepository = usuariosRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> listar() {
        return usuariosRepository.findAllConRoles().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioAdminDTO buscarPorId(Long id) {
        return toDTO(obtener(id));
    }

    /** Usuarios activos con rol Transportador, para asignarlos a los despachos. */
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> transportadores() {
        return usuariosRepository.findActivosConRol(Roles.TRANSPORTADOR).stream().map(this::toDTO).toList();
    }

    /**
     * Reemplaza los roles del usuario y su estado. Un administrador no puede quitarse a sí mismo
     * el rol de administrador ni desactivarse, para que el sistema no quede sin administrador.
     */
    public void actualizar(Long id, List<String> roles, boolean enabled, String loginActual) {
        Usuarios usuario = obtener(id);
        Set<String> elegidos = new HashSet<>(roles == null ? List.of() : roles);
        elegidos.retainAll(ROLES_ASIGNABLES);

        if (usuario.getLogin().equals(loginActual) && (!elegidos.contains(Roles.ADMIN) || !enabled)) {
            throw new ReglaNegocioException("No puede quitarse el rol de administrador ni desactivar su propia cuenta.");
        }
        if (elegidos.isEmpty()) {
            elegidos.add(Roles.SIN_ROL);
        }

        Set<Rol> nuevos = new HashSet<>();
        for (String nombre : elegidos) {
            nuevos.add(rolRepository.findByNombre(nombre)
                    .orElseThrow(() -> new ReglaNegocioException("El rol " + nombre + " no existe")));
        }
        usuario.getRoles().clear();
        usuario.getRoles().addAll(nuevos);
        usuario.setEnabled(enabled);
        usuario.setFechaModificacion(LocalDateTime.now());
    }

    private Usuarios obtener(Long id) {
        return usuariosRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("El usuario solicitado no existe"));
    }

    private UsuarioAdminDTO toDTO(Usuarios u) {
        UsuarioAdminDTO d = new UsuarioAdminDTO();
        d.setId(u.getId());
        d.setLogin(u.getLogin());
        String nombre = ((u.getNombre() == null ? "" : u.getNombre()) + " "
                + (u.getApellido() == null ? "" : u.getApellido())).trim();
        d.setNombreCompleto(nombre.isEmpty() ? u.getLogin() : nombre);
        d.setEmail(u.getEmail());
        d.setEnabled(u.getEnabled());
        d.setRoles(u.getRoles().stream().map(Rol::getNombre).sorted().toList());
        return d;
    }
}
