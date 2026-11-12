package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.DetalleOrdenCompraDTO;
import co.edu.unipacifico.logistica.dto.OrdenCompraDTO;
import co.edu.unipacifico.logistica.model.DetalleOrdenCompra;
import co.edu.unipacifico.logistica.model.OrdenCompra;
import org.springframework.stereotype.Component;

/** Convierte la orden guardada en el DTO que muestran las vistas. La orden se arma en el servicio. */
@Component
public class OrdenCompraMapper {

    public OrdenCompraDTO toDTO(OrdenCompra e) {
        OrdenCompraDTO d = new OrdenCompraDTO();
        d.setOrdenId(e.getOrdenId());
        d.setNumero(e.getNumero());
        d.setFecha(e.getFecha());
        d.setFechaEntregaEsperada(e.getFechaEntregaEsperada());
        d.setProveedorId(e.getProveedor().getProveedorId());
        d.setProveedorNombre(e.getProveedor().getRazonSocial());
        d.setBodegaId(e.getBodega().getBodegaId());
        d.setBodegaNombre(e.getBodega().getNombre());
        d.setObservacion(e.getObservacion());
        d.setEstado(e.getEstado());
        d.setSubtotal(e.getSubtotal());
        d.setIva(e.getIva());
        d.setTotal(e.getTotal());
        d.setFechaRecepcion(e.getFechaRecepcion());
        for (DetalleOrdenCompra det : e.getDetalles()) {
            d.getDetalles().add(toDTO(det));
        }
        return d;
    }

    private DetalleOrdenCompraDTO toDTO(DetalleOrdenCompra e) {
        DetalleOrdenCompraDTO d = new DetalleOrdenCompraDTO();
        d.setProductoId(e.getProducto().getProductoId());
        d.setProductoCodigo(e.getProducto().getCodigo());
        d.setProductoNombre(e.getProducto().getNombre());
        d.setCantidad(e.getCantidad());
        d.setCostoUnitario(e.getCostoUnitario());
        d.setSubtotal(e.getSubtotal());
        return d;
    }
}
