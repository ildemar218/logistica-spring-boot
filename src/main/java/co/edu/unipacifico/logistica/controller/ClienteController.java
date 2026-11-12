package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.ClienteDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService servicio;

    public ClienteController(ClienteService servicio) {
        this.servicio = servicio;
    }

    /** Estos campos los asigna el servidor; no se aceptan desde el formulario. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("clienteId", "fechaCreacion", "fechaActualizacion");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String filtro, Model model) {
        model.addAttribute("clientes", filtro != null && !filtro.isBlank()
                ? servicio.buscar(filtro) : servicio.listar());
        model.addAttribute("filtro", filtro);
        return "clientes/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        ClienteDTO dto = new ClienteDTO();
        dto.setEstado(true);
        model.addAttribute("clienteDto", dto);
        return "clientes/crearCliente";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("clienteDto") ClienteDTO dto,
                          BindingResult resultado, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Cliente creado.");
                return "redirect:/clientes/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "clientes/crearCliente";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("clienteDto", servicio.buscarPorId(id));
        return "clientes/editarCliente";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("clienteDto") ClienteDTO dto,
                             BindingResult resultado, RedirectAttributes flash) {
        dto.setClienteId(id);
        if (!resultado.hasErrors()) {
            try {
                servicio.actualizar(id, dto);
                flash.addFlashAttribute("mensaje", "Cliente actualizado.");
                return "redirect:/clientes/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "clientes/editarCliente";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Cliente eliminado.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar: tiene registros relacionados. Puede marcarlo como Inactivo.");
        }
        return "redirect:/clientes/";
    }

    private void rechazar(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
        else resultado.reject("regla", e.getMessage());
    }
}
