package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.OrdenCompraDTO;
import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompraDTO> listar(String estado);
    OrdenCompraDTO buscarPorId(Long id);
    String siguienteNumero();
    OrdenCompraDTO guardar(OrdenCompraDTO dto);
    /** Ingresa la mercancía a la bodega destino. Solo para órdenes PENDIENTES. */
    OrdenCompraDTO recibir(Long id);
    /** Cancela una orden que aún no se ha recibido. */
    OrdenCompraDTO anular(Long id);
    long contarPendientes();
}
