package co.edu.unipacifico.logistica.mapper;

import co.edu.unipacifico.logistica.dto.ProductoDTO;
import co.edu.unipacifico.logistica.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoDTO toDTO(Producto e) {
        if (e == null) return null;
        ProductoDTO d = new ProductoDTO();
        d.setProductoId(e.getProductoId());
        d.setCodigo(e.getCodigo());
        d.setNombre(e.getNombre());
        d.setDescripcion(e.getDescripcion());
        if (e.getCategoria() != null) {
            d.setCategoriaId(e.getCategoria().getCategoriaId());
            d.setCategoriaNombre(e.getCategoria().getNombre());
        }
        d.setUnidadMedida(e.getUnidadMedida());
        d.setPrecioCompra(e.getPrecioCompra());
        d.setPrecioVenta(e.getPrecioVenta());
        d.setStockMinimo(e.getStockMinimo());
        d.setStockMaximo(e.getStockMaximo());
        d.setEstado(e.getEstado());
        d.setFechaCreacion(e.getFechaCreacion());
        d.setFechaActualizacion(e.getFechaActualizacion());
        return d;
    }

    public Producto toEntity(ProductoDTO d) {
        if (d == null) return null;
        Producto e = new Producto();
        actualizarEntity(e, d);
        return e;
    }

    /** Copia solo los datos editables; conserva el id y las fechas. */
    // La relación con otras tablas la asigna el servicio, que comprueba que exista.
    public void actualizarEntity(Producto e, ProductoDTO d) {
        e.setCodigo(limpiar(d.getCodigo()));
        e.setNombre(limpiar(d.getNombre()));
        e.setDescripcion(limpiar(d.getDescripcion()));
        e.setUnidadMedida(limpiar(d.getUnidadMedida()));
        e.setPrecioCompra(d.getPrecioCompra());
        e.setPrecioVenta(d.getPrecioVenta());
        e.setStockMinimo(d.getStockMinimo());
        e.setStockMaximo(d.getStockMaximo());
        if (d.getEstado() != null) e.setEstado(d.getEstado());
    }

    private String limpiar(String valor) {
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
