package com.example.demo.mappers;

import com.example.demo.DTO.RoleDTO;
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
    public Role roleDtoToRole(RoleDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role role = new Role();

        if ( roleDTO.getRoleName() != null ) {
            role.setRoleName( Enum.valueOf( Role.RoleType.class, roleDTO.getRoleName() ) );
        }

        return role;
    }
}
