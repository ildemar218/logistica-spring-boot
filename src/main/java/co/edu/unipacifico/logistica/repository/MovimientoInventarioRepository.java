package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.MovimientoInventario;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    @Query("select m from MovimientoInventario m "
            + "where (:productoId is null or m.producto.productoId = :productoId) "
            + "and (:bodegaId is null or m.bodega.bodegaId = :bodegaId) "
            + "order by m.movimientoId desc")
    List<MovimientoInventario> buscar(@Param("productoId") Long productoId,
                                      @Param("bodegaId") Long bodegaId, Pageable limite);
}
