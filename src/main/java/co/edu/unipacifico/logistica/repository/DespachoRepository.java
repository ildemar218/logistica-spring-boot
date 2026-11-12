package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Despacho;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    List<Despacho> findAllByOrderByDespachoIdDesc();

    List<Despacho> findByEstadoOrderByDespachoIdDesc(String estado);

    boolean existsByNumeroIgnoreCase(String numero);

    long countByEstado(String estado);
}
