package com.example.demo.mappers;

import com.example.demo.DTO.user.UserCreateDTO;
import com.example.demo.DTO.user.UserLoginDTO;
import com.example.demo.DTO.user.UserResponseDTO;
import com.example.demo.DTO.user.UserUpdateDTO;
import com.example.demo.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "role", ignore = true)
    User toEntity(UserUpdateDTO dto);

    User toEntity(UserLoginDTO dto);

    @Mapping(target = "role", source = "role")
    UserResponseDTO toDTO(User user);
}

