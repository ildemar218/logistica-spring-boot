package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.DetalleCantidadDTO;
import co.edu.unipacifico.logistica.dto.SalidaDTO;
import co.edu.unipacifico.logistica.model.DetalleSalida;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.model.Salida;
import org.springframework.stereotype.Component;

@Component
public class SalidaMapper {

    public SalidaDTO toDTO(Salida e) {
        SalidaDTO d = new SalidaDTO();
        d.setSalidaId(e.getSalidaId());
        d.setNumero(e.getNumero());
        d.setFecha(e.getFecha());
        d.setTipo(e.getTipo());
        d.setBodegaId(e.getBodega().getBodegaId());
        d.setBodegaNombre(e.getBodega().getNombre());
        if (e.getCliente() != null) {
            d.setClienteId(e.getCliente().getClienteId());
            d.setClienteNombre(e.getCliente().getNombres());
        }
        d.setMotivo(e.getMotivo());
        d.setEstado(e.getEstado());
        d.setUsuario(e.getUsuario());
        for (DetalleSalida det : e.getDetalles()) {
            d.getDetalles().add(detalle(det.getProducto(), det.getCantidad()));
        }
        return d;
    }

    static DetalleCantidadDTO detalle(Producto p, Integer cantidad) {
        DetalleCantidadDTO dd = new DetalleCantidadDTO();
        dd.setProductoId(p.getProductoId());
        dd.setProductoCodigo(p.getCodigo());
        dd.setProductoNombre(p.getNombre());
        dd.setUnidadMedida(p.getUnidadMedida());
        dd.setCantidad(cantidad);
        return dd;
    }
}
