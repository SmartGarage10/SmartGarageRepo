package com.example.demo.mappers;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import lombok.NoArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    User userDtoToUser(UserDTO userDTO);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "name", ignore = true)
//    @Mapping(target = "username", ignore = true)
//    @Mapping(target = "phone", ignore = true)
//    @Mapping(target = "address", ignore = true)
//    @Mapping(target = "vehicles", ignore = true)
    User userDtoToUserLogin(LoginDTO userDTO);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "role", source = "role")  // This will map the role parameter!
    User userDtoToUserWithRole(UserDTO userDTO, Role role);
}

