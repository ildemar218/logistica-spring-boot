package co.edu.unipacifico.logistica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unipacifico.logistica.model.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("select u from Usuarios u left join fetch u.roles where u.login = :login")
    Optional<Usuarios> findByLoginConRoles(@Param("login") String login);
}