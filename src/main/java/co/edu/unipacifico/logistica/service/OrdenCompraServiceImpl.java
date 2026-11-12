package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DetalleOrdenCompraDTO;
import co.edu.unipacifico.logistica.dto.OrdenCompraDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.OrdenCompraMapper;
import co.edu.unipacifico.logistica.model.Bodega;
import co.edu.unipacifico.logistica.model.DetalleOrdenCompra;
import co.edu.unipacifico.logistica.model.OrdenCompra;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.model.Proveedor;
import co.edu.unipacifico.logistica.repository.BodegaRepository;
import co.edu.unipacifico.logistica.repository.OrdenCompraRepository;
import co.edu.unipacifico.logistica.repository.ProductoRepository;
import co.edu.unipacifico.logistica.repository.ProveedorRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdenCompraServiceImpl implements OrdenCompraService {

    /** Regla académica: IVA del 19 %. Para cambiarlo, modificar solo esta constante. */
    static final BigDecimal TASA_IVA = new BigDecimal("0.19");

    private final OrdenCompraRepository repository;
    private final OrdenCompraMapper mapper;
    private final ProveedorRepository proveedorRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public OrdenCompraServiceImpl(OrdenCompraRepository repository, OrdenCompraMapper mapper,
                                  ProveedorRepository proveedorRepository,
                                  BodegaRepository bodegaRepository,
                                  ProductoRepository productoRepository,
                                  InventarioService inventarioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.proveedorRepository = proveedorRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> listar(String estado) {
        List<OrdenCompra> ordenes = estado == null || estado.isBlank()
                ? repository.findAllByOrderByOrdenIdDesc()
                : repository.findByEstadoOrderByOrdenIdDesc(estado);
        return ordenes.stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String siguienteNumero() {
        return String.format("OC-%05d", repository.count() + 1);
    }

    @Override
    public OrdenCompraDTO guardar(OrdenCompraDTO dto) {
        String numero = dto.getNumero().trim().toUpperCase();
        if (repository.existsByNumeroIgnoreCase(numero)) {
            throw new ReglaNegocioException("numero", "Ya existe una orden con ese número");
        }
        if (dto.getFechaEntregaEsperada() != null && dto.getFechaEntregaEsperada().isBefore(dto.getFecha())) {
            throw new ReglaNegocioException("fechaEntregaEsperada",
                    "La entrega esperada no puede ser anterior a la fecha de la orden");
        }
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .filter(Proveedor::getEstado)
                .orElseThrow(() -> new ReglaNegocioException("proveedorId", "El proveedor no existe o está inactivo"));
        Bodega bodega = bodegaRepository.findById(dto.getBodegaId())
                .filter(Bodega::getEstado)
                .orElseThrow(() -> new ReglaNegocioException("bodegaId", "La bodega no existe o está inactiva"));

        OrdenCompra orden = new OrdenCompra();
        orden.setNumero(numero);
        orden.setFecha(dto.getFecha());
        orden.setFechaEntregaEsperada(dto.getFechaEntregaEsperada());
        orden.setProveedor(proveedor);
        orden.setBodega(bodega);
        orden.setObservacion(dto.getObservacion());
        orden.setEstado(OrdenCompra.PENDIENTE);

        // Los importes se recalculan siempre en el servidor con BigDecimal.
        BigDecimal subtotal = BigDecimal.ZERO;
        int fila = 0;
        for (DetalleOrdenCompraDTO d : dto.getDetalles()) {
            fila++;
            final int numFila = fila;
            Producto producto = productoRepository.findById(d.getProductoId())
                    .filter(Producto::getEstado)
                    .orElseThrow(() -> new ReglaNegocioException(
                            "El producto de la fila " + numFila + " no existe o está inactivo"));
            DetalleOrdenCompra detalle = new DetalleOrdenCompra();
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setCostoUnitario(d.getCostoUnitario().setScale(2, RoundingMode.HALF_UP));
            detalle.setSubtotal(detalle.getCostoUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            orden.agregarDetalle(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        orden.setSubtotal(subtotal);
        orden.setIva(iva);
        orden.setTotal(subtotal.add(iva));
        return mapper.toDTO(repository.save(orden));
    }

    @Override
    public OrdenCompraDTO recibir(Long id) {
        OrdenCompra orden = obtener(id);
        if (!OrdenCompra.PENDIENTE.equals(orden.getEstado())) {
            throw new ReglaNegocioException("Solo se puede recibir una orden PENDIENTE. Estado actual: " + orden.getEstado());
        }
        String referencia = "Orden de compra " + orden.getNumero();
        for (DetalleOrdenCompra d : orden.getDetalles()) {
            inventarioService.registrarEntrada(d.getProducto(), orden.getBodega(), d.getCantidad(), referencia);
        }
        orden.setEstado(OrdenCompra.RECIBIDA);
        orden.setFechaRecepcion(LocalDateTime.now());
        return mapper.toDTO(orden);
    }

    @Override
    public OrdenCompraDTO anular(Long id) {
        OrdenCompra orden = obtener(id);
        if (!OrdenCompra.PENDIENTE.equals(orden.getEstado())) {
            throw new ReglaNegocioException("Solo se puede anular una orden PENDIENTE. Estado actual: " + orden.getEstado());
        }
        orden.setEstado(OrdenCompra.ANULADA);
        return mapper.toDTO(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPendientes() {
        return repository.countByEstado(OrdenCompra.PENDIENTE);
    }

    private OrdenCompra obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("La orden de compra solicitada no existe"));
    }
}
