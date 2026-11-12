package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ProductoDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.ProductoMapper;
import co.edu.unipacifico.logistica.model.Categoria;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.repository.CategoriaRepository;
import co.edu.unipacifico.logistica.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository repository,
            ProductoMapper mapper,
            CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return repository.findByEstadoTrueOrderByNombre().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscar(String filtro) {
        String texto = filtro.trim();
        return repository.findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCase(texto, texto)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    public ProductoDTO guardar(ProductoDTO dto) {
        validar(null, dto);
        Producto entity = mapper.toEntity(dto);
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId()).orElseThrow(
                () -> new ReglaNegocioException("categoriaId", "La categoría seleccionada no existe"));
        entity.setCategoria(categoria);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto entity = obtener(id);
        validar(id, dto);
        mapper.actualizarEntity(entity, dto);
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId()).orElseThrow(
                () -> new ReglaNegocioException("categoriaId", "La categoría seleccionada no existe"));
        entity.setCategoria(categoria);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        repository.delete(obtener(id));
        // flush envía el DELETE ya: si hay registros relacionados, el error se detecta aquí.
        repository.flush();
    }

    /** Reglas del servidor. En edición (id distinto de null) se excluye el propio registro. */
    private void validar(Long id, ProductoDTO dto) {
        if (id == null ? repository.existsByCodigoIgnoreCase(dto.getCodigo().trim())
                : repository.existsByCodigoIgnoreCaseAndProductoIdNot(dto.getCodigo().trim(), id)) {
            throw new ReglaNegocioException("codigo", "Ya existe un producto con ese código");
        }
        if (dto.getStockMaximo() < dto.getStockMinimo()) {
            throw new ReglaNegocioException("stockMaximo",
                    "El stock máximo no puede ser menor que el stock mínimo");
        }
    }

    private Producto obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("El producto solicitado no existe"));
    }
}
