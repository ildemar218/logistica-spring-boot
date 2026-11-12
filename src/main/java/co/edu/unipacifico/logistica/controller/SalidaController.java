package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.DetalleCantidadDTO;
import co.edu.unipacifico.logistica.dto.SalidaDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.model.Salida;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.ClienteService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import co.edu.unipacifico.logistica.service.SalidaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Salidas de mercancía por venta, merma, ajuste o consumo interno. */
@Controller
@RequestMapping("/salidas")
public class SalidaController {

    private final SalidaService servicio;
    private final BodegaService bodegaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public SalidaController(SalidaService servicio, BodegaService bodegaService, ClienteService clienteService,
                            ProductoService productoService, InventarioService inventarioService) {
        this.servicio = servicio;
        this.bodegaService = bodegaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("salidaId", "estado", "usuario", "bodegaNombre", "clienteNombre",
                "*productoNombre", "*productoCodigo", "*unidadMedida");
    }

    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("salidas", servicio.listar());
        return "salidas/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        SalidaDTO dto = new SalidaDTO();
        dto.setNumero(servicio.siguienteNumero());
        dto.setFecha(LocalDate.now());
        DetalleCantidadDTO fila = new DetalleCantidadDTO();
        fila.setCantidad(1);
        dto.getDetalles().add(fila);
        model.addAttribute("salidaDto", dto);
        cargarCatalogos(model);
        return "salidas/crearSalida";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("salidaDto") SalidaDTO dto,
                          BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                SalidaDTO guardada = servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Salida " + guardada.getNumero() + " registrada. El stock se descontó.");
                return "redirect:/salidas/" + guardada.getSalidaId();
            } catch (ReglaNegocioException e) {
                Errores.rechazar(resultado, e);
            }
        }
        cargarCatalogos(model);
        return "salidas/crearSalida";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("salida", servicio.buscarPorId(id));
        return "salidas/verSalida";
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.anular(id);
            flash.addFlashAttribute("mensaje", "Salida anulada. La mercancía volvió a la bodega.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/salidas/" + id;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("tipos", Salida.TIPOS);
        model.addAttribute("bodegas", bodegaService.listarActivos());
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("stock", inventarioService.stockPorBodega());
    }
}
