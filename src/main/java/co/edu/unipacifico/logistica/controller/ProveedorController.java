package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.ProveedorDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService servicio;

    public ProveedorController(ProveedorService servicio) {
        this.servicio = servicio;
    }

    /** Estos campos los asigna el servidor; no se aceptan desde el formulario. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("proveedorId", "fechaCreacion", "fechaActualizacion");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String filtro, Model model) {
        model.addAttribute("proveedores", filtro != null && !filtro.isBlank()
                ? servicio.buscar(filtro) : servicio.listar());
        model.addAttribute("filtro", filtro);
        return "proveedores/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setEstado(true);
        model.addAttribute("proveedorDto", dto);
        return "proveedores/crearProveedor";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("proveedorDto") ProveedorDTO dto,
                          BindingResult resultado, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Proveedor creado.");
                return "redirect:/proveedores/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "proveedores/crearProveedor";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("proveedorDto", servicio.buscarPorId(id));
        return "proveedores/editarProveedor";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("proveedorDto") ProveedorDTO dto,
                             BindingResult resultado, RedirectAttributes flash) {
        dto.setProveedorId(id);
        if (!resultado.hasErrors()) {
            try {
                servicio.actualizar(id, dto);
                flash.addFlashAttribute("mensaje", "Proveedor actualizado.");
                return "redirect:/proveedores/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "proveedores/editarProveedor";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Proveedor eliminado.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar: tiene registros relacionados. Puede marcarlo como Inactivo.");
        }
        return "redirect:/proveedores/";
    }

    private void rechazar(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
        else resultado.reject("regla", e.getMessage());
    }
}
