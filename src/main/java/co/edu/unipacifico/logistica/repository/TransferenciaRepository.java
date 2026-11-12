package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Transferencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    List<Transferencia> findAllByOrderByTransferenciaIdDesc();

    boolean existsByNumeroIgnoreCase(String numero);
}
