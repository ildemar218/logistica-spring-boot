package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.MovimientoInventario;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /** Kardex con filtros opcionales: cada filtro en null no se aplica. */
    @Query("select m from MovimientoInventario m "
            + "where (:productoId is null or m.producto.productoId = :productoId) "
            + "and (:bodegaId is null or m.bodega.bodegaId = :bodegaId) "
            + "and (:tipo is null or m.tipo = :tipo) "
            + "and (:concepto is null or m.concepto = :concepto) "
            + "and (:desde is null or m.fecha >= :desde) "
            + "and (:hasta is null or m.fecha < :hasta) "
            + "order by m.movimientoId desc")
    List<MovimientoInventario> buscar(@Param("productoId") Long productoId,
                                      @Param("bodegaId") Long bodegaId,
                                      @Param("tipo") String tipo,
                                      @Param("concepto") String concepto,
                                      @Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta,
                                      Pageable limite);
}
