package com.example.demo.filter;

import com.example.demo.DTO.Filter;
import com.example.demo.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BaseSpecifications {
    public <T> Specification<T> createSearchSpecification(String searchFilter, String type) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(type)), searchFilter.toLowerCase() + "%");
    }

    public <T> Specification<T> createSpecification(List<Filter> filters) {
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

    protected <T> Predicate buildPredicate(Filter filter, Path<T> root, CriteriaBuilder cb) {
        String field = filter.getId();
        Object rawValue = filter.getValue(); // can be String or List
        String operator = filter.getOperator().toLowerCase();

        Path<?> fieldPath = root.get(field);
        Class<?> fieldType = fieldPath.getJavaType();

        // Normalize string value
        String stringValue = rawValue != null ? rawValue.toString() : "";

        // Normalize arrayValue for in/notin
        List<Object> valuesList;
        if (rawValue instanceof List) {
            valuesList = ((List<?>) rawValue).stream()
                    .map(v -> convertToType(v.toString(), fieldType))
                    .toList();  // Java 16+ toList(), otherwise use collect(Collectors.toList())
        } else {
            valuesList = Arrays.stream(stringValue.split(","))
                    .map(v -> convertToType(v, fieldType))
                    .toList();
        }

        switch (operator) {
            case "ilike":
                return cb.like(cb.lower(fieldPath.as(String.class)), "%" + stringValue.toLowerCase() + "%");
            case "notilike":
                return cb.notLike(cb.lower(fieldPath.as(String.class)), "%" + stringValue.toLowerCase() + "%");
            case "eq":
                return cb.equal(fieldPath, stringValue);
            case "ne":
                return cb.notEqual(fieldPath, stringValue);
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
            case "inarray":
                return fieldPath.in(valuesList);
            case "notinarray":
                return fieldPath.in(valuesList).not();
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    private Object convertToType(String value, Class<?> type) {
        if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(value);
        if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(value);
        if (type.equals(java.time.Year.class)) return java.time.Year.parse(value);
        if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(value);
        return value; // default String
    }

}
