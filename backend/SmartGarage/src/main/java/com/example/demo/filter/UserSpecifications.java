package com.example.demo.filter;

import com.example.demo.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.example.demo.DTO.Filter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecifications {

    public Specification<User> createSpecification(List<Filter> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters != null) {
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
