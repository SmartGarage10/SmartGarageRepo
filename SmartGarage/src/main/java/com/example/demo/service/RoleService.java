package com.example.demo.service;

import com.example.demo.models.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleByRoleName(String roleName);
}
