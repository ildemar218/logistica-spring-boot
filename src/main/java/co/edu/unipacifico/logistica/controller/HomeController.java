package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.Permisos;
import co.edu.unipacifico.logistica.model.Despacho;
import co.edu.unipacifico.logistica.service.DespachoService;
import co.edu.unipacifico.logistica.service.InventarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    private final InventarioService inventarioService;
    private final DespachoService despachoService;

    public HomeController(InventarioService inventarioService, DespachoService despachoService) {
        this.inventarioService = inventarioService;
        this.despachoService = despachoService;
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /** Página protegida: tablero adaptado al rol del usuario. */
    @GetMapping("/principal")
    public String principal(@ModelAttribute("permisos") Permisos permisos, Principal principal, Model model) {
        if (permisos.isBodega()) {
            model.addAttribute("alertas", inventarioService.alertasStockMinimo());
            model.addAttribute("ultimosMovimientos", inventarioService.movimientos(null, null).stream().limit(6).toList());
            model.addAttribute("pendientes", despachoService.contar(Despacho.PENDIENTE, null));
            model.addAttribute("enRuta", despachoService.contar(Despacho.EN_RUTA, null));
        } else if (permisos.isTransportador()) {
            String login = principal.getName();
            model.addAttribute("pendientes", despachoService.contar(Despacho.PENDIENTE, login));
            model.addAttribute("enRuta", despachoService.contar(Despacho.EN_RUTA, login));
            model.addAttribute("misDespachos", despachoService.listar(Despacho.EN_RUTA, login));
        }
        return "principal";
    }
}
