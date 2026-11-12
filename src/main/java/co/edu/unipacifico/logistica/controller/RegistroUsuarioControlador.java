package co.edu.unipacifico.logistica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import co.edu.unipacifico.logistica.dto.UsuarioRegistroDTO;
import co.edu.unipacifico.logistica.exception.RegistroDuplicadoException;
import co.edu.unipacifico.logistica.service.RegistroUsuarioService;
import jakarta.validation.Valid;

@Controller
public class RegistroUsuarioControlador {

    private final RegistroUsuarioService registroUsuarioService;

    public RegistroUsuarioControlador(RegistroUsuarioService registroUsuarioService) {
        this.registroUsuarioService = registroUsuarioService;
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO dto,
                            BindingResult result) {
        if (result.hasErrors()) {
            dto.setPassword(null);
            return "registro";
        }
        try {
            registroUsuarioService.registrar(dto);
        } catch (RegistroDuplicadoException e) {
            result.rejectValue(e.getCampo(), "duplicado", e.getMessage());
            dto.setPassword(null);
            return "registro";
        }
        return "redirect:/registro?exito";
    }
}