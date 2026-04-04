package com.example.demo.mappers;

import com.example.demo.DTO.role.RoleResponseDTO;
import com.example.demo.DTO.user.UserCreateDTO;
import com.example.demo.DTO.user.UserLoginDTO;
import com.example.demo.DTO.user.UserResponseDTO;
import com.example.demo.DTO.user.UserUpdateDTO;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( dto.name() );
        user.address( dto.address() );
        user.username( dto.username() );
        user.email( dto.email() );
        user.phone( dto.phone() );

        return user.build();
    }

    @Override
    public User toEntity(UserUpdateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( dto.name() );
        user.address( dto.address() );
        user.username( dto.username() );
        user.email( dto.email() );
        user.phone( dto.phone() );

        return user.build();
    }

    @Override
    public User toEntity(UserLoginDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.password( dto.password() );
        user.email( dto.email() );

        return user.build();
    }

    @Override
    public UserResponseDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO.UserResponseDTOBuilder userResponseDTO = UserResponseDTO.builder();

        userResponseDTO.role( roleToRoleResponseDTO( user.getRole() ) );
        userResponseDTO.id( user.getId() );
        userResponseDTO.name( user.getName() );
        userResponseDTO.username( user.getUsername() );
        userResponseDTO.email( user.getEmail() );
        userResponseDTO.phone( user.getPhone() );
        userResponseDTO.address( user.getAddress() );

        return userResponseDTO.build();
    }

    protected RoleResponseDTO roleToRoleResponseDTO(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponseDTO.RoleResponseDTOBuilder roleResponseDTO = RoleResponseDTO.builder();

        roleResponseDTO.id( role.getId() );
        if ( role.getRoleName() != null ) {
            roleResponseDTO.roleName( role.getRoleName().name() );
        }

        return roleResponseDTO.build();
    }
}
