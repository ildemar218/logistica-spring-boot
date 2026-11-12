package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.OrdenCompra;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findAllByOrderByOrdenIdDesc();

    List<OrdenCompra> findByEstadoOrderByOrdenIdDesc(String estado);

    boolean existsByNumeroIgnoreCase(String numero);

    long countByEstado(String estado);
}
