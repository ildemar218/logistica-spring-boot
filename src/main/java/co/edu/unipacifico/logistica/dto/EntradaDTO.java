package co.edu.unipacifico.logistica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class EntradaDTO {

    private Long entradaId;

    @NotBlank(message = "El número de la entrada es obligatorio")
    @Size(max = 20, message = "Número: máximo 20 caracteres")
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "Seleccione el proveedor")
    private Long proveedorId;

    @NotNull(message = "Seleccione la bodega que recibe")
    private Long bodegaId;

    @Size(max = 40, message = "Documento del proveedor: máximo 40 caracteres")
    private String documentoProveedor;

    @Size(max = 255, message = "Observación: máximo 255 caracteres")
    private String observacion;

    @Valid
    @NotEmpty(message = "Agregue al menos un producto")
    private List<DetalleEntradaDTO> detalles = new ArrayList<>();

    // Los completa el servidor para mostrar la entrada.
    private String proveedorNombre;
    private String bodegaNombre;
    private String estado;
    private String usuario;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public Long getEntradaId() { return entradaId; }
    public void setEntradaId(Long entradaId) { this.entradaId = entradaId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    public Long getBodegaId() { return bodegaId; }
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }
    public String getDocumentoProveedor() { return documentoProveedor; }
    public void setDocumentoProveedor(String documentoProveedor) { this.documentoProveedor = documentoProveedor; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public List<DetalleEntradaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleEntradaDTO> detalles) { this.detalles = detalles; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
