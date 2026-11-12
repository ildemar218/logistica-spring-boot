package co.edu.unipacifico.logistica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Cantidad disponible de un producto en una bodega. Hay una fila por cada par producto-bodega. */
@Entity
@Table(name = "existencias",
       uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "bodega_id"}))
public class Existencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "existencia_id")
    private Long existenciaId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    protected void alGuardar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Existencia() {
    }

    public Existencia(Producto producto, Bodega bodega) {
        this.producto = producto;
        this.bodega = bodega;
        this.cantidad = 0;
    }

    public Long getExistenciaId() { return existenciaId; }
    public Producto getProducto() { return producto; }
    public Bodega getBodega() { return bodega; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
