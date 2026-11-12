package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Entrada;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    List<Entrada> findAllByOrderByEntradaIdDesc();

    boolean existsByNumeroIgnoreCase(String numero);
}
