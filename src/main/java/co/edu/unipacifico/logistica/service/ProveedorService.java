package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ProveedorDTO;
import java.util.List;

public interface ProveedorService {
    List<ProveedorDTO> listar();
    List<ProveedorDTO> listarActivos();
    List<ProveedorDTO> buscar(String filtro);
    ProveedorDTO buscarPorId(Long id);
    ProveedorDTO guardar(ProveedorDTO dto);
    ProveedorDTO actualizar(Long id, ProveedorDTO dto);
    void eliminar(Long id);
}
