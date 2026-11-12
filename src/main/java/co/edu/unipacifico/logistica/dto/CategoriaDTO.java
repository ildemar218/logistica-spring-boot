package co.edu.unipacifico.logistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaDTO {

    private Long categoriaId;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 80, message = "Nombre: máximo 80 caracteres")
    private String nombre;

    @Size(max = 255, message = "Descripción: máximo 255 caracteres")
    private String descripcion;

    private Boolean estado;

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
