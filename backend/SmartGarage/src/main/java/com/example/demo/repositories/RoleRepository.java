package com.example.demo.repositories;

import com.example.demo.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByRoleNameIn(List<String> roleNames);
    Optional<Role> findRoleById(Long roleId); // Add "Id" after "By"
    Optional<Role> findRoleByRoleName(Role.RoleType roleName);
}
