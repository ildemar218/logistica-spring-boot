package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.TransferenciaDTO;
import co.edu.unipacifico.logistica.model.DetalleTransferencia;
import co.edu.unipacifico.logistica.model.Transferencia;
import org.springframework.stereotype.Component;

@Component
public class TransferenciaMapper {

    public TransferenciaDTO toDTO(Transferencia e) {
        TransferenciaDTO d = new TransferenciaDTO();
        d.setTransferenciaId(e.getTransferenciaId());
        d.setNumero(e.getNumero());
        d.setFecha(e.getFecha());
        d.setBodegaOrigenId(e.getBodegaOrigen().getBodegaId());
        d.setBodegaOrigenNombre(e.getBodegaOrigen().getNombre());
        d.setBodegaDestinoId(e.getBodegaDestino().getBodegaId());
        d.setBodegaDestinoNombre(e.getBodegaDestino().getNombre());
        d.setResponsable(e.getResponsable());
        d.setObservacion(e.getObservacion());
        d.setUsuario(e.getUsuario());
        for (DetalleTransferencia det : e.getDetalles()) {
            d.getDetalles().add(SalidaMapper.detalle(det.getProducto(), det.getCantidad()));
        }
        return d;
    }
}
