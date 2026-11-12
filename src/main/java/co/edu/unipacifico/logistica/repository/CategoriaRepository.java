package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre, String descripcion);

    List<Categoria> findByEstadoTrueOrderByNombre();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndCategoriaIdNot(String nombre, Long categoriaId);
}
