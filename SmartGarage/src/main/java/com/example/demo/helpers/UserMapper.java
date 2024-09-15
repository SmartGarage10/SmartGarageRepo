package com.example.demo.helpers;

import com.example.demo.DTO.EditUserDTO;
import com.example.demo.DTO.RoleDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.models.Role;

import com.example.demo.models.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserMapper(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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
        user.setRole(roleService.getRoleByRoleName(userDto.getRole().getRoleName()));

        return user;
    }

    public User fromDto(EditUserDTO userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());

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

