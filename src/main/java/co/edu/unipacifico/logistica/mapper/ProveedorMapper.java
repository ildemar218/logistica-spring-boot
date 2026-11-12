package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.ProveedorDTO;
import co.edu.unipacifico.logistica.model.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public ProveedorDTO toDTO(Proveedor e) {
        if (e == null) return null;
        ProveedorDTO d = new ProveedorDTO();
        d.setProveedorId(e.getProveedorId());
        d.setTipoDocumento(e.getTipoDocumento());
        d.setNumeroDocumento(e.getNumeroDocumento());
        d.setRazonSocial(e.getRazonSocial());
        d.setContacto(e.getContacto());
        d.setTelefono(e.getTelefono());
        d.setCorreo(e.getCorreo());
        d.setDireccion(e.getDireccion());
        d.setCiudad(e.getCiudad());
        d.setEstado(e.getEstado());
        d.setFechaCreacion(e.getFechaCreacion());
        d.setFechaActualizacion(e.getFechaActualizacion());
        return d;
    }

    public Proveedor toEntity(ProveedorDTO d) {
        if (d == null) return null;
        Proveedor e = new Proveedor();
        actualizarEntity(e, d);
        return e;
    }

    /** Copia solo los datos editables; conserva el id y las fechas. */
    public void actualizarEntity(Proveedor e, ProveedorDTO d) {
        e.setTipoDocumento(limpiar(d.getTipoDocumento()));
        e.setNumeroDocumento(limpiar(d.getNumeroDocumento()));
        e.setRazonSocial(limpiar(d.getRazonSocial()));
        e.setContacto(limpiar(d.getContacto()));
        e.setTelefono(limpiar(d.getTelefono()));
        e.setCorreo(limpiar(d.getCorreo()));
        e.setDireccion(limpiar(d.getDireccion()));
        e.setCiudad(limpiar(d.getCiudad()));
        if (d.getEstado() != null) e.setEstado(d.getEstado());
    }

    private String limpiar(String valor) {
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
