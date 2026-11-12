package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.CategoriaDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.CategoriaMapper;
import co.edu.unipacifico.logistica.model.Categoria;
import co.edu.unipacifico.logistica.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    public CategoriaServiceImpl(CategoriaRepository repository,
            CategoriaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarActivos() {
        return repository.findByEstadoTrueOrderByNombre().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> buscar(String filtro) {
        String texto = filtro.trim();
        return repository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(texto, texto)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    public CategoriaDTO guardar(CategoriaDTO dto) {
        validar(null, dto);
        Categoria entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria entity = obtener(id);
        validar(id, dto);
        mapper.actualizarEntity(entity, dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        repository.delete(obtener(id));
        // flush envía el DELETE ya: si hay registros relacionados, el error se detecta aquí.
        repository.flush();
    }

    /** Reglas del servidor. En edición (id distinto de null) se excluye el propio registro. */
    private void validar(Long id, CategoriaDTO dto) {
        if (id == null ? repository.existsByNombreIgnoreCase(dto.getNombre().trim())
                : repository.existsByNombreIgnoreCaseAndCategoriaIdNot(dto.getNombre().trim(), id)) {
            throw new ReglaNegocioException("nombre", "Ya existe una categoría con ese nombre");
        }
    }

    private Categoria obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("La categoría solicitada no existe"));
    }
}
