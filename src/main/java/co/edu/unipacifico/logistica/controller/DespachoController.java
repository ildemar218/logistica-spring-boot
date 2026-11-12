package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import co.edu.unipacifico.logistica.dto.DetalleDespachoDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.ClienteService;
import co.edu.unipacifico.logistica.service.DespachoService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/despachos")
public class DespachoController {

    private final DespachoService servicio;
    private final ClienteService clienteService;
    private final BodegaService bodegaService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public DespachoController(DespachoService servicio, ClienteService clienteService,
                              BodegaService bodegaService, ProductoService productoService,
                              InventarioService inventarioService) {
        this.servicio = servicio;
        this.clienteService = clienteService;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    /** Estado, importes y nombres los asigna el servidor. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("despachoId", "estado", "*subtotal", "iva", "total", "fechaEntrega",
                "clienteNombre", "bodegaNombre", "*productoNombre", "*productoCodigo");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String estado, Model model) {
        model.addAttribute("despachos", servicio.listar(estado));
        model.addAttribute("estado", estado);
        return "despachos/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        DespachoDTO dto = new DespachoDTO();
        dto.setNumero(servicio.siguienteNumero());
        dto.setFecha(LocalDate.now());
        DetalleDespachoDTO fila = new DetalleDespachoDTO();
        fila.setCantidad(1);
        dto.getDetalles().add(fila);
        model.addAttribute("despachoDto", dto);
        cargarCatalogos(model);
        return "despachos/crearDespacho";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("despachoDto") DespachoDTO dto,
                          BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                DespachoDTO guardado = servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Despacho " + guardado.getNumero() + " registrado. El stock ya se descontó.");
                return "redirect:/despachos/" + guardado.getDespachoId();
            } catch (ReglaNegocioException e) {
                if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
                else resultado.reject("regla", e.getMessage());
            }
        }
        cargarCatalogos(model);
        return "despachos/crearDespacho";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("despacho", servicio.buscarPorId(id));
        return "despachos/verDespacho";
    }

    @PostMapping("/{id}/entregar")
    public String entregar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.marcarEntregado(id);
            flash.addFlashAttribute("mensaje", "Despacho marcado como entregado.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/despachos/" + id;
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.anular(id);
            flash.addFlashAttribute("mensaje", "Despacho anulado. La mercancía volvió a la bodega.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/despachos/" + id;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("bodegas", bodegaService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("stock", inventarioService.stockPorBodega());
    }
}
