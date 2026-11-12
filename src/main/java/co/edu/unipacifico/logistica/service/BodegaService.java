package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.BodegaDTO;
import java.util.List;

public interface BodegaService {
    List<BodegaDTO> listar();
    List<BodegaDTO> listarActivos();
    List<BodegaDTO> buscar(String filtro);
    BodegaDTO buscarPorId(Long id);
    BodegaDTO guardar(BodegaDTO dto);
    BodegaDTO actualizar(Long id, BodegaDTO dto);
    void eliminar(Long id);
}
