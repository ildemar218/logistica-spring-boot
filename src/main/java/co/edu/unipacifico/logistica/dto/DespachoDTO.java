package co.edu.unipacifico.logistica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class DespachoDTO {

    private Long despachoId;

    @NotBlank(message = "El número del despacho es obligatorio")
    @Size(max = 20, message = "Número: máximo 20 caracteres")
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "Seleccione el cliente")
    private Long clienteId;

    @NotNull(message = "Seleccione la bodega de origen")
    private Long bodegaId;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 200, message = "Dirección: máximo 200 caracteres")
    private String direccionEntrega;

    @Size(max = 100, message = "Transportista: máximo 100 caracteres")
    private String transportista;

    @Size(max = 40, message = "Guía: máximo 40 caracteres")
    private String numeroGuia;

    @Size(max = 255, message = "Observación: máximo 255 caracteres")
    private String observacion;

    @Valid
    @NotEmpty(message = "Agregue al menos un producto al despacho")
    private List<DetalleDespachoDTO> detalles = new ArrayList<>();

    // Los completa el servidor para mostrar el despacho.
    private String clienteNombre;
    private String bodegaNombre;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private LocalDateTime fechaEntrega;

    public Long getDespachoId() { return despachoId; }
    public void setDespachoId(Long despachoId) { this.despachoId = despachoId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getBodegaId() { return bodegaId; }
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public List<DetalleDespachoDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleDespachoDTO> detalles) { this.detalles = detalles; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
}
