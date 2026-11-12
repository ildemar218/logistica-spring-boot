package co.edu.unipacifico.logistica.repository;

import co.edu.unipacifico.logistica.model.Usuarios;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("select u from Usuarios u left join fetch u.roles where u.login = :login")
    Optional<Usuarios> findByLoginConRoles(@Param("login") String login);

    @Query("select distinct u from Usuarios u left join fetch u.roles order by u.login")
    List<Usuarios> findAllConRoles();

    /** Usuarios activos que tienen el rol indicado, por ejemplo los transportadores. */
    @Query("select distinct u from Usuarios u join u.roles r where r.nombre = :rol and u.enabled = true order by u.nombre")
    List<Usuarios> findActivosConRol(@Param("rol") String rol);

    @Query("select count(u) > 0 from Usuarios u join u.roles r where r.nombre = :rol")
    boolean existeAlgunoConRol(@Param("rol") String rol);
}
