package co.edu.unipacifico.logistica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Kardex: cada entrada o salida de mercancía queda registrada con el saldo resultante. */
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    public static final String ENTRADA = "ENTRADA";
    public static final String SALIDA = "SALIDA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private Long movimientoId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /** Existencia de ese producto en esa bodega después del movimiento. */
    @Column(name = "saldo", nullable = false)
    private Integer saldo;

    /** Documento que originó el movimiento, por ejemplo "Orden de compra OC-00001". */
    @Column(name = "referencia", nullable = false, length = 60)
    private String referencia;

    @Column(name = "usuario", length = 50)
    private String usuario;

    public MovimientoInventario() {
    }

    public MovimientoInventario(String tipo, Producto producto, Bodega bodega, Integer cantidad,
                                Integer saldo, String referencia, String usuario) {
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.producto = producto;
        this.bodega = bodega;
        this.cantidad = cantidad;
        this.saldo = saldo;
        this.referencia = referencia;
        this.usuario = usuario;
    }

    public Long getMovimientoId() { return movimientoId; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public Producto getProducto() { return producto; }
    public Bodega getBodega() { return bodega; }
    public Integer getCantidad() { return cantidad; }
    public Integer getSaldo() { return saldo; }
    public String getReferencia() { return referencia; }
    public String getUsuario() { return usuario; }
}
