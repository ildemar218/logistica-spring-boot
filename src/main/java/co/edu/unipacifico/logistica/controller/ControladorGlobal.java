package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.Permisos;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Comportamiento común a todos los controladores. */
@ControllerAdvice
public class ControladorGlobal {

    /** Nombre del usuario con sesión, para mostrarlo en el menú de todas las páginas. */
    @ModelAttribute("usuarioActual")
    public String usuarioActual(Principal principal) {
        return principal != null ? principal.getName() : null;
    }

    /** Permisos del usuario con sesión, para mostrar solo las opciones que puede usar. */
    @ModelAttribute("permisos")
    public Permisos permisos(Authentication auth) {
        List<String> roles = auth == null ? List.of()
                : auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new Permisos(roles);
    }

    /**
     * Un id inexistente responde 404 con una página controlada, no con un error 500.
     * Los métodos @ExceptionHandler no reciben los @ModelAttribute de arriba, por eso se agregan aquí.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException e, Authentication auth, Model model) {
        model.addAttribute("mensaje", e.getMessage());
        model.addAttribute("usuarioActual", auth != null ? auth.getName() : null);
        model.addAttribute("permisos", permisos(auth));
        return "error/no-encontrado";
    }
}
