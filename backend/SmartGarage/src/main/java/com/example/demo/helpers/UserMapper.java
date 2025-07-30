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

    private final RoleService roleService;

    @Autowired
    public UserMapper(RoleService roleService) {
        this.roleService = roleService;
    }

    public User fromDto(int id, UserDTO userDto) {
        User user = fromDto(userDto);
        user.setId(id);
        return user;
    }

    public User fromDto(UserDTO userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());

        // Handle role mapping
        if (userDto.getRole() != null) {
            user.setRole(mapRoleDtoToRole(userDto.getRole()));
        }

        return user;
    }

//    public User fromDto(EditUserDTO userDto) {
//        if (userDto == null) {
//            return null;
//        }
//
//        User user = new User();
//        user.setUsername(userDto.getUsername());
//        user.setEmail(userDto.getEmail());
//        user.setPhone(userDto.getPhone());
//
//        // Handle role mapping for EditUserDTO
//        if (userDto.get() != null) {
//            user.setRole(mapRoleDtoToRole(userDto.getRole()));
//        }
//
//        return user;
//    }

    public Role mapRoleDtoToRole(RoleDTO roleDto) {
        if (roleDto == null) {
            return null;
        }
        return roleService.getRoleByRoleName(roleDto.getRoleName());
    }

    private Role.RoleType mapStringToRoleType(String roleName) {
        if (roleName == null) {
            return null;
        }

        try {
            return Role.RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role type: " + roleName);
        }
    }
}