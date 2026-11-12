package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ProveedorDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.ProveedorMapper;
import co.edu.unipacifico.logistica.model.Proveedor;
import co.edu.unipacifico.logistica.repository.ProveedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;

    public ProveedorServiceImpl(ProveedorRepository repository,
            ProveedorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarActivos() {
        return repository.findByEstadoTrueOrderByRazonSocial().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> buscar(String filtro) {
        String texto = filtro.trim();
        return repository.findByNumeroDocumentoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(texto, texto)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    public ProveedorDTO guardar(ProveedorDTO dto) {
        validar(null, dto);
        Proveedor entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ProveedorDTO actualizar(Long id, ProveedorDTO dto) {
        Proveedor entity = obtener(id);
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
    private void validar(Long id, ProveedorDTO dto) {
        if (id == null ? repository.existsByNumeroDocumentoIgnoreCase(dto.getNumeroDocumento().trim())
                : repository.existsByNumeroDocumentoIgnoreCaseAndProveedorIdNot(dto.getNumeroDocumento().trim(), id)) {
            throw new ReglaNegocioException("numeroDocumento", "El número de documento ya está registrado");
        }
    }

    private Proveedor obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("El proveedor solicitado no existe"));
    }
}
