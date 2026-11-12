package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import co.edu.unipacifico.logistica.dto.DetalleDespachoDTO;
import co.edu.unipacifico.logistica.model.Despacho;
import co.edu.unipacifico.logistica.model.DetalleDespacho;
import org.springframework.stereotype.Component;

/** Convierte el despacho guardado en el DTO que muestran las vistas. El despacho se arma en el servicio. */
@Component
public class DespachoMapper {

    public DespachoDTO toDTO(Despacho e) {
        DespachoDTO d = new DespachoDTO();
        d.setDespachoId(e.getDespachoId());
        d.setNumero(e.getNumero());
        d.setFecha(e.getFecha());
        d.setClienteId(e.getCliente().getClienteId());
        d.setClienteNombre(e.getCliente().getNombres());
        d.setBodegaId(e.getBodega().getBodegaId());
        d.setBodegaNombre(e.getBodega().getNombre());
        d.setDireccionEntrega(e.getDireccionEntrega());
        d.setTransportista(e.getTransportista());
        d.setNumeroGuia(e.getNumeroGuia());
        d.setObservacion(e.getObservacion());
        d.setEstado(e.getEstado());
        d.setSubtotal(e.getSubtotal());
        d.setIva(e.getIva());
        d.setTotal(e.getTotal());
        d.setFechaEntrega(e.getFechaEntrega());
        for (DetalleDespacho det : e.getDetalles()) {
            d.getDetalles().add(toDTO(det));
        }
        return d;
    }

    private DetalleDespachoDTO toDTO(DetalleDespacho e) {
        DetalleDespachoDTO d = new DetalleDespachoDTO();
        d.setProductoId(e.getProducto().getProductoId());
        d.setProductoCodigo(e.getProducto().getCodigo());
        d.setProductoNombre(e.getProducto().getNombre());
        d.setCantidad(e.getCantidad());
        d.setPrecioUnitario(e.getPrecioUnitario());
        d.setSubtotal(e.getSubtotal());
        return d;
    }
}
