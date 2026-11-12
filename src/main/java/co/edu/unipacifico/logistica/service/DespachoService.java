package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import java.util.List;

public interface DespachoService {
    List<DespachoDTO> listar(String estado);
    DespachoDTO buscarPorId(Long id);
    String siguienteNumero();
    /** Registra el despacho y descuenta el stock de la bodega de origen. */
    DespachoDTO guardar(DespachoDTO dto);
    /** Confirma que el cliente recibió la mercancía. */
    DespachoDTO marcarEntregado(Long id);
    /** Cancela un despacho no entregado y devuelve la mercancía a la bodega. */
    DespachoDTO anular(Long id);
    long contarEnTransito();
}
