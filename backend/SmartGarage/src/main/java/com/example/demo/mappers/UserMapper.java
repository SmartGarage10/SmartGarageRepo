package com.example.demo.mappers;

import com.example.demo.DTO.UserDTO;
import com.example.demo.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDto(User user);
    User userDtoToUser(UserDTO userDTO);
}
