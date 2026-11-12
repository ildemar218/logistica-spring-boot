package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.ResumenStockDTO;
import co.edu.unipacifico.logistica.service.DespachoService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.OrdenCompraService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final InventarioService inventarioService;
    private final OrdenCompraService ordenCompraService;
    private final DespachoService despachoService;

    public HomeController(InventarioService inventarioService, OrdenCompraService ordenCompraService,
                          DespachoService despachoService) {
        this.inventarioService = inventarioService;
        this.ordenCompraService = ordenCompraService;
        this.despachoService = despachoService;
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /** Página protegida: tablero con el estado de la cadena de suministro. */
    @GetMapping("/principal")
    public String principal(Model model) {
        List<ResumenStockDTO> bajoMinimo = inventarioService.resumen().stream()
                .filter(r -> ResumenStockDTO.BAJO.equals(r.getNivel()))
                .toList();
        model.addAttribute("bajoMinimo", bajoMinimo);
        model.addAttribute("ordenesPendientes", ordenCompraService.contarPendientes());
        model.addAttribute("despachosEnTransito", despachoService.contarEnTransito());
        model.addAttribute("ultimosMovimientos", inventarioService.movimientos(null, null).stream().limit(6).toList());
        return "principal";
    }
}
