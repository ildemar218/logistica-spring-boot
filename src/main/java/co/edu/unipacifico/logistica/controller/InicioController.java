package co.edu.unipacifico.logistica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("mensaje", "Bienvenido al sistema de logística");
        return "inicio";
    }

    /**
     * Página que se muestra cuando un usuario intenta entrar a una opción que su rol no permite.
     * Acepta cualquier método porque Spring Security reenvía aquí también los POST rechazados.
     */
    @RequestMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }
}
