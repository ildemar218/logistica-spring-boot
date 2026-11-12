package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.DetalleCantidadDTO;
import co.edu.unipacifico.logistica.dto.TransferenciaDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import co.edu.unipacifico.logistica.service.TransferenciaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Transferencias de mercancía entre bodegas. */
@Controller
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService servicio;
    private final BodegaService bodegaService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public TransferenciaController(TransferenciaService servicio, BodegaService bodegaService,
                                   ProductoService productoService, InventarioService inventarioService) {
        this.servicio = servicio;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("transferenciaId", "usuario", "bodegaOrigenNombre", "bodegaDestinoNombre",
                "*productoNombre", "*productoCodigo", "*unidadMedida");
    }

    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("transferencias", servicio.listar());
        return "transferencias/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        TransferenciaDTO dto = new TransferenciaDTO();
        dto.setNumero(servicio.siguienteNumero());
        dto.setFecha(LocalDate.now());
        DetalleCantidadDTO fila = new DetalleCantidadDTO();
        fila.setCantidad(1);
        dto.getDetalles().add(fila);
        model.addAttribute("transferenciaDto", dto);
        cargarCatalogos(model);
        return "transferencias/crearTransferencia";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("transferenciaDto") TransferenciaDTO dto,
                          BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                TransferenciaDTO guardada = servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Transferencia " + guardada.getNumero() + " registrada: la mercancía salió de "
                        + guardada.getBodegaOrigenNombre() + " y entró a " + guardada.getBodegaDestinoNombre() + ".");
                return "redirect:/transferencias/" + guardada.getTransferenciaId();
            } catch (ReglaNegocioException e) {
                Errores.rechazar(resultado, e);
            }
        }
        cargarCatalogos(model);
        return "transferencias/crearTransferencia";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        TransferenciaDTO t = servicio.buscarPorId(id);
        model.addAttribute("transferencia", t);
        // Existencias actuales en origen y destino, para verificar el resultado.
        model.addAttribute("stockOrigen", inventarioService.existencias(t.getBodegaOrigenId(), null));
        model.addAttribute("stockDestino", inventarioService.existencias(t.getBodegaDestinoId(), null));
        return "transferencias/verTransferencia";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("bodegas", bodegaService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("stock", inventarioService.stockPorBodega());
    }
}
