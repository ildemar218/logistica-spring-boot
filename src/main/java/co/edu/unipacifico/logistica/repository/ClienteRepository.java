package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNumeroDocumentoContainingIgnoreCaseOrNombresContainingIgnoreCase(
            String numeroDocumento, String nombres);

    List<Cliente> findByEstadoTrueOrderByNombres();

    boolean existsByNumeroDocumentoIgnoreCase(String numeroDocumento);

    boolean existsByNumeroDocumentoIgnoreCaseAndClienteIdNot(String numeroDocumento, Long clienteId);
}
