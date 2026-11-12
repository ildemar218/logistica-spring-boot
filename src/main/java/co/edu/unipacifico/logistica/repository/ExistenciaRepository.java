package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Existencia;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExistenciaRepository extends JpaRepository<Existencia, Long> {

    /**
     * Bloquea la fila mientras dura la transacción: si dos despachos descuentan el mismo
     * producto a la vez, el segundo espera al primero y nunca se vende más de lo que hay.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Existencia e where e.producto.productoId = :productoId and e.bodega.bodegaId = :bodegaId")
    Optional<Existencia> bloquear(@Param("productoId") Long productoId, @Param("bodegaId") Long bodegaId);

    @Query("select e from Existencia e where (:bodegaId is null or e.bodega.bodegaId = :bodegaId) "
            + "order by e.bodega.nombre, e.producto.nombre")
    List<Existencia> listarPorBodega(@Param("bodegaId") Long bodegaId);

    /** Total de unidades de cada producto sumando todas las bodegas: [productoId, total]. */
    @Query("select e.producto.productoId, sum(e.cantidad) from Existencia e group by e.producto.productoId")
    List<Object[]> totalesPorProducto();
}
