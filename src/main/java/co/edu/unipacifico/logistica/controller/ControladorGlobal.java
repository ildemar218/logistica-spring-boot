package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import java.security.Principal;
import org.springframework.http.HttpStatus;
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

    /** Un id inexistente responde 404 con una página controlada, no con un error 500. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException e, Model model) {
        model.addAttribute("mensaje", e.getMessage());
        return "error/no-encontrado";
    }
}
