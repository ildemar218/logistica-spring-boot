package co.edu.unipacifico.logistica.dto;

import java.util.ArrayList;
import java.util.List;

/** Usuario visto desde el módulo de administración: datos básicos, estado y roles. */
public class UsuarioAdminDTO {

    private Long id;
    private String login;
    private String nombreCompleto;
    private String email;
    private Boolean enabled;
    private List<String> roles = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
