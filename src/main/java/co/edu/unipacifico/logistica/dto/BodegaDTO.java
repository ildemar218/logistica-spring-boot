package co.edu.unipacifico.logistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BodegaDTO {

    private Long bodegaId;

    @NotBlank(message = "Código es obligatorio")
    @Size(max = 20, message = "Código: máximo 20 caracteres")
    private String codigo;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 100, message = "Nombre: máximo 100 caracteres")
    private String nombre;

    @NotBlank(message = "Ciudad es obligatorio")
    @Size(max = 80, message = "Ciudad: máximo 80 caracteres")
    private String ciudad;

    @Size(max = 200, message = "Dirección: máximo 200 caracteres")
    private String direccion;

    @Size(max = 100, message = "Responsable: máximo 100 caracteres")
    private String responsable;

    @Size(max = 30, message = "Teléfono: máximo 30 caracteres")
    private String telefono;

    private Boolean estado;

    public Long getBodegaId() { return bodegaId; }
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
