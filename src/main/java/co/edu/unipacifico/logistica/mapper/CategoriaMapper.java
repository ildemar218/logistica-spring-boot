package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.CategoriaDTO;
import co.edu.unipacifico.logistica.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaDTO toDTO(Categoria e) {
        if (e == null) return null;
        CategoriaDTO d = new CategoriaDTO();
        d.setCategoriaId(e.getCategoriaId());
        d.setNombre(e.getNombre());
        d.setDescripcion(e.getDescripcion());
        d.setEstado(e.getEstado());
        return d;
    }

    public Categoria toEntity(CategoriaDTO d) {
        if (d == null) return null;
        Categoria e = new Categoria();
        actualizarEntity(e, d);
        return e;
    }

    /** Copia solo los datos editables; conserva el id y las fechas. */
    public void actualizarEntity(Categoria e, CategoriaDTO d) {
        e.setNombre(limpiar(d.getNombre()));
        e.setDescripcion(limpiar(d.getDescripcion()));
        if (d.getEstado() != null) e.setEstado(d.getEstado());
    }

    private String limpiar(String valor) {
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
