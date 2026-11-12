package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Bodega;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    List<Bodega> findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCase(
            String codigo, String nombre);

    List<Bodega> findByEstadoTrueOrderByNombre();

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndBodegaIdNot(String codigo, Long bodegaId);
}
