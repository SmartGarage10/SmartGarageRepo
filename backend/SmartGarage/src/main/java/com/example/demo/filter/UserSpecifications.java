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
public class UserSpecifications {

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

    public Specification<User> createSearchSpecification(String searchFilter) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), searchFilter.toLowerCase() + "%");
    }

    public Specification<User> createSpecification(List<Filter> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!filters.isEmpty()) {
                for (Filter filter : filters) {
                    predicates.add(buildPredicate(filter, root, cb));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate buildPredicate(Filter filter, Root<User> root, CriteriaBuilder cb) {
        String field = filter.getId();
        String value = filter.getValue();
        String operator = filter.getOperator().toLowerCase();

        Path<String> fieldPath = root.get(field);

        switch (operator) {
            case "ilike":
                return cb.like(cb.lower(fieldPath), "%" + value.toLowerCase() + "%");
            case "notilike":
                return cb.notLike(cb.lower(fieldPath), "%" + value.toLowerCase() + "%");
            case "eq":
                return cb.equal(fieldPath, value);
            case "ne":
                return cb.notEqual(fieldPath, value);
            case "isempty":
                return cb.or(
                        cb.isNull(fieldPath),
                        cb.equal(fieldPath, ""),
                        cb.equal(fieldPath, " ")
                );
            case "isnotempty":
                return cb.and(
                        cb.isNotNull(fieldPath),
                        cb.notEqual(fieldPath, ""),
                        cb.notEqual(fieldPath, " ")
                );
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }
}
