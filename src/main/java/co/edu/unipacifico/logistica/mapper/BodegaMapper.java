package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.BodegaDTO;
import co.edu.unipacifico.logistica.model.Bodega;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public BodegaDTO toDTO(Bodega e) {
        if (e == null) return null;
        BodegaDTO d = new BodegaDTO();
        d.setBodegaId(e.getBodegaId());
        d.setCodigo(e.getCodigo());
        d.setNombre(e.getNombre());
        d.setCiudad(e.getCiudad());
        d.setDireccion(e.getDireccion());
        d.setResponsable(e.getResponsable());
        d.setTelefono(e.getTelefono());
        d.setEstado(e.getEstado());
        return d;
    }

    public Bodega toEntity(BodegaDTO d) {
        if (d == null) return null;
        Bodega e = new Bodega();
        actualizarEntity(e, d);
        return e;
    }

    /** Copia solo los datos editables; conserva el id y las fechas. */
    public void actualizarEntity(Bodega e, BodegaDTO d) {
        e.setCodigo(limpiar(d.getCodigo()));
        e.setNombre(limpiar(d.getNombre()));
        e.setCiudad(limpiar(d.getCiudad()));
        e.setDireccion(limpiar(d.getDireccion()));
        e.setResponsable(limpiar(d.getResponsable()));
        e.setTelefono(limpiar(d.getTelefono()));
        if (d.getEstado() != null) e.setEstado(d.getEstado());
    }

    private String limpiar(String valor) {
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
