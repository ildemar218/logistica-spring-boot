package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCase(
            String codigo, String nombre);

    List<Producto> findByEstadoTrueOrderByNombre();

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndProductoIdNot(String codigo, Long productoId);
}
