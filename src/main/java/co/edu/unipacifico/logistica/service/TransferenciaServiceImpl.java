package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DetalleCantidadDTO;
import co.edu.unipacifico.logistica.dto.TransferenciaDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.TransferenciaMapper;
import co.edu.unipacifico.logistica.model.DetalleTransferencia;
import co.edu.unipacifico.logistica.model.MovimientoInventario;
import co.edu.unipacifico.logistica.model.Transferencia;
import co.edu.unipacifico.logistica.repository.TransferenciaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransferenciaServiceImpl implements TransferenciaService {

    private final TransferenciaRepository repository;
    private final TransferenciaMapper mapper;
    private final Referencias referencias;
    private final InventarioService inventarioService;

    public TransferenciaServiceImpl(TransferenciaRepository repository, TransferenciaMapper mapper,
                                    Referencias referencias, InventarioService inventarioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.referencias = referencias;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferenciaDTO> listar() {
        return repository.findAllByOrderByTransferenciaIdDesc().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransferenciaDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String siguienteNumero() {
        return Referencias.consecutivo("TRF", repository.count());
    }

    /**
     * Por cada producto: SALIDA en origen y ENTRADA en destino, ambas con concepto TRANSFERENCIA.
     * Si el origen no tiene existencias suficientes de algún producto, no se mueve nada.
     */
    @Override
    public TransferenciaDTO guardar(TransferenciaDTO dto) {
        String numero = dto.getNumero().trim().toUpperCase();
        if (repository.existsByNumeroIgnoreCase(numero)) {
            throw new ReglaNegocioException("numero", "Ya existe una transferencia con ese número");
        }
        if (dto.getBodegaOrigenId().equals(dto.getBodegaDestinoId())) {
            throw new ReglaNegocioException("bodegaDestinoId", "La bodega de destino debe ser distinta a la de origen");
        }
        Transferencia t = new Transferencia();
        t.setNumero(numero);
        t.setFecha(dto.getFecha());
        t.setBodegaOrigen(referencias.bodegaActiva(dto.getBodegaOrigenId(), "bodegaOrigenId"));
        t.setBodegaDestino(referencias.bodegaActiva(dto.getBodegaDestinoId(), "bodegaDestinoId"));
        t.setResponsable(dto.getResponsable());
        t.setObservacion(dto.getObservacion());
        t.setUsuario(InventarioService.usuarioActual());

        String referencia = "Transferencia " + numero;
        int fila = 0;
        for (DetalleCantidadDTO d : dto.getDetalles()) {
            DetalleTransferencia detalle = new DetalleTransferencia();
            detalle.setProducto(referencias.productoActivo(d.getProductoId(), ++fila));
            detalle.setCantidad(d.getCantidad());
            inventarioService.registrarSalida(detalle.getProducto(), t.getBodegaOrigen(), d.getCantidad(),
                    MovimientoInventario.TRANSFERENCIA, referencia);
            inventarioService.registrarEntrada(detalle.getProducto(), t.getBodegaDestino(), d.getCantidad(),
                    MovimientoInventario.TRANSFERENCIA, referencia);
            t.agregarDetalle(detalle);
        }
        return mapper.toDTO(repository.save(t));
    }

    private Transferencia obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("La transferencia solicitada no existe"));
    }
}
