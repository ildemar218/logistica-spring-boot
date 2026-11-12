package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Consulta de inventario por producto y bodega, y kardex. Solo lectura. */
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
    public String existencias(@RequestParam(required = false) Long bodegaId,
                              @RequestParam(required = false) Long productoId, Model model) {
        model.addAttribute("resumen", inventarioService.resumen());
        model.addAttribute("existencias", inventarioService.existencias(bodegaId, productoId));
        model.addAttribute("bodegas", bodegaService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("bodegaId", bodegaId);
        model.addAttribute("productoId", productoId);
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
