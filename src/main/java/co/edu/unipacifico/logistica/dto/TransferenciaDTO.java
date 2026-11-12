package co.edu.unipacifico.logistica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class TransferenciaDTO {

    private Long transferenciaId;

    @NotBlank(message = "El número de la transferencia es obligatorio")
    @Size(max = 20, message = "Número: máximo 20 caracteres")
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "Seleccione la bodega de origen")
    private Long bodegaOrigenId;

    @NotNull(message = "Seleccione la bodega de destino")
    private Long bodegaDestinoId;

    @Size(max = 100, message = "Responsable: máximo 100 caracteres")
    private String responsable;

    @Size(max = 255, message = "Observación: máximo 255 caracteres")
    private String observacion;

    @Valid
    @NotEmpty(message = "Agregue al menos un producto")
    private List<DetalleCantidadDTO> detalles = new ArrayList<>();

    // Los completa el servidor para mostrar la transferencia.
    private String bodegaOrigenNombre;
    private String bodegaDestinoNombre;
    private String usuario;

    public Long getTransferenciaId() { return transferenciaId; }
    public void setTransferenciaId(Long transferenciaId) { this.transferenciaId = transferenciaId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getBodegaOrigenId() { return bodegaOrigenId; }
    public void setBodegaOrigenId(Long bodegaOrigenId) { this.bodegaOrigenId = bodegaOrigenId; }
    public Long getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Long bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public List<DetalleCantidadDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCantidadDTO> detalles) { this.detalles = detalles; }
    public String getBodegaOrigenNombre() { return bodegaOrigenNombre; }
    public void setBodegaOrigenNombre(String bodegaOrigenNombre) { this.bodegaOrigenNombre = bodegaOrigenNombre; }
    public String getBodegaDestinoNombre() { return bodegaDestinoNombre; }
    public void setBodegaDestinoNombre(String bodegaDestinoNombre) { this.bodegaDestinoNombre = bodegaDestinoNombre; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
