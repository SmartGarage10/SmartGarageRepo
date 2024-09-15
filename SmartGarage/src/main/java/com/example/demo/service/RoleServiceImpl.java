package com.example.demo.service;

import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleByRoleName(String roleName) {
        Role.RoleType roleType;
        try {
            roleType = Role.RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role name: " + roleName, e);
        }

        return roleRepository.findRoleByRoleName(roleType)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));
    }
}
