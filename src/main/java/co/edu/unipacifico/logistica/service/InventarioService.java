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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Único punto del sistema que cambia el stock. Las órdenes de compra y los despachos
 * lo llaman; así cada cambio de existencias queda registrado en el kardex.
 */
@Service
@Transactional
public class InventarioService {

    private static final int LIMITE_KARDEX = 300;

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

    /** Suma unidades a la bodega y registra la ENTRADA. Devuelve el nuevo saldo. */
    public int registrarEntrada(Producto producto, Bodega bodega, int cantidad, String referencia) {
        Existencia existencia = obtenerOCrear(producto, bodega);
        existencia.setCantidad(existencia.getCantidad() + cantidad);
        existenciaRepository.save(existencia);
        registrarMovimiento(MovimientoInventario.ENTRADA, producto, bodega, cantidad,
                existencia.getCantidad(), referencia);
        return existencia.getCantidad();
    }

    /** Resta unidades de la bodega y registra la SALIDA. Falla si no hay stock suficiente. */
    public int registrarSalida(Producto producto, Bodega bodega, int cantidad, String referencia) {
        Existencia existencia = obtenerOCrear(producto, bodega);
        if (existencia.getCantidad() < cantidad) {
            throw new ReglaNegocioException(String.format(
                    "Stock insuficiente de %s en %s: disponible %d, solicitado %d.",
                    producto.getNombre(), bodega.getNombre(), existencia.getCantidad(), cantidad));
        }
        existencia.setCantidad(existencia.getCantidad() - cantidad);
        existenciaRepository.save(existencia);
        registrarMovimiento(MovimientoInventario.SALIDA, producto, bodega, cantidad,
                existencia.getCantidad(), referencia);
        return existencia.getCantidad();
    }

    @Transactional(readOnly = true)
    public List<ExistenciaDTO> existencias(Long bodegaId) {
        return existenciaRepository.listarPorBodega(bodegaId).stream().map(e -> {
            ExistenciaDTO d = new ExistenciaDTO();
            d.setProductoId(e.getProducto().getProductoId());
            d.setProductoCodigo(e.getProducto().getCodigo());
            d.setProductoNombre(e.getProducto().getNombre());
            d.setUnidadMedida(e.getProducto().getUnidadMedida());
            d.setBodegaId(e.getBodega().getBodegaId());
            d.setBodegaNombre(e.getBodega().getNombre());
            d.setCantidad(e.getCantidad());
            d.setFechaActualizacion(e.getFechaActualizacion());
            return d;
        }).toList();
    }

    /** Stock total de cada producto activo frente a su mínimo y máximo. */
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

    /** Stock disponible por bodega y producto: {bodegaId: {productoId: cantidad}}. Lo usa el formulario de despacho. */
    @Transactional(readOnly = true)
    public Map<Long, Map<Long, Integer>> stockPorBodega() {
        Map<Long, Map<Long, Integer>> mapa = new HashMap<>();
        for (Existencia e : existenciaRepository.findAll()) {
            mapa.computeIfAbsent(e.getBodega().getBodegaId(), k -> new HashMap<>())
                    .put(e.getProducto().getProductoId(), e.getCantidad());
        }
        return mapa;
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> movimientos(Long productoId, Long bodegaId) {
        return movimientoRepository.buscar(productoId, bodegaId, PageRequest.of(0, LIMITE_KARDEX))
                .stream().map(m -> {
                    MovimientoDTO d = new MovimientoDTO();
                    d.setMovimientoId(m.getMovimientoId());
                    d.setFecha(m.getFecha());
                    d.setTipo(m.getTipo());
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

    private Existencia obtenerOCrear(Producto producto, Bodega bodega) {
        return existenciaRepository.bloquear(producto.getProductoId(), bodega.getBodegaId())
                .orElseGet(() -> existenciaRepository.saveAndFlush(new Existencia(producto, bodega)));
    }

    private void registrarMovimiento(String tipo, Producto producto, Bodega bodega, int cantidad,
                                     int saldo, String referencia) {
        movimientoRepository.save(new MovimientoInventario(tipo, producto, bodega, cantidad, saldo,
                referencia, usuarioActual()));
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
