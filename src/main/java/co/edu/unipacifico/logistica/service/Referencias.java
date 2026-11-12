package co.edu.unipacifico.logistica.service;

import co.edu.unipacifico.logistica.exception.ReglaNegocioException;
import co.edu.unipacifico.logistica.model.Bodega;
import co.edu.unipacifico.logistica.model.Cliente;
import co.edu.unipacifico.logistica.model.Producto;
import co.edu.unipacifico.logistica.model.Proveedor;
import co.edu.unipacifico.logistica.repository.BodegaRepository;
import co.edu.unipacifico.logistica.repository.ClienteRepository;
import co.edu.unipacifico.logistica.repository.ProductoRepository;
import co.edu.unipacifico.logistica.repository.ProveedorRepository;
import org.springframework.stereotype.Component;

/**
 * Busca los registros que eligió el usuario en un formulario y comprueba que existan y estén activos.
 * Si no, lanza ReglaNegocioException indicando el campo, para mostrar el error junto a él.
 */
@Component
public class Referencias {

    private final ProveedorRepository proveedorRepository;
    private final ClienteRepository clienteRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;

    public Referencias(ProveedorRepository proveedorRepository, ClienteRepository clienteRepository,
                       BodegaRepository bodegaRepository, ProductoRepository productoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.clienteRepository = clienteRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
    }

    public Proveedor proveedorActivo(Long id, String campo) {
        return proveedorRepository.findById(id).filter(Proveedor::getEstado)
                .orElseThrow(() -> new ReglaNegocioException(campo, "El proveedor no existe o está inactivo"));
    }

    public Cliente clienteActivo(Long id, String campo) {
        return clienteRepository.findById(id).filter(Cliente::getEstado)
                .orElseThrow(() -> new ReglaNegocioException(campo, "El cliente no existe o está inactivo"));
    }

    public Bodega bodegaActiva(Long id, String campo) {
        return bodegaRepository.findById(id).filter(Bodega::getEstado)
                .orElseThrow(() -> new ReglaNegocioException(campo, "La bodega no existe o está inactiva"));
    }

    /** fila: número de fila del detalle (desde 1), para indicar en qué fila está el problema. */
    public Producto productoActivo(Long id, int fila) {
        return productoRepository.findById(id).filter(Producto::getEstado)
                .orElseThrow(() -> new ReglaNegocioException(
                        "El producto de la fila " + fila + " no existe o está inactivo"));
    }

    /** Número consecutivo para un documento nuevo, por ejemplo ENT-00001. */
    public static String consecutivo(String prefijo, long existentes) {
        return String.format("%s-%05d", prefijo, existentes + 1);
    }
}
