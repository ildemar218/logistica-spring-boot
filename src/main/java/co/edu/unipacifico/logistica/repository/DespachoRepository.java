package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Despacho;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    /** Filtros opcionales por estado y por transportador (login); null = sin filtro. */
    @Query("select d from Despacho d where (:estado is null or d.estado = :estado) "
            + "and (:login is null or d.transportador.login = :login) order by d.despachoId desc")
    List<Despacho> buscar(@Param("estado") String estado, @Param("login") String login);

    boolean existsByNumeroIgnoreCase(String numero);

    long countByEstado(String estado);

    long countByEstadoAndTransportadorLogin(String estado, String login);
}
