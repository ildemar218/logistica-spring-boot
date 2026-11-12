package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.TransferenciaDTO;
import java.util.List;

public interface TransferenciaService {
    List<TransferenciaDTO> listar();
    TransferenciaDTO buscarPorId(Long id);
    String siguienteNumero();
    /** Saca la mercancía de la bodega de origen y la ingresa en la de destino, en una sola transacción. */
    TransferenciaDTO guardar(TransferenciaDTO dto);
}
