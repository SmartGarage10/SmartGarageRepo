package com.example.demo.repositories;

import com.example.demo.models.User;
import com.example.demo.models.Visit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u LEFT JOIN Vehicle v ON u.id = v.client.id LEFT JOIN Visit vis ON v.id = vis.vehicle.id WHERE u.role.roleId = 1")
    List<User> findAllCustomersWithVehiclesAndVisits(Specification<User> spec, Sort sort);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
