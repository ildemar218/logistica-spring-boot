package co.edu.unipacifico.logistica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Historial de un despacho: cada cambio de estado con su fecha, usuario y observación. */
@Entity
@Table(name = "seguimiento_despacho")
public class SeguimientoDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seguimiento_id")
    private Long seguimientoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "despacho_id")
    private Despacho despacho;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Column(name = "usuario", length = 50)
    private String usuario;

    @Column(name = "observacion")
    private String observacion;

    public SeguimientoDespacho() {
    }

    public SeguimientoDespacho(Despacho despacho, String estado, String usuario, String observacion) {
        this.despacho = despacho;
        this.fecha = LocalDateTime.now();
        this.estado = estado;
        this.usuario = usuario;
        this.observacion = observacion;
    }

    public Long getSeguimientoId() { return seguimientoId; }
    public Despacho getDespacho() { return despacho; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public String getUsuario() { return usuario; }
    public String getObservacion() { return observacion; }
}
