package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import co.edu.unipacifico.logistica.dto.DetalleDespachoDTO;
import co.edu.unipacifico.logistica.dto.SeguimientoDTO;
import co.edu.unipacifico.logistica.model.Despacho;
import co.edu.unipacifico.logistica.model.DetalleDespacho;
import co.edu.unipacifico.logistica.model.SeguimientoDespacho;
import co.edu.unipacifico.logistica.model.Usuarios;
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
        Usuarios t = e.getTransportador();
        d.setTransportadorId(t.getId());
        d.setTransportadorLogin(t.getLogin());
        d.setTransportadorNombre(nombre(t));
        d.setVehiculo(e.getVehiculo());
        d.setNumeroGuia(e.getNumeroGuia());
        d.setObservacion(e.getObservacion());
        d.setMotivoDevolucion(e.getMotivoDevolucion());
        d.setEstado(e.getEstado());
        d.setSubtotal(e.getSubtotal());
        d.setIva(e.getIva());
        d.setTotal(e.getTotal());
        d.setFechaSalida(e.getFechaSalida());
        d.setFechaEntrega(e.getFechaEntrega());
        for (DetalleDespacho det : e.getDetalles()) {
            DetalleDespachoDTO dd = new DetalleDespachoDTO();
            dd.setProductoId(det.getProducto().getProductoId());
            dd.setProductoCodigo(det.getProducto().getCodigo());
            dd.setProductoNombre(det.getProducto().getNombre());
            dd.setCantidad(det.getCantidad());
            dd.setPrecioUnitario(det.getPrecioUnitario());
            dd.setSubtotal(det.getSubtotal());
            d.getDetalles().add(dd);
        }
        for (SeguimientoDespacho s : e.getSeguimiento()) {
            d.getSeguimiento().add(new SeguimientoDTO(s.getFecha(), s.getEstado(), s.getUsuario(), s.getObservacion()));
        }
        return d;
    }

    public static String nombre(Usuarios u) {
        String n = ((u.getNombre() == null ? "" : u.getNombre()) + " "
                + (u.getApellido() == null ? "" : u.getApellido())).trim();
        return n.isEmpty() ? u.getLogin() : n;
    }
}
