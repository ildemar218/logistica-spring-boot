package co.edu.unipacifico.logistica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ProveedorDTO {

    private Long proveedorId;

    @NotBlank(message = "Tipo de documento es obligatorio")
    @Size(max = 20, message = "Tipo de documento: máximo 20 caracteres")
    private String tipoDocumento;

    @NotBlank(message = "Número de documento es obligatorio")
    @Size(max = 30, message = "Número de documento: máximo 30 caracteres")
    private String numeroDocumento;

    @NotBlank(message = "Razón social es obligatorio")
    @Size(max = 120, message = "Razón social: máximo 120 caracteres")
    private String razonSocial;

    @Size(max = 100, message = "Persona de contacto: máximo 100 caracteres")
    private String contacto;

    @Size(max = 30, message = "Teléfono: máximo 30 caracteres")
    private String telefono;

    @Size(max = 120, message = "Correo: máximo 120 caracteres")
    @Email(message = "El correo no es válido")
    private String correo;

    @Size(max = 200, message = "Dirección: máximo 200 caracteres")
    private String direccion;

    @Size(max = 80, message = "Ciudad: máximo 80 caracteres")
    private String ciudad;

    private Boolean estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
