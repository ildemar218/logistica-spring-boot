package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.DetalleOrdenCompraDTO;
import co.edu.unipacifico.logistica.dto.OrdenCompraDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.OrdenCompraService;
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

@Controller
@RequestMapping("/ordenes-compra")
public class OrdenCompraController {

    private final OrdenCompraService servicio;
    private final ProveedorService proveedorService;
    private final BodegaService bodegaService;
    private final ProductoService productoService;

    public OrdenCompraController(OrdenCompraService servicio, ProveedorService proveedorService,
                                 BodegaService bodegaService, ProductoService productoService) {
        this.servicio = servicio;
        this.proveedorService = proveedorService;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
    }

    /** Estado, importes y nombres los asigna el servidor. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("ordenId", "estado", "*subtotal", "iva", "total", "fechaRecepcion",
                "proveedorNombre", "bodegaNombre", "*productoNombre", "*productoCodigo");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String estado, Model model) {
        model.addAttribute("ordenes", servicio.listar(estado));
        model.addAttribute("estado", estado);
        return "ordenes-compra/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        OrdenCompraDTO dto = new OrdenCompraDTO();
        dto.setNumero(servicio.siguienteNumero());
        dto.setFecha(LocalDate.now());
        DetalleOrdenCompraDTO fila = new DetalleOrdenCompraDTO();
        fila.setCantidad(1);
        dto.getDetalles().add(fila);
        model.addAttribute("ordenDto", dto);
        cargarCatalogos(model);
        return "ordenes-compra/crearOrden";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("ordenDto") OrdenCompraDTO dto,
                          BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                OrdenCompraDTO guardada = servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Orden " + guardada.getNumero() + " registrada. Queda pendiente de recibir.");
                return "redirect:/ordenes-compra/" + guardada.getOrdenId();
            } catch (ReglaNegocioException e) {
                if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
                else resultado.reject("regla", e.getMessage());
            }
        }
        cargarCatalogos(model);
        return "ordenes-compra/crearOrden";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("orden", servicio.buscarPorId(id));
        return "ordenes-compra/verOrden";
    }

    @PostMapping("/{id}/recibir")
    public String recibir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.recibir(id);
            flash.addFlashAttribute("mensaje", "Mercancía recibida. Las existencias de la bodega se actualizaron.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes-compra/" + id;
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.anular(id);
            flash.addFlashAttribute("mensaje", "Orden anulada.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes-compra/" + id;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("proveedores", proveedorService.listarActivos());
        model.addAttribute("bodegas", bodegaService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
    }
}
