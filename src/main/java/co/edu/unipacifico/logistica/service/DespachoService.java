package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import java.util.List;

public interface DespachoService {
    /** login: si no es null, solo los despachos asignados a ese transportador. */
    List<DespachoDTO> listar(String estado, String login);
    DespachoDTO buscarPorId(Long id);
    String siguienteNumero();
    /** Crea la orden de despacho en estado PENDIENTE. Comprueba existencias, pero aún no las descuenta. */
    DespachoDTO guardar(DespachoDTO dto);
    /** Cambia el transportador o el vehículo mientras el despacho está PENDIENTE. */
    DespachoDTO asignar(Long id, Long transportadorId, String vehiculo);
    /** PENDIENTE → EN_RUTA: la mercancía sale de la bodega. */
    DespachoDTO despachar(Long id);
    /** EN_RUTA → ENTREGADO. soloAsignado: el usuario solo puede actuar sobre sus propios despachos. */
    DespachoDTO entregar(Long id, String observacion, String login, boolean soloAsignado);
    /** EN_RUTA → DEVUELTO: la mercancía vuelve a la bodega de origen. */
    DespachoDTO devolver(Long id, String motivo, String login, boolean soloAsignado);
    /** PENDIENTE → CANCELADO. */
    DespachoDTO cancelar(Long id, String motivo);
    long contar(String estado, String login);
}
