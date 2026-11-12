package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.dto.DetalleCantidadDTO;
import co.edu.unipacifico.logistica.dto.SalidaDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.SalidaMapper;
import co.edu.unipacifico.logistica.model.DetalleSalida;
import co.edu.unipacifico.logistica.model.MovimientoInventario;
import co.edu.unipacifico.logistica.model.Salida;
import co.edu.unipacifico.logistica.repository.SalidaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SalidaServiceImpl implements SalidaService {

    private final SalidaRepository repository;
    private final SalidaMapper mapper;
    private final Referencias referencias;
    private final InventarioService inventarioService;

    public SalidaServiceImpl(SalidaRepository repository, SalidaMapper mapper,
                             Referencias referencias, InventarioService inventarioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.referencias = referencias;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalidaDTO> listar() {
        return repository.findAllByOrderBySalidaIdDesc().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SalidaDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String siguienteNumero() {
        return Referencias.consecutivo("SAL", repository.count());
    }

    /** Todo en una transacción: si un producto no alcanza, no se descuenta ninguno. */
    @Override
    public SalidaDTO guardar(SalidaDTO dto) {
        String numero = dto.getNumero().trim().toUpperCase();
        if (repository.existsByNumeroIgnoreCase(numero)) {
            throw new ReglaNegocioException("numero", "Ya existe una salida con ese número");
        }
        Salida salida = new Salida();
        salida.setNumero(numero);
        salida.setFecha(dto.getFecha());
        salida.setTipo(dto.getTipo());
        salida.setBodega(referencias.bodegaActiva(dto.getBodegaId(), "bodegaId"));
        if ("VENTA".equals(dto.getTipo())) {
            if (dto.getClienteId() == null) {
                throw new ReglaNegocioException("clienteId", "Una salida por venta debe indicar el cliente");
            }
            salida.setCliente(referencias.clienteActivo(dto.getClienteId(), "clienteId"));
        }
        salida.setMotivo(dto.getMotivo().trim());
        salida.setEstado(Salida.REGISTRADA);
        salida.setUsuario(InventarioService.usuarioActual());

        String referencia = "Salida " + numero;
        int fila = 0;
        for (DetalleCantidadDTO d : dto.getDetalles()) {
            DetalleSalida detalle = new DetalleSalida();
            detalle.setProducto(referencias.productoActivo(d.getProductoId(), ++fila));
            detalle.setCantidad(d.getCantidad());
            inventarioService.registrarSalida(detalle.getProducto(), salida.getBodega(), d.getCantidad(),
                    salida.getTipo(), referencia);
            salida.agregarDetalle(detalle);
        }
        return mapper.toDTO(repository.save(salida));
    }

    @Override
    public SalidaDTO anular(Long id) {
        Salida salida = obtener(id);
        if (!Salida.REGISTRADA.equals(salida.getEstado())) {
            throw new ReglaNegocioException("La salida ya está anulada.");
        }
        String referencia = "Anulación salida " + salida.getNumero();
        for (DetalleSalida d : salida.getDetalles()) {
            inventarioService.registrarEntrada(d.getProducto(), salida.getBodega(), d.getCantidad(),
                    MovimientoInventario.ANULACION, referencia);
        }
        salida.setEstado(Salida.ANULADA);
        return mapper.toDTO(salida);
    }

    private Salida obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("La salida solicitada no existe"));
    }
}
