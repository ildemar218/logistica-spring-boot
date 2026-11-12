package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.CategoriaDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    /** Estos campos los asigna el servidor; no se aceptan desde el formulario. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("categoriaId");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String filtro, Model model) {
        model.addAttribute("categorias", filtro != null && !filtro.isBlank()
                ? servicio.buscar(filtro) : servicio.listar());
        model.addAttribute("filtro", filtro);
        return "categorias/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setEstado(true);
        model.addAttribute("categoriaDto", dto);
        return "categorias/crearCategoria";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("categoriaDto") CategoriaDTO dto,
                          BindingResult resultado, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Categoría creada.");
                return "redirect:/categorias/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "categorias/crearCategoria";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoriaDto", servicio.buscarPorId(id));
        return "categorias/editarCategoria";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("categoriaDto") CategoriaDTO dto,
                             BindingResult resultado, RedirectAttributes flash) {
        dto.setCategoriaId(id);
        if (!resultado.hasErrors()) {
            try {
                servicio.actualizar(id, dto);
                flash.addFlashAttribute("mensaje", "Categoría actualizada.");
                return "redirect:/categorias/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        return "categorias/editarCategoria";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Categoría eliminada.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar: tiene registros relacionados. Puede marcarla como Inactiva.");
        }
        return "redirect:/categorias/";
    }

    private void rechazar(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
        else resultado.reject("regla", e.getMessage());
    }
}
