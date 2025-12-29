package com.example.demo.mappers;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.RoleDTO;
import com.example.demo.DTO.UserDTO;
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
    public User userDtoToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( userDTO.getName() );
        user.address( userDTO.getAddress() );
        user.username( userDTO.getUsername() );
        user.email( userDTO.getEmail() );
        user.phone( userDTO.getPhone() );
        user.role( roleDTOToRole( userDTO.getRole() ) );

        return user.build();
    }

    @Override
    public User userDtoToUserLogin(LoginDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.password( userDTO.getPassword() );
        user.email( userDTO.getEmail() );

        return user.build();
    }

    @Override
    public User userDtoToUserWithRole(UserDTO userDTO, Role role) {
        if ( userDTO == null && role == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        if ( userDTO != null ) {
            user.name( userDTO.getName() );
            user.address( userDTO.getAddress() );
            user.username( userDTO.getUsername() );
            user.email( userDTO.getEmail() );
            user.phone( userDTO.getPhone() );
        }
        user.role( role );

        return user.build();
    }

    protected Role roleDTOToRole(RoleDTO roleDTO) {
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
