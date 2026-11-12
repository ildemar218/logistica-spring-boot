package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.ExistenciaDTO;
import co.edu.unipacifico.logistica.dto.MovimientoDTO;
import co.edu.unipacifico.logistica.dto.ResumenStockDTO;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.model.Bodega;
import co.edu.unipacifico.logistica.model.Existencia;
import co.edu.unipacifico.logistica.model.MovimientoInventario;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.repository.ExistenciaRepository;
import co.edu.unipacifico.logistica.repository.MovimientoInventarioRepository;
import co.edu.unipacifico.logistica.repository.ProductoRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Único punto del sistema que cambia el stock. Entradas, salidas, transferencias y despachos
 * lo llaman; así cada cambio de existencias queda registrado en el kardex.
 */
@Service
@Transactional
public class InventarioService {

    private static final int LIMITE_PANTALLA = 300;
    private static final int LIMITE_REPORTE = 10000;

    private final ExistenciaRepository existenciaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public InventarioService(ExistenciaRepository existenciaRepository,
                             MovimientoInventarioRepository movimientoRepository,
                             ProductoRepository productoRepository) {
        this.existenciaRepository = existenciaRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    /** Suma unidades a la bodega y registra la ENTRADA en el kardex. Devuelve el nuevo saldo. */
    public int registrarEntrada(Producto producto, Bodega bodega, int cantidad, String concepto, String referencia) {
        Existencia existencia = obtenerOCrear(producto, bodega);
        existencia.setCantidad(existencia.getCantidad() + cantidad);
        existenciaRepository.save(existencia);
        registrarMovimiento(MovimientoInventario.ENTRADA, concepto, producto, bodega, cantidad,
                existencia.getCantidad(), referencia);
        return existencia.getCantidad();
    }

    /** Resta unidades de la bodega y registra la SALIDA. Rechaza la operación si no hay existencias suficientes. */
    public int registrarSalida(Producto producto, Bodega bodega, int cantidad, String concepto, String referencia) {
        Existencia existencia = obtenerOCrear(producto, bodega);
        if (existencia.getCantidad() < cantidad) {
            throw new ReglaNegocioException(String.format(
                    "Existencias insuficientes de %s en %s: disponible %d, solicitado %d.",
                    producto.getNombre(), bodega.getNombre(), existencia.getCantidad(), cantidad));
        }
        existencia.setCantidad(existencia.getCantidad() - cantidad);
        existenciaRepository.save(existencia);
        registrarMovimiento(MovimientoInventario.SALIDA, concepto, producto, bodega, cantidad,
                existencia.getCantidad(), referencia);
        return existencia.getCantidad();
    }

    /** Stock actual de un producto en una bodega, sin bloquear. */
    @Transactional(readOnly = true)
    public int disponible(Long productoId, Long bodegaId) {
        return existenciaRepository.consultar(productoId, bodegaId).map(Existencia::getCantidad).orElse(0);
    }

    @Transactional(readOnly = true)
    public List<ExistenciaDTO> existencias(Long bodegaId, Long productoId) {
        return existenciaRepository.buscar(bodegaId, productoId).stream().map(e -> {
            ExistenciaDTO d = new ExistenciaDTO();
            d.setProductoId(e.getProducto().getProductoId());
            d.setProductoCodigo(e.getProducto().getCodigo());
            d.setProductoNombre(e.getProducto().getNombre());
            d.setUnidadMedida(e.getProducto().getUnidadMedida());
            d.setStockMinimo(e.getProducto().getStockMinimo());
            d.setCostoUnitario(e.getProducto().getPrecioCompra());
            d.setBodegaId(e.getBodega().getBodegaId());
            d.setBodegaNombre(e.getBodega().getNombre());
            d.setCantidad(e.getCantidad());
            d.setFechaActualizacion(e.getFechaActualizacion());
            return d;
        }).toList();
    }

    /** Stock total de cada producto activo frente a su mínimo y máximo: base de las alertas. */
    @Transactional(readOnly = true)
    public List<ResumenStockDTO> resumen() {
        Map<Long, Integer> totales = new HashMap<>();
        for (Object[] fila : existenciaRepository.totalesPorProducto()) {
            totales.put((Long) fila[0], ((Number) fila[1]).intValue());
        }
        return productoRepository.findByEstadoTrueOrderByNombre().stream()
                .map(p -> new ResumenStockDTO(p.getProductoId(), p.getCodigo(), p.getNombre(),
                        p.getUnidadMedida(), totales.getOrDefault(p.getProductoId(), 0),
                        p.getStockMinimo(), p.getStockMaximo()))
                .toList();
    }

    /** Productos cuyo stock total está por debajo del mínimo. */
    @Transactional(readOnly = true)
    public List<ResumenStockDTO> alertasStockMinimo() {
        return resumen().stream().filter(r -> ResumenStockDTO.BAJO.equals(r.getNivel())).toList();
    }

    /** Stock disponible por bodega y producto: {bodegaId: {productoId: cantidad}}. Lo usan los formularios. */
    @Transactional(readOnly = true)
    public Map<Long, Map<Long, Integer>> stockPorBodega() {
        Map<Long, Map<Long, Integer>> mapa = new HashMap<>();
        for (Existencia e : existenciaRepository.findAll()) {
            mapa.computeIfAbsent(e.getBodega().getBodegaId(), k -> new HashMap<>())
                    .put(e.getProducto().getProductoId(), e.getCantidad());
        }
        return mapa;
    }

    /** Kardex en pantalla (últimos movimientos). */
    @Transactional(readOnly = true)
    public List<MovimientoDTO> movimientos(Long productoId, Long bodegaId) {
        return movimientos(productoId, bodegaId, null, null, null, null, LIMITE_PANTALLA);
    }

    /** Reporte de movimientos con todos los filtros. Las fechas son inclusivas. */
    @Transactional(readOnly = true)
    public List<MovimientoDTO> reporteMovimientos(Long productoId, Long bodegaId, String tipo, String concepto,
                                                  LocalDate desde, LocalDate hasta) {
        return movimientos(productoId, bodegaId, tipo, concepto, desde, hasta, LIMITE_REPORTE);
    }

    private List<MovimientoDTO> movimientos(Long productoId, Long bodegaId, String tipo, String concepto,
                                            LocalDate desde, LocalDate hasta, int limite) {
        return movimientoRepository.buscar(productoId, bodegaId, vacioANull(tipo), vacioANull(concepto),
                        desde == null ? null : desde.atStartOfDay(),
                        hasta == null ? null : hasta.plusDays(1).atStartOfDay(),
                        PageRequest.of(0, limite))
                .stream().map(m -> {
                    MovimientoDTO d = new MovimientoDTO();
                    d.setMovimientoId(m.getMovimientoId());
                    d.setFecha(m.getFecha());
                    d.setTipo(m.getTipo());
                    d.setConcepto(m.getConcepto());
                    d.setProductoCodigo(m.getProducto().getCodigo());
                    d.setProductoNombre(m.getProducto().getNombre());
                    d.setBodegaNombre(m.getBodega().getNombre());
                    d.setCantidad(m.getCantidad());
                    d.setSaldo(m.getSaldo());
                    d.setReferencia(m.getReferencia());
                    d.setUsuario(m.getUsuario());
                    return d;
                }).toList();
    }

    /** Login del usuario con sesión; se guarda en cada documento y movimiento. */
    public static String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private static String vacioANull(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }

    private Existencia obtenerOCrear(Producto producto, Bodega bodega) {
        return existenciaRepository.bloquear(producto.getProductoId(), bodega.getBodegaId())
                .orElseGet(() -> existenciaRepository.saveAndFlush(new Existencia(producto, bodega)));
    }

    private void registrarMovimiento(String tipo, String concepto, Producto producto, Bodega bodega,
                                     int cantidad, int saldo, String referencia) {
        movimientoRepository.save(new MovimientoInventario(tipo, concepto, producto, bodega, cantidad, saldo,
                referencia, usuarioActual()));
    }
}
