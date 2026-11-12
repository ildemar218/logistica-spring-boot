package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Proveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByNumeroDocumentoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(
            String numeroDocumento, String razonSocial);

    List<Proveedor> findByEstadoTrueOrderByRazonSocial();

    boolean existsByNumeroDocumentoIgnoreCase(String numeroDocumento);

    boolean existsByNumeroDocumentoIgnoreCaseAndProveedorIdNot(String numeroDocumento, Long proveedorId);
}
