package co.edu.unipacifico.logistica.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Fila de detalle con producto y cantidad. La usan salidas y transferencias. */
public class DetalleCantidadDTO {

    @NotNull(message = "Seleccione el producto en cada fila")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;

    // Los completa el servidor para mostrar el documento.
    private String productoCodigo;
    private String productoNombre;
    private String unidadMedida;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
}
