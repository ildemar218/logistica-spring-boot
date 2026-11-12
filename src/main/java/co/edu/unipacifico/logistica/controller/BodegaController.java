package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.BodegaDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.BodegaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bodegas")
public class BodegaController {

    private final BodegaService servicio;

    public BodegaController(BodegaService servicio) {
        this.servicio = servicio;
    }

    /** Estos campos los asigna el servidor; no se aceptan desde el formulario. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("bodegaId");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String filtro, Model model) {
        model.addAttribute("bodegas", filtro != null && !filtro.isBlank()
                ? servicio.buscar(filtro) : servicio.listar());
        model.addAttribute("filtro", filtro);
        return "bodegas/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        BodegaDTO dto = new BodegaDTO();
        dto.setEstado(true);
        model.addAttribute("bodegaDto", dto);
        return "bodegas/crearBodega";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("bodegaDto") BodegaDTO dto,
                          BindingResult resultado, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Bodega creada.");
                return "redirect:/bodegas/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "bodegas/crearBodega";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("bodegaDto", servicio.buscarPorId(id));
        return "bodegas/editarBodega";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("bodegaDto") BodegaDTO dto,
                             BindingResult resultado, RedirectAttributes flash) {
        dto.setBodegaId(id);
        if (!resultado.hasErrors()) {
            try {
                servicio.actualizar(id, dto);
                flash.addFlashAttribute("mensaje", "Bodega actualizada.");
                return "redirect:/bodegas/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "bodegas/editarBodega";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Bodega eliminada.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar: tiene registros relacionados. Puede marcarla como Inactiva.");
        }
        return "redirect:/bodegas/";
    }

    private void rechazar(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
        else resultado.reject("regla", e.getMessage());
    }
}
