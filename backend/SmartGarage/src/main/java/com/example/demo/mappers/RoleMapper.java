package com.example.demo.mappers;

import com.example.demo.DTO.RoleDTO;
import com.example.demo.DTO.VisitDTO;
import com.example.demo.DTO.role.RoleCreateDTO;
import com.example.demo.DTO.role.RoleUpdateDTO;
import com.example.demo.models.Role;
import com.example.demo.models.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "roleName", source = "roleName")
    Role roleDtoToRole(RoleCreateDTO roleDTO);

    @Mapping(target = "roleName", source = "roleName")
    Role roleDtoToRole(RoleUpdateDTO roleDTO);

    @Mapping(target = "roleName", source = "roleName")
    Role toEntity(RoleDTO dto);
}
