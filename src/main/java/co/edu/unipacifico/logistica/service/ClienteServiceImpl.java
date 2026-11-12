package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ClienteDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.ClienteMapper;
import co.edu.unipacifico.logistica.model.Cliente;
import co.edu.unipacifico.logistica.repository.ClienteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    public ClienteServiceImpl(ClienteRepository repository,
            ClienteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarActivos() {
        return repository.findByEstadoTrueOrderByNombres().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscar(String filtro) {
        String texto = filtro.trim();
        return repository.findByNumeroDocumentoContainingIgnoreCaseOrNombresContainingIgnoreCase(texto, texto)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    public ClienteDTO guardar(ClienteDTO dto) {
        validar(null, dto);
        Cliente entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        Cliente entity = obtener(id);
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
    private void validar(Long id, ClienteDTO dto) {
        if (id == null ? repository.existsByNumeroDocumentoIgnoreCase(dto.getNumeroDocumento().trim())
                : repository.existsByNumeroDocumentoIgnoreCaseAndClienteIdNot(dto.getNumeroDocumento().trim(), id)) {
            throw new ReglaNegocioException("numeroDocumento", "El número de documento ya está registrado");
        }
    }

    private Cliente obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("El cliente solicitado no existe"));
    }
}
