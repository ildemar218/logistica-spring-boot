package co.edu.unipacifico.logistica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class SalidaDTO {

    private Long salidaId;

    @NotBlank(message = "El número de la salida es obligatorio")
    @Size(max = 20, message = "Número: máximo 20 caracteres")
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotBlank(message = "Seleccione el tipo de salida")
    @Pattern(regexp = "VENTA|MERMA|AJUSTE|CONSUMO_INTERNO", message = "El tipo de salida no es válido")
    private String tipo;

    @NotNull(message = "Seleccione la bodega")
    private Long bodegaId;

    /** Obligatorio solo cuando el tipo es VENTA (lo valida el servicio). */
    private Long clienteId;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 255, message = "Motivo: máximo 255 caracteres")
    private String motivo;

    @Valid
    @NotEmpty(message = "Agregue al menos un producto")
    private List<DetalleCantidadDTO> detalles = new ArrayList<>();

    // Los completa el servidor para mostrar la salida.
    private String bodegaNombre;
    private String clienteNombre;
    private String estado;
    private String usuario;

    public Long getSalidaId() { return salidaId; }
    public void setSalidaId(Long salidaId) { this.salidaId = salidaId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getBodegaId() { return bodegaId; }
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public List<DetalleCantidadDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCantidadDTO> detalles) { this.detalles = detalles; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
