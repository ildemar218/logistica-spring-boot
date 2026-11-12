package co.edu.unipacifico.logistica.dto;

import java.time.LocalDateTime;

/** Fila de la consulta de existencias: un producto en una bodega. */
public class ExistenciaDTO {

    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private String unidadMedida;
    private Long bodegaId;
    private String bodegaNombre;
    private Integer cantidad;
    private LocalDateTime fechaActualizacion;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public Long getBodegaId() { return bodegaId; }
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
