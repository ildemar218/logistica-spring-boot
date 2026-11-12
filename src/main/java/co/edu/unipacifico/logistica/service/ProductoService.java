package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ProductoDTO;
import java.util.List;

public interface ProductoService {
    List<ProductoDTO> listar();
    List<ProductoDTO> listarActivos();
    List<ProductoDTO> buscar(String filtro);
    ProductoDTO buscarPorId(Long id);
    ProductoDTO guardar(ProductoDTO dto);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    void eliminar(Long id);
}
