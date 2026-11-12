package co.edu.unipacifico.logistica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("mensaje", "Bienvenido al sistema de logística");
        return "inicio";
    }
}
