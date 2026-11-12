package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.ClienteDTO;
import co.edu.unipacifico.logistica.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteDTO toDTO(Cliente e) {
        if (e == null) return null;
        ClienteDTO d = new ClienteDTO();
        d.setClienteId(e.getClienteId());
        d.setTipoDocumento(e.getTipoDocumento());
        d.setNumeroDocumento(e.getNumeroDocumento());
        d.setNombres(e.getNombres());
        d.setTelefono(e.getTelefono());
        d.setCorreo(e.getCorreo());
        d.setDireccion(e.getDireccion());
        d.setCiudad(e.getCiudad());
        d.setEstado(e.getEstado());
        d.setFechaCreacion(e.getFechaCreacion());
        d.setFechaActualizacion(e.getFechaActualizacion());
        return d;
    }

    public Cliente toEntity(ClienteDTO d) {
        if (d == null) return null;
        Cliente e = new Cliente();
        actualizarEntity(e, d);
        return e;
    }

    /** Copia solo los datos editables; conserva el id y las fechas. */
    public void actualizarEntity(Cliente e, ClienteDTO d) {
        e.setTipoDocumento(limpiar(d.getTipoDocumento()));
        e.setNumeroDocumento(limpiar(d.getNumeroDocumento()));
        e.setNombres(limpiar(d.getNombres()));
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
