package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import org.springframework.validation.BindingResult;

/** Muestra un error de regla de negocio en el formulario: junto al campo, o arriba si no tiene campo. */
final class Errores {

    private Errores() {
    }

    static void rechazar(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() != null) {
            resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
        } else {
            resultado.reject("regla", e.getMessage());
        }
    }
}
