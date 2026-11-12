package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DespachoDTO;
import co.edu.unipacifico.logistica.dto.DetalleDespachoDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.DespachoMapper;
import co.edu.unipacifico.logistica.model.Bodega;
import co.edu.unipacifico.logistica.model.Cliente;
import co.edu.unipacifico.logistica.model.Despacho;
import co.edu.unipacifico.logistica.model.DetalleDespacho;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.repository.BodegaRepository;
import co.edu.unipacifico.logistica.repository.ClienteRepository;
import co.edu.unipacifico.logistica.repository.DespachoRepository;
import co.edu.unipacifico.logistica.repository.ProductoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DespachoServiceImpl implements DespachoService {

    private final DespachoRepository repository;
    private final DespachoMapper mapper;
    private final ClienteRepository clienteRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public DespachoServiceImpl(DespachoRepository repository, DespachoMapper mapper,
                               ClienteRepository clienteRepository,
                               BodegaRepository bodegaRepository,
                               ProductoRepository productoRepository,
                               InventarioService inventarioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.clienteRepository = clienteRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DespachoDTO> listar(String estado) {
        List<Despacho> despachos = estado == null || estado.isBlank()
                ? repository.findAllByOrderByDespachoIdDesc()
                : repository.findByEstadoOrderByDespachoIdDesc(estado);
        return despachos.stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DespachoDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String siguienteNumero() {
        return String.format("DSP-%05d", repository.count() + 1);
    }

    /**
     * Todo ocurre en una transacción: si un solo producto no tiene stock suficiente,
     * no se guarda el despacho ni se descuenta nada.
     */
    @Override
    public DespachoDTO guardar(DespachoDTO dto) {
        String numero = dto.getNumero().trim().toUpperCase();
        if (repository.existsByNumeroIgnoreCase(numero)) {
            throw new ReglaNegocioException("numero", "Ya existe un despacho con ese número");
        }
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .filter(Cliente::getEstado)
                .orElseThrow(() -> new ReglaNegocioException("clienteId", "El cliente no existe o está inactivo"));
        Bodega bodega = bodegaRepository.findById(dto.getBodegaId())
                .filter(Bodega::getEstado)
                .orElseThrow(() -> new ReglaNegocioException("bodegaId", "La bodega no existe o está inactiva"));

        Despacho despacho = new Despacho();
        despacho.setNumero(numero);
        despacho.setFecha(dto.getFecha());
        despacho.setCliente(cliente);
        despacho.setBodega(bodega);
        despacho.setDireccionEntrega(dto.getDireccionEntrega().trim());
        despacho.setTransportista(dto.getTransportista());
        despacho.setNumeroGuia(dto.getNumeroGuia());
        despacho.setObservacion(dto.getObservacion());
        despacho.setEstado(Despacho.DESPACHADO);

        String referencia = "Despacho " + numero;
        BigDecimal subtotal = BigDecimal.ZERO;
        int fila = 0;
        for (DetalleDespachoDTO d : dto.getDetalles()) {
            fila++;
            final int numFila = fila;
            Producto producto = productoRepository.findById(d.getProductoId())
                    .filter(Producto::getEstado)
                    .orElseThrow(() -> new ReglaNegocioException(
                            "El producto de la fila " + numFila + " no existe o está inactivo"));
            // Descuenta el stock; lanza ReglaNegocioException si no alcanza.
            inventarioService.registrarSalida(producto, bodega, d.getCantidad(), referencia);

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            despacho.agregarDetalle(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        BigDecimal iva = subtotal.multiply(OrdenCompraServiceImpl.TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        despacho.setSubtotal(subtotal);
        despacho.setIva(iva);
        despacho.setTotal(subtotal.add(iva));
        return mapper.toDTO(repository.save(despacho));
    }

    @Override
    public DespachoDTO marcarEntregado(Long id) {
        Despacho despacho = obtener(id);
        if (!Despacho.DESPACHADO.equals(despacho.getEstado())) {
            throw new ReglaNegocioException("Solo se puede entregar un despacho en tránsito. Estado actual: " + despacho.getEstado());
        }
        despacho.setEstado(Despacho.ENTREGADO);
        despacho.setFechaEntrega(LocalDateTime.now());
        return mapper.toDTO(despacho);
    }

    @Override
    public DespachoDTO anular(Long id) {
        Despacho despacho = obtener(id);
        if (!Despacho.DESPACHADO.equals(despacho.getEstado())) {
            throw new ReglaNegocioException("Solo se puede anular un despacho en tránsito. Estado actual: " + despacho.getEstado());
        }
        String referencia = "Anulación despacho " + despacho.getNumero();
        for (DetalleDespacho d : despacho.getDetalles()) {
            inventarioService.registrarEntrada(d.getProducto(), despacho.getBodega(), d.getCantidad(), referencia);
        }
        despacho.setEstado(Despacho.ANULADO);
        return mapper.toDTO(despacho);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEnTransito() {
        return repository.countByEstado(Despacho.DESPACHADO);
    }

    private Despacho obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("El despacho solicitado no existe"));
    }
}
