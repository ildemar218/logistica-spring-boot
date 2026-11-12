package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {
    List<ClienteDTO> listar();
    List<ClienteDTO> listarActivos();
    List<ClienteDTO> buscar(String filtro);
    ClienteDTO buscarPorId(Long id);
    ClienteDTO guardar(ClienteDTO dto);
    ClienteDTO actualizar(Long id, ClienteDTO dto);
    void eliminar(Long id);
}
