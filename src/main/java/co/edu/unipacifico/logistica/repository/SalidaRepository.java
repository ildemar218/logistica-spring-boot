package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Salida;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalidaRepository extends JpaRepository<Salida, Long> {

    List<Salida> findAllByOrderBySalidaIdDesc();

    boolean existsByNumeroIgnoreCase(String numero);
}
