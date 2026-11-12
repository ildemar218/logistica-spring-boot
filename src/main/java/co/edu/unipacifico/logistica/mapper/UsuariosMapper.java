package co.edu.unipacifico.logistica.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.unipacifico.logistica.dto.UsuarioDTO;
import co.edu.unipacifico.logistica.dto.UsuarioRegistroDTO;
import co.edu.unipacifico.logistica.model.Usuarios;

@Mapper(componentModel = "spring")
public interface UsuariosMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Usuarios toEntity(UsuarioRegistroDTO dto);

    UsuarioDTO toDto(Usuarios usuario);
}