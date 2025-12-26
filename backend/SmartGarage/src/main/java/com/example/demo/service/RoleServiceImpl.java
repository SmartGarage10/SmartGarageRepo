package com.example.demo.service;


import com.example.demo.exceptions.RequestValidationException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.models.Role.RoleType;
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
    public Role getRoleById(Integer roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Role with id %d not found.", roleId)
                ));
    }

    @Override
    public Role getRoleByRoleName(String roleName){
        RoleType roleType;
        try {
            roleType = RoleType.valueOf(roleName);
        } catch (RequestValidationException e) {
            throw new RequestValidationException(String.format("Invalid role name: %s", roleName));
        }

        return roleRepository.findByRoleName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role with name %s not found.", roleName)));
    }
}
