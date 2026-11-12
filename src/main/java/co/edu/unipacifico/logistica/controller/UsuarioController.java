package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.config.Roles;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.UsuarioAdminService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Gestión de usuarios y roles. Solo administradores (ver SecurityConfiguration). */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioAdminService servicio;

    public UsuarioController(UsuarioAdminService servicio) {
        this.servicio = servicio;
    }

    @ModelAttribute("rolesAsignables")
    public List<String> rolesAsignables() {
        return UsuarioAdminService.ROLES_ASIGNABLES;
    }

    /** Permite a las vistas mostrar "Coordinador logístico" en lugar de "ROLE_COORDINADOR". */
    @ModelAttribute("etiquetaRol")
    public java.util.function.Function<String, String> etiquetaRol() {
        return Roles::etiqueta;
    }

    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("usuarios", servicio.listar());
        return "usuarios/index";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", servicio.buscarPorId(id));
        return "usuarios/editarUsuario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam(name = "roles", required = false) List<String> roles,
                             @RequestParam(name = "enabled", defaultValue = "false") boolean enabled,
                             Principal principal, RedirectAttributes flash) {
        try {
            servicio.actualizar(id, roles, enabled, principal.getName());
            flash.addFlashAttribute("mensaje", "Usuario actualizado. Los cambios aplican en su próximo inicio de sesión.");
            return "redirect:/usuarios/";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/editar/" + id;
        }
    }
}
