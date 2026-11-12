package co.edu.unipacifico.logistica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_salida")
public class DetalleSalida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id")
    private Long detalleId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "salida_id")
    private Salida salida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public Long getDetalleId() { return detalleId; }
    public Salida getSalida() { return salida; }
    public void setSalida(Salida salida) { this.salida = salida; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
