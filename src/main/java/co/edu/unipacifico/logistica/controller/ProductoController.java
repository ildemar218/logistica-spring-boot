package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.ProductoDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.service.CategoriaService;
import co.edu.unipacifico.logistica.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService servicio;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService servicio, CategoriaService categoriaService) {
        this.servicio = servicio;
        this.categoriaService = categoriaService;
    }

    /** Estos campos los asigna el servidor; no se aceptan desde el formulario. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("productoId", "fechaCreacion", "fechaActualizacion", "categoriaNombre");
    }

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) String filtro, Model model) {
        model.addAttribute("productos", filtro != null && !filtro.isBlank()
                ? servicio.buscar(filtro) : servicio.listar());
        model.addAttribute("filtro", filtro);
        return "productos/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        ProductoDTO dto = new ProductoDTO();
        dto.setEstado(true);
        model.addAttribute("productoDto", dto);
        cargarCatalogos(model);
        return "productos/crearProducto";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute("productoDto") ProductoDTO dto,
                          BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                servicio.guardar(dto);
                flash.addFlashAttribute("mensaje", "Producto creado.");
                return "redirect:/productos/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        cargarCatalogos(model);
        return "productos/crearProducto";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("productoDto", servicio.buscarPorId(id));
        cargarCatalogos(model);
        return "productos/editarProducto";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("productoDto") ProductoDTO dto,
                             BindingResult resultado, Model model, RedirectAttributes flash) {
        dto.setProductoId(id);
        if (!resultado.hasErrors()) {
            try {
                servicio.actualizar(id, dto);
                flash.addFlashAttribute("mensaje", "Producto actualizado.");
                return "redirect:/productos/";
            } catch (ReglaNegocioException e) {
                rechazar(resultado, e);
            }
        }
        cargarCatalogos(model);
        return "productos/editarProducto";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Producto eliminado.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar: tiene registros relacionados. Puede marcarlo como Inactivo.");
        }
        return "redirect:/productos/";
    }

    private void rechazar(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() != null) resultado.rejectValue(e.getCampo(), "regla", e.getMessage());
        else resultado.reject("regla", e.getMessage());
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("categorias", categoriaService.listarActivos());
    }
}
