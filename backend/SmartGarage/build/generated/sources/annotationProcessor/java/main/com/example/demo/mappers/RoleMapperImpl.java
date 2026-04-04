package com.example.demo.mappers;

import com.example.demo.DTO.RoleDTO;
import com.example.demo.DTO.role.RoleCreateDTO;
import com.example.demo.DTO.role.RoleUpdateDTO;
import com.example.demo.models.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role roleDtoToRole(RoleCreateDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role role = new Role();

        if ( roleDTO.roleName() != null ) {
            role.setRoleName( Enum.valueOf( Role.RoleType.class, roleDTO.roleName() ) );
        }

        return role;
    }

    @Override
    public Role roleDtoToRole(RoleUpdateDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role role = new Role();

        if ( roleDTO.roleName() != null ) {
            role.setRoleName( Enum.valueOf( Role.RoleType.class, roleDTO.roleName() ) );
        }

        return role;
    }

    @Override
    public Role toEntity(RoleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Role role = new Role();

        if ( dto.getRoleName() != null ) {
            role.setRoleName( Enum.valueOf( Role.RoleType.class, dto.getRoleName() ) );
        }

        return role;
    }
}
