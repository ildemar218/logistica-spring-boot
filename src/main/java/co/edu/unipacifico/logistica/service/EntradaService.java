package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.EntradaDTO;
import java.util.List;

public interface EntradaService {
    List<EntradaDTO> listar();
    EntradaDTO buscarPorId(Long id);
    String siguienteNumero();
    /** Registra la mercancía recibida y suma el stock en la bodega. */
    EntradaDTO guardar(EntradaDTO dto);
    /** Revierte la entrada, solo si la mercancía sigue en la bodega. */
    EntradaDTO anular(Long id);
}
