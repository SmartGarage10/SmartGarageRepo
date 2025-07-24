package com.example.demo.filter;

import com.example.demo.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserSpecifications {
    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
    }
    public static Specification<User> hasRole(String rolename) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.join("role").get("roleName")),
                    rolename.toUpperCase()
            );
        };
    }
}
