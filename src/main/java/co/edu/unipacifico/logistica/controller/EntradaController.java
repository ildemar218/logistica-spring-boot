package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.DetalleEntradaDTO;
import co.edu.unipacifico.logistica.dto.EntradaDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.EntradaService;
import co.edu.unipacifico.logistica.service.ProductoService;
import co.edu.unipacifico.logistica.service.ProveedorService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Entradas de mercancía por proveedor. */
@Controller
@RequestMapping("/entradas")
public class EntradaController {

    private final EntradaService servicio;
    private final ProveedorService proveedorService;
    private final BodegaService bodegaService;
    private final ProductoService productoService;

    public EntradaController(EntradaService servicio, ProveedorService proveedorService,
                             BodegaService bodegaService, ProductoService productoService) {
        this.servicio = servicio;
        this.proveedorService = proveedorService;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
    }

    /** Estado, importes, usuario y nombres los asigna el servidor. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("entradaId", "estado", "usuario", "*subtotal", "iva", "total",
                "proveedorNombre", "bodegaNombre", "*productoNombre", "*productoCodigo");
    }

    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("entradas", servicio.listar());
        return "entradas/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        EntradaDTO dto = new EntradaDTO();
        dto.setNumero(servicio.siguienteNumero());
        dto.setFecha(LocalDate.now());
        DetalleEntradaDTO fila = new DetalleEntradaDTO();
        fila.setCantidad(1);
        dto.getDetalles().add(fila);
        model.addAttribute("entradaDto", dto);
        cargarCatalogos(model);
        return "entradas/crearEntrada";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("entradaDto") EntradaDTO dto,
                          BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                EntradaDTO guardada = servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Entrada " + guardada.getNumero()
                        + " registrada. El stock de " + guardada.getBodegaNombre() + " se actualizó.");
                return "redirect:/entradas/" + guardada.getEntradaId();
            } catch (ReglaNegocioException e) {
                Errores.rechazar(resultado, e);
            }
        }
        cargarCatalogos(model);
        return "entradas/crearEntrada";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("entrada", servicio.buscarPorId(id));
        return "entradas/verEntrada";
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.anular(id);
            flash.addFlashAttribute("mensaje", "Entrada anulada. La mercancía se descontó de la bodega.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", "No se pudo anular: " + e.getMessage());
        }
        return "redirect:/entradas/" + id;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("proveedores", proveedorService.listarActivos());
        model.addAttribute("bodegas", bodegaService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
    }
}
