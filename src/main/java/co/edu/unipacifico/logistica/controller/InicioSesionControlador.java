package co.edu.unipacifico.logistica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioSesionControlador {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}