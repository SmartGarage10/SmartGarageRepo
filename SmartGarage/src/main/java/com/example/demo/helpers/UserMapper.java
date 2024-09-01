package com.example.demo.helpers;

import com.example.demo.DTO.RoleDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.models.Role;

import com.example.demo.models.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService){
        this.userService = userService;
    }

    public User fromDto(int id, UserDTO userDto){
        User user = fromDto(userDto);
        user.setId(id);

        return user;
    }


    public User fromDto(UserDTO userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setRole(mapRoleDtoToRole(userDto.getRole()));

        return user;
    }

    private Role mapRoleDtoToRole(RoleDTO roleDto) {
        if (roleDto == null) {
            return null;
        }

        Role role = new Role();
        role.setRoleId(roleDto.getRoleId());
        role.setRoleName(mapStringToRoleType(roleDto.getRoleName()));
        return role;
    }
    private Role.RoleType mapStringToRoleType(String roleName) {
        try {
            return Role.RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role type: " + roleName);
        }
    }

}
