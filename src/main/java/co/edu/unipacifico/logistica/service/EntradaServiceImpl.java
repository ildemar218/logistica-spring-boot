package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DetalleEntradaDTO;
import co.edu.unipacifico.logistica.dto.EntradaDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.EntradaMapper;
import co.edu.unipacifico.logistica.model.DetalleEntrada;
import co.edu.unipacifico.logistica.model.Entrada;
import co.edu.unipacifico.logistica.model.MovimientoInventario;
import co.edu.unipacifico.logistica.repository.EntradaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntradaServiceImpl implements EntradaService {

    /** Regla académica: IVA del 19 %. Para cambiarlo, modificar solo esta constante. */
    public static final BigDecimal TASA_IVA = new BigDecimal("0.19");

    private final EntradaRepository repository;
    private final EntradaMapper mapper;
    private final Referencias referencias;
    private final InventarioService inventarioService;

    public EntradaServiceImpl(EntradaRepository repository, EntradaMapper mapper,
                              Referencias referencias, InventarioService inventarioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.referencias = referencias;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntradaDTO> listar() {
        return repository.findAllByOrderByEntradaIdDesc().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EntradaDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String siguienteNumero() {
        return Referencias.consecutivo("ENT", repository.count());
    }

    @Override
    public EntradaDTO guardar(EntradaDTO dto) {
        String numero = dto.getNumero().trim().toUpperCase();
        if (repository.existsByNumeroIgnoreCase(numero)) {
            throw new ReglaNegocioException("numero", "Ya existe una entrada con ese número");
        }
        Entrada entrada = new Entrada();
        entrada.setNumero(numero);
        entrada.setFecha(dto.getFecha());
        entrada.setProveedor(referencias.proveedorActivo(dto.getProveedorId(), "proveedorId"));
        entrada.setBodega(referencias.bodegaActiva(dto.getBodegaId(), "bodegaId"));
        entrada.setDocumentoProveedor(dto.getDocumentoProveedor());
        entrada.setObservacion(dto.getObservacion());
        entrada.setEstado(Entrada.REGISTRADA);
        entrada.setUsuario(InventarioService.usuarioActual());

        String referencia = "Entrada " + numero;
        BigDecimal subtotal = BigDecimal.ZERO;
        int fila = 0;
        for (DetalleEntradaDTO d : dto.getDetalles()) {
            DetalleEntrada detalle = new DetalleEntrada();
            detalle.setProducto(referencias.productoActivo(d.getProductoId(), ++fila));
            detalle.setCantidad(d.getCantidad());
            detalle.setCostoUnitario(d.getCostoUnitario().setScale(2, RoundingMode.HALF_UP));
            detalle.setSubtotal(detalle.getCostoUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            entrada.agregarDetalle(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
            inventarioService.registrarEntrada(detalle.getProducto(), entrada.getBodega(), d.getCantidad(),
                    MovimientoInventario.ENTRADA_PROVEEDOR, referencia);
        }
        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        entrada.setSubtotal(subtotal);
        entrada.setIva(iva);
        entrada.setTotal(subtotal.add(iva));
        return mapper.toDTO(repository.save(entrada));
    }

    @Override
    public EntradaDTO anular(Long id) {
        Entrada entrada = obtener(id);
        if (!Entrada.REGISTRADA.equals(entrada.getEstado())) {
            throw new ReglaNegocioException("La entrada ya está anulada.");
        }
        // Si parte de la mercancía ya salió de la bodega, registrarSalida lo rechaza y no se anula nada.
        String referencia = "Anulación entrada " + entrada.getNumero();
        for (DetalleEntrada d : entrada.getDetalles()) {
            inventarioService.registrarSalida(d.getProducto(), entrada.getBodega(), d.getCantidad(),
                    MovimientoInventario.ANULACION, referencia);
        }
        entrada.setEstado(Entrada.ANULADA);
        return mapper.toDTO(entrada);
    }

    private Entrada obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("La entrada solicitada no existe"));
    }
}
