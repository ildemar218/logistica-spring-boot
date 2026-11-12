package co.edu.unipacifico.logistica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orden de despacho hacia un cliente.
 *
 * PENDIENTE  → EN_RUTA (sale de la bodega: se descuenta el stock)
 *            → CANCELADO (no se movió mercancía)
 * EN_RUTA    → ENTREGADO
 *            → DEVUELTO (la mercancía vuelve a la bodega de origen)
 */
@Entity
@Table(name = "despachos")
public class Despacho {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String EN_RUTA = "EN_RUTA";
    public static final String ENTREGADO = "ENTREGADO";
    public static final String DEVUELTO = "DEVUELTO";
    public static final String CANCELADO = "CANCELADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "despacho_id")
    private Long despachoId;

    @Column(name = "numero", nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;

    @Column(name = "direccion_entrega", nullable = false, length = 200)
    private String direccionEntrega;

    /** Usuario con rol Transportador responsable de la entrega. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "transportador_id")
    private Usuarios transportador;

    /** Placa del vehículo. */
    @Column(name = "vehiculo", length = 20)
    private String vehiculo;

    @Column(name = "numero_guia", length = 40)
    private String numeroGuia;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = PENDIENTE;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "motivo_devolucion")
    private String motivoDevolucion;

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "iva", nullable = false, precision = 14, scale = 2)
    private BigDecimal iva;

    @Column(name = "total", nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDespacho> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seguimientoId asc")
    private List<SeguimientoDespacho> seguimiento = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        fechaCreacion = LocalDateTime.now();
    }

    public void agregarDetalle(DetalleDespacho detalle) {
        detalle.setDespacho(this);
        detalles.add(detalle);
    }

    /** Cambia el estado y deja constancia en el historial de seguimiento. */
    public void cambiarEstado(String nuevoEstado, String usuario, String observacion) {
        this.estado = nuevoEstado;
        seguimiento.add(new SeguimientoDespacho(this, nuevoEstado, usuario, observacion));
    }

    public Long getDespachoId() { return despachoId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Bodega getBodega() { return bodega; }
    public void setBodega(Bodega bodega) { this.bodega = bodega; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public Usuarios getTransportador() { return transportador; }
    public void setTransportador(Usuarios transportador) { this.transportador = transportador; }
    public String getVehiculo() { return vehiculo; }
    public void setVehiculo(String vehiculo) { this.vehiculo = vehiculo; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getEstado() { return estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getMotivoDevolucion() { return motivoDevolucion; }
    public void setMotivoDevolucion(String motivoDevolucion) { this.motivoDevolucion = motivoDevolucion; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public List<DetalleDespacho> getDetalles() { return detalles; }
    public List<SeguimientoDespacho> getSeguimiento() { return seguimiento; }
}
