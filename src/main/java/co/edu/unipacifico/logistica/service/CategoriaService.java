package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.CategoriaDTO;
import java.util.List;

public interface CategoriaService {
    List<CategoriaDTO> listar();
    List<CategoriaDTO> listarActivos();
    List<CategoriaDTO> buscar(String filtro);
    CategoriaDTO buscarPorId(Long id);
    CategoriaDTO guardar(CategoriaDTO dto);
    CategoriaDTO actualizar(Long id, CategoriaDTO dto);
    void eliminar(Long id);
}
