package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.BodegaDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.BodegaMapper;
import co.edu.unipacifico.logistica.model.Bodega;
import co.edu.unipacifico.logistica.repository.BodegaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository repository;
    private final BodegaMapper mapper;

    public BodegaServiceImpl(BodegaRepository repository,
            BodegaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaDTO> listarActivos() {
        return repository.findByEstadoTrueOrderByNombre().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaDTO> buscar(String filtro) {
        String texto = filtro.trim();
        return repository.findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCase(texto, texto)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BodegaDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    public BodegaDTO guardar(BodegaDTO dto) {
        validar(null, dto);
        Bodega entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public BodegaDTO actualizar(Long id, BodegaDTO dto) {
        Bodega entity = obtener(id);
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
    private void validar(Long id, BodegaDTO dto) {
        if (id == null ? repository.existsByCodigoIgnoreCase(dto.getCodigo().trim())
                : repository.existsByCodigoIgnoreCaseAndBodegaIdNot(dto.getCodigo().trim(), id)) {
            throw new ReglaNegocioException("codigo", "Ya existe una bodega con ese código");
        }
    }

    private Bodega obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("La bodega solicitada no existe"));
    }
}
