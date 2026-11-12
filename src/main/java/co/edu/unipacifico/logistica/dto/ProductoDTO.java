package co.edu.unipacifico.logistica.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoDTO {

    private Long productoId;

    @NotBlank(message = "Código (SKU) es obligatorio")
    @Size(max = 30, message = "Código (SKU): máximo 30 caracteres")
    private String codigo;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 120, message = "Nombre: máximo 120 caracteres")
    private String nombre;

    @Size(max = 255, message = "Descripción: máximo 255 caracteres")
    private String descripcion;

    @NotNull(message = "Seleccione la categoría")
    private Long categoriaId;

    // Solo para mostrar en el listado.
    private String categoriaNombre;

    @NotBlank(message = "Unidad de medida es obligatorio")
    @Size(max = 20, message = "Unidad de medida: máximo 20 caracteres")
    private String unidadMedida;

    @NotNull(message = "Precio de compra es obligatorio")
    @DecimalMin(value = "0.00", message = "Precio de compra no puede ser negativo")
    @Digits(integer = 12, fraction = 2, message = "Precio de compra: máximo 12 enteros y 2 decimales")
    private BigDecimal precioCompra;

    @NotNull(message = "Precio de venta es obligatorio")
    @DecimalMin(value = "0.00", message = "Precio de venta no puede ser negativo")
    @Digits(integer = 12, fraction = 2, message = "Precio de venta: máximo 12 enteros y 2 decimales")
    private BigDecimal precioVenta;

    @NotNull(message = "Stock mínimo es obligatorio")
    @Min(value = 0, message = "Stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "Stock máximo es obligatorio")
    @Min(value = 0, message = "Stock máximo no puede ser negativo")
    private Integer stockMaximo;

    private Boolean estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
