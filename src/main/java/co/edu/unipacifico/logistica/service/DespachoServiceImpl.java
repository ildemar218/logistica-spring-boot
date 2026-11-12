package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.config.Roles;
import co.edu.unipacifico.logistica.dto.DespachoDTO;
import co.edu.unipacifico.logistica.dto.DetalleDespachoDTO;
import co.edu.unipacifico.logistica.exception.RecursoNoEncontradoException;
import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.mapper.DespachoMapper;
import co.edu.unipacifico.logistica.model.Despacho;
import co.edu.unipacifico.logistica.model.DetalleDespacho;
import co.edu.unipacifico.logistica.model.MovimientoInventario;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.model.Rol;
import co.edu.unipacifico.logistica.model.Usuarios;
import co.edu.unipacifico.logistica.repository.DespachoRepository;
import co.edu.unipacifico.logistica.repository.UsuariosRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DespachoServiceImpl implements DespachoService {

    private final DespachoRepository repository;
    private final DespachoMapper mapper;
    private final Referencias referencias;
    private final UsuariosRepository usuariosRepository;
    private final InventarioService inventarioService;

    public DespachoServiceImpl(DespachoRepository repository, DespachoMapper mapper, Referencias referencias,
                               UsuariosRepository usuariosRepository, InventarioService inventarioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.referencias = referencias;
        this.usuariosRepository = usuariosRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DespachoDTO> listar(String estado, String login) {
        return repository.buscar(estado == null || estado.isBlank() ? null : estado, login)
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DespachoDTO buscarPorId(Long id) {
        return mapper.toDTO(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String siguienteNumero() {
        return Referencias.consecutivo("DSP", repository.count());
    }

    @Override
    public DespachoDTO guardar(DespachoDTO dto) {
        String numero = dto.getNumero().trim().toUpperCase();
        if (repository.existsByNumeroIgnoreCase(numero)) {
            throw new ReglaNegocioException("numero", "Ya existe un despacho con ese número");
        }
        Despacho despacho = new Despacho();
        despacho.setNumero(numero);
        despacho.setFecha(dto.getFecha());
        despacho.setCliente(referencias.clienteActivo(dto.getClienteId(), "clienteId"));
        despacho.setBodega(referencias.bodegaActiva(dto.getBodegaId(), "bodegaId"));
        despacho.setDireccionEntrega(dto.getDireccionEntrega().trim());
        despacho.setTransportador(transportador(dto.getTransportadorId()));
        despacho.setVehiculo(dto.getVehiculo());
        despacho.setNumeroGuia(dto.getNumeroGuia());
        despacho.setObservacion(dto.getObservacion());

        // Comprueba que hoy haya existencias para todo el pedido (sumando filas del mismo producto).
        Map<Long, Integer> pedido = new HashMap<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int fila = 0;
        for (DetalleDespachoDTO d : dto.getDetalles()) {
            Producto producto = referencias.productoActivo(d.getProductoId(), ++fila);
            int total = pedido.merge(producto.getProductoId(), d.getCantidad(), Integer::sum);
            int disponible = inventarioService.disponible(producto.getProductoId(), despacho.getBodega().getBodegaId());
            if (total > disponible) {
                throw new ReglaNegocioException(String.format(
                        "Existencias insuficientes de %s en %s: disponible %d, solicitado %d.",
                        producto.getNombre(), despacho.getBodega().getNombre(), disponible, total));
            }
            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            despacho.agregarDetalle(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        BigDecimal iva = subtotal.multiply(EntradaServiceImpl.TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        despacho.setSubtotal(subtotal);
        despacho.setIva(iva);
        despacho.setTotal(subtotal.add(iva));
        despacho.cambiarEstado(Despacho.PENDIENTE, InventarioService.usuarioActual(),
                "Orden creada. Transportador: " + DespachoMapper.nombre(despacho.getTransportador()));
        return mapper.toDTO(repository.save(despacho));
    }

    @Override
    public DespachoDTO asignar(Long id, Long transportadorId, String vehiculo) {
        Despacho despacho = obtener(id);
        exigirEstado(despacho, Despacho.PENDIENTE, "reasignar");
        if (transportadorId == null) {
            throw new ReglaNegocioException("Seleccione el transportador");
        }
        despacho.setTransportador(transportador(transportadorId));
        despacho.setVehiculo(vehiculo == null || vehiculo.isBlank() ? null : vehiculo.trim());
        despacho.cambiarEstado(Despacho.PENDIENTE, InventarioService.usuarioActual(),
                "Asignado a " + DespachoMapper.nombre(despacho.getTransportador())
                        + (despacho.getVehiculo() != null ? " — vehículo " + despacho.getVehiculo() : ""));
        return mapper.toDTO(despacho);
    }

    @Override
    public DespachoDTO despachar(Long id) {
        Despacho despacho = obtener(id);
        exigirEstado(despacho, Despacho.PENDIENTE, "despachar");
        String referencia = "Despacho " + despacho.getNumero();
        for (DetalleDespacho d : despacho.getDetalles()) {
            inventarioService.registrarSalida(d.getProducto(), despacho.getBodega(), d.getCantidad(),
                    MovimientoInventario.DESPACHO, referencia);
        }
        despacho.setFechaSalida(LocalDateTime.now());
        despacho.cambiarEstado(Despacho.EN_RUTA, InventarioService.usuarioActual(),
                "Salió de " + despacho.getBodega().getNombre());
        return mapper.toDTO(despacho);
    }

    @Override
    public DespachoDTO entregar(Long id, String observacion, String login, boolean soloAsignado) {
        Despacho despacho = obtener(id);
        exigirAsignado(despacho, login, soloAsignado);
        exigirEstado(despacho, Despacho.EN_RUTA, "marcar como entregado");
        despacho.setFechaEntrega(LocalDateTime.now());
        despacho.cambiarEstado(Despacho.ENTREGADO, login,
                observacion == null || observacion.isBlank() ? "Entregado al cliente" : observacion.trim());
        return mapper.toDTO(despacho);
    }

    @Override
    public DespachoDTO devolver(Long id, String motivo, String login, boolean soloAsignado) {
        Despacho despacho = obtener(id);
        exigirAsignado(despacho, login, soloAsignado);
        exigirEstado(despacho, Despacho.EN_RUTA, "registrar como devuelto");
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaNegocioException("Indique el motivo de la devolución");
        }
        String referencia = "Devolución despacho " + despacho.getNumero();
        for (DetalleDespacho d : despacho.getDetalles()) {
            inventarioService.registrarEntrada(d.getProducto(), despacho.getBodega(), d.getCantidad(),
                    MovimientoInventario.DEVOLUCION, referencia);
        }
        despacho.setMotivoDevolucion(motivo.trim());
        despacho.cambiarEstado(Despacho.DEVUELTO, login,
                "Devuelto: " + motivo.trim() + ". La mercancía regresó a " + despacho.getBodega().getNombre());
        return mapper.toDTO(despacho);
    }

    @Override
    public DespachoDTO cancelar(Long id, String motivo) {
        Despacho despacho = obtener(id);
        exigirEstado(despacho, Despacho.PENDIENTE, "cancelar");
        despacho.cambiarEstado(Despacho.CANCELADO, InventarioService.usuarioActual(),
                motivo == null || motivo.isBlank() ? "Cancelado antes de salir de bodega" : motivo.trim());
        return mapper.toDTO(despacho);
    }

    @Override
    @Transactional(readOnly = true)
    public long contar(String estado, String login) {
        return login == null ? repository.countByEstado(estado)
                : repository.countByEstadoAndTransportadorLogin(estado, login);
    }

    private Usuarios transportador(Long id) {
        Usuarios u = usuariosRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("transportadorId", "El transportador no existe"));
        boolean esTransportador = u.getRoles().stream().map(Rol::getNombre).anyMatch(Roles.TRANSPORTADOR::equals);
        if (!esTransportador || !Boolean.TRUE.equals(u.getEnabled())) {
            throw new ReglaNegocioException("transportadorId", "El usuario elegido no es un transportador activo");
        }
        return u;
    }

    private void exigirEstado(Despacho d, String esperado, String accion) {
        if (!esperado.equals(d.getEstado())) {
            throw new ReglaNegocioException(String.format(
                    "No se puede %s: el despacho está %s y debe estar %s.", accion, d.getEstado(), esperado));
        }
    }

    private void exigirAsignado(Despacho d, String login, boolean soloAsignado) {
        if (soloAsignado && !d.getTransportador().getLogin().equals(login)) {
            throw new ReglaNegocioException("Este despacho está asignado a otro transportador.");
        }
    }

    private Despacho obtener(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("El despacho solicitado no existe"));
    }
}
