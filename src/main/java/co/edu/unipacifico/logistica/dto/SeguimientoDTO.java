package co.edu.unipacifico.logistica.dto;

import java.time.LocalDateTime;

/** Un cambio de estado en el historial de un despacho. */
public class SeguimientoDTO {

    private final LocalDateTime fecha;
    private final String estado;
    private final String usuario;
    private final String observacion;

    public SeguimientoDTO(LocalDateTime fecha, String estado, String usuario, String observacion) {
        this.fecha = fecha;
        this.estado = estado;
        this.usuario = usuario;
        this.observacion = observacion;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public String getUsuario() { return usuario; }
    public String getObservacion() { return observacion; }
}
