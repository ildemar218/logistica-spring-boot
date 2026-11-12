package co.edu.unipacifico.logistica.dto;

import java.time.LocalDateTime;

/** Fila del kardex. */
public class MovimientoDTO {

    private Long movimientoId;
    private LocalDateTime fecha;
    private String tipo;
    private String productoCodigo;
    private String productoNombre;
    private String bodegaNombre;
    private Integer cantidad;
    private Integer saldo;
    private String referencia;
    private String usuario;

    public Long getMovimientoId() { return movimientoId; }
    public void setMovimientoId(Long movimientoId) { this.movimientoId = movimientoId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getSaldo() { return saldo; }
    public void setSaldo(Integer saldo) { this.saldo = saldo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
