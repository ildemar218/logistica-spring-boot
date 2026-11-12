package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Consultas de inventario: solo lectura. El stock cambia con órdenes de compra y despachos. */
@Controller
public class InventarioController {

    private final InventarioService inventarioService;
    private final BodegaService bodegaService;
    private final ProductoService productoService;

    public InventarioController(InventarioService inventarioService, BodegaService bodegaService,
                                ProductoService productoService) {
        this.inventarioService = inventarioService;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
    }

    @GetMapping({"/existencias", "/existencias/"})
    public String existencias(@RequestParam(required = false) Long bodegaId, Model model) {
        model.addAttribute("resumen", inventarioService.resumen());
        model.addAttribute("existencias", inventarioService.existencias(bodegaId));
        model.addAttribute("bodegas", bodegaService.listar());
        model.addAttribute("bodegaId", bodegaId);
        return "inventario/existencias";
    }

    @GetMapping({"/movimientos", "/movimientos/"})
    public String movimientos(@RequestParam(required = false) Long productoId,
                              @RequestParam(required = false) Long bodegaId, Model model) {
        model.addAttribute("movimientos", inventarioService.movimientos(productoId, bodegaId));
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("bodegas", bodegaService.listar());
        model.addAttribute("productoId", productoId);
        model.addAttribute("bodegaId", bodegaId);
        return "inventario/movimientos";
    }
}
