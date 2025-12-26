package com.example.demo.repositories;

import com.example.demo.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByRoleNameIn(List<String> roleNames);
    Optional<Role> findRoleByRoleId(Integer roleId);
    Optional<Role> findByRoleName(Role.RoleType roleName);
}
