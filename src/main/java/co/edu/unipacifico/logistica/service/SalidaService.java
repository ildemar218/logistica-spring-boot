package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.SalidaDTO;
import java.util.List;

public interface SalidaService {
    List<SalidaDTO> listar();
    SalidaDTO buscarPorId(Long id);
    String siguienteNumero();
    /** Descuenta el stock de la bodega. Rechaza la salida si algún producto no tiene existencias. */
    SalidaDTO guardar(SalidaDTO dto);
    /** Devuelve la mercancía a la bodega. */
    SalidaDTO anular(Long id);
}
