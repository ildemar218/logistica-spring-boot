package co.edu.unipacifico.logistica.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Salida directa de mercancía de una bodega (venta de mostrador, merma, ajuste o consumo interno). */
@Entity
@Table(name = "salidas")
public class Salida {

    public static final String REGISTRADA = "REGISTRADA";
    public static final String ANULADA = "ANULADA";

    /** Tipos de salida; también son el concepto del movimiento en el kardex. */
    public static final List<String> TIPOS = List.of("VENTA", "MERMA", "AJUSTE", "CONSUMO_INTERNO");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salida_id")
    private Long salidaId;

    @Column(name = "numero", nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;

    /** Solo para ventas. */
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = REGISTRADA;

    @Column(name = "usuario", length = 50)
    private String usuario;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "salida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleSalida> detalles = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        fechaCreacion = LocalDateTime.now();
    }

    public void agregarDetalle(DetalleSalida detalle) {
        detalle.setSalida(this);
        detalles.add(detalle);
    }

    public Long getSalidaId() { return salidaId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Bodega getBodega() { return bodega; }
    public void setBodega(Bodega bodega) { this.bodega = bodega; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public List<DetalleSalida> getDetalles() { return detalles; }
}
