package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.DetalleEntradaDTO;
import co.edu.unipacifico.logistica.dto.EntradaDTO;
import co.edu.unipacifico.logistica.model.DetalleEntrada;
import co.edu.unipacifico.logistica.model.Entrada;
import org.springframework.stereotype.Component;

/** Convierte la entrada guardada en el DTO que muestran las vistas. La entrada se arma en el servicio. */
@Component
public class EntradaMapper {

    public EntradaDTO toDTO(Entrada e) {
        EntradaDTO d = new EntradaDTO();
        d.setEntradaId(e.getEntradaId());
        d.setNumero(e.getNumero());
        d.setFecha(e.getFecha());
        d.setProveedorId(e.getProveedor().getProveedorId());
        d.setProveedorNombre(e.getProveedor().getRazonSocial());
        d.setBodegaId(e.getBodega().getBodegaId());
        d.setBodegaNombre(e.getBodega().getNombre());
        d.setDocumentoProveedor(e.getDocumentoProveedor());
        d.setObservacion(e.getObservacion());
        d.setEstado(e.getEstado());
        d.setUsuario(e.getUsuario());
        d.setSubtotal(e.getSubtotal());
        d.setIva(e.getIva());
        d.setTotal(e.getTotal());
        for (DetalleEntrada det : e.getDetalles()) {
            DetalleEntradaDTO dd = new DetalleEntradaDTO();
            dd.setProductoId(det.getProducto().getProductoId());
            dd.setProductoCodigo(det.getProducto().getCodigo());
            dd.setProductoNombre(det.getProducto().getNombre());
            dd.setCantidad(det.getCantidad());
            dd.setCostoUnitario(det.getCostoUnitario());
            dd.setSubtotal(det.getSubtotal());
            d.getDetalles().add(dd);
        }
        return d;
    }
}
