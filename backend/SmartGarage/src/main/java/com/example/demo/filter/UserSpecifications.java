package com.example.demo.filter;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.DTO.Filter;
import com.example.demo.repositories.RoleRepository;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecifications extends BaseSpecifications{

    public Specification<User> createRoleSpecification(List<Role> roles) {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) {
                return cb.conjunction(); // No filtering if no roles
            }

            List<Predicate> predicates = new ArrayList<>();

            for (Role role : roles) {
                if (role != null) {
                    // Use equals or like depending on your requirement
                    // If exact match:
                    predicates.add(cb.equal(root.get("role"), role));
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }

}
