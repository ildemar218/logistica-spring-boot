package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import co.edu.unipacifico.logistica.dto.DetalleDespachoDTO;
import co.edu.unipacifico.logistica.dto.Permisos;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.ClienteService;
import co.edu.unipacifico.logistica.service.DespachoService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import co.edu.unipacifico.logistica.service.UsuarioAdminService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Órdenes de despacho y seguimiento de entregas.
 * Un usuario que solo es Transportador ve y actualiza únicamente los despachos que tiene asignados.
 */
@Controller
@RequestMapping("/despachos")
public class DespachoController {

    private final DespachoService servicio;
    private final ClienteService clienteService;
    private final BodegaService bodegaService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;
    private final UsuarioAdminService usuarioService;

    public DespachoController(DespachoService servicio, ClienteService clienteService, BodegaService bodegaService,
                              ProductoService productoService, InventarioService inventarioService,
                              UsuarioAdminService usuarioService) {
        this.servicio = servicio;
        this.clienteService = clienteService;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
        this.usuarioService = usuarioService;
    }

    @InitBinder("despachoDto")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("despachoId", "estado", "*subtotal", "iva", "total", "fechaEntrega", "fechaSalida",
                "clienteNombre", "bodegaNombre", "transportadorNombre", "transportadorLogin", "motivoDevolucion",
                "seguimiento*", "*productoNombre", "*productoCodigo");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String estado,
                         @ModelAttribute("permisos") Permisos permisos, Principal principal, Model model) {
        model.addAttribute("despachos", servicio.listar(estado, soloPropios(permisos, principal)));
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
                flash.addFlashAttribute("mensaje", "Orden de despacho " + guardado.getNumero()
                        + " creada y asignada a " + guardado.getTransportadorNombre() + ". Queda PENDIENTE de salir de bodega.");
                return "redirect:/despachos/" + guardado.getDespachoId();
            } catch (ReglaNegocioException e) {
                Errores.rechazar(resultado, e);
            }
        }
        cargarCatalogos(model);
        return "despachos/crearDespacho";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, @ModelAttribute("permisos") Permisos permisos,
                      Principal principal, Model model) {
        DespachoDTO despacho = servicio.buscarPorId(id);
        String propio = soloPropios(permisos, principal);
        if (propio != null && !propio.equals(despacho.getTransportadorLogin())) {
            // Un transportador no puede ver despachos de otros: se responde como si no existiera.
            throw new RecursoNoEncontradoException("El despacho solicitado no existe");
        }
        model.addAttribute("despacho", despacho);
        if (permisos.isGestionDespachos()) {
            model.addAttribute("transportadores", usuarioService.transportadores());
        }
        return "despachos/verDespacho";
    }

    @PostMapping("/{id}/asignar")
    public String asignar(@PathVariable Long id, @RequestParam(required = false) Long transportadorId,
                          @RequestParam(required = false) String vehiculo, RedirectAttributes flash) {
        return ejecutar(id, flash, "Transportador asignado.", () -> servicio.asignar(id, transportadorId, vehiculo));
    }

    @PostMapping("/{id}/despachar")
    public String despachar(@PathVariable Long id, RedirectAttributes flash) {
        return ejecutar(id, flash, "Despacho EN RUTA. La mercancía salió de la bodega.", () -> servicio.despachar(id));
    }

    @PostMapping("/{id}/entregar")
    public String entregar(@PathVariable Long id, @RequestParam(required = false) String observacion,
                           @ModelAttribute("permisos") Permisos permisos, Principal principal,
                           RedirectAttributes flash) {
        return ejecutar(id, flash, "Entrega registrada.",
                () -> servicio.entregar(id, observacion, principal.getName(), permisos.isSoloTransportador()));
    }

    @PostMapping("/{id}/devolver")
    public String devolver(@PathVariable Long id, @RequestParam(required = false) String motivo,
                           @ModelAttribute("permisos") Permisos permisos, Principal principal,
                           RedirectAttributes flash) {
        return ejecutar(id, flash, "Devolución registrada. La mercancía volvió a la bodega.",
                () -> servicio.devolver(id, motivo, principal.getName(), permisos.isSoloTransportador()));
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, @RequestParam(required = false) String motivo,
                           RedirectAttributes flash) {
        return ejecutar(id, flash, "Despacho cancelado.", () -> servicio.cancelar(id, motivo));
    }

    private String ejecutar(Long id, RedirectAttributes flash, String exito, Runnable accion) {
        try {
            accion.run();
            flash.addFlashAttribute("mensaje", exito);
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/despachos/" + id;
    }

    /** Login del usuario si solo debe ver sus propios despachos; null si ve todos. */
    private String soloPropios(Permisos permisos, Principal principal) {
        return permisos.isSoloTransportador() ? principal.getName() : null;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("bodegas", bodegaService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("transportadores", usuarioService.transportadores());
        model.addAttribute("stock", inventarioService.stockPorBodega());
    }
}
