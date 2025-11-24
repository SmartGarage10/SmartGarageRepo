package com.example.demo.filter;

import com.example.demo.DTO.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Object rawValue = filter.getValue(); // This is already transformed by VisitSpecification
        String operator = filter.getOperator().toLowerCase();

        Path<?> fieldPath = root.get(field);
        Class<?> fieldType = fieldPath.getJavaType();

        // DEBUG: See what's coming in
        System.out.println(">>> BaseSpecifications.buildPredicate() - Field: " + field +
                ", Raw Value: " + rawValue +
                ", Raw Value Type: " + (rawValue != null ? rawValue.getClass().getName() : "null") +
                ", Field Type: " + fieldType.getName());

        // SKIP EVERYTHING IF VALUE IS NULL - return a predicate that does nothing
        if (rawValue == null) {
            System.out.println(">>> Skipping predicate - value is null");
            return cb.conjunction(); // This means "true" - no filtering applied
        }

        // Also skip if it's an empty string
        if (rawValue instanceof String && ((String) rawValue).trim().isEmpty()) {
            System.out.println(">>> Skipping predicate - value is empty string");
            return cb.conjunction(); // This means "true" - no filtering applied
        }

        // Use rawValue directly for most operations - it's already the correct type
        switch (operator) {
            case "ilike":
            case "notilike":
                // For LIKE operations, we need strings
                String stringValue = rawValue.toString();
                if (operator.equals("ilike")) {
                    return cb.like(cb.lower(fieldPath.as(String.class)), "%" + stringValue.toLowerCase() + "%");
                } else {
                    return cb.notLike(cb.lower(fieldPath.as(String.class)), "%" + stringValue.toLowerCase() + "%");
                }

            case "eq":
                // Use the rawValue directly - it's already the correct type (LocalDateTime)
                return cb.equal(fieldPath, rawValue);

            case "ne":
                return cb.notEqual(fieldPath, rawValue);

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
            case "notinarray":
                // Handle lists with proper type conversion
                List<Object> valuesList;
                if (rawValue instanceof List) {
                    valuesList = ((List<?>) rawValue).stream()
                            .map(v -> ensureTypeCompatibility(v, fieldType))
                            .toList();
                } else {
                    valuesList = Arrays.stream(rawValue.toString().split(","))
                            .map(v -> ensureTypeCompatibility(v, fieldType))
                            .toList();
                }
                Predicate inPredicate = fieldPath.in(valuesList);
                return operator.equals("inarray") ? inPredicate : inPredicate.not();

            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    /**
     * Ensure value compatibility with field type
     */
    private Object ensureTypeCompatibility(Object value, Class<?> targetType) {
        if (value == null) return null;

        // If types already match, return as-is
        if (targetType.isInstance(value)) {
            return value;
        }

        // Convert string to target type if needed
        if (value instanceof String stringValue) {
            try {
                if (targetType == LocalDateTime.class) {
                    // Handle timestamp strings (your transformer should handle this, but as fallback)
                    try {
                        long timestamp = Long.parseLong(stringValue);
                        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
                    } catch (NumberFormatException e) {
                        // Try parsing as ISO string if needed
                        return LocalDateTime.parse(stringValue);
                    }
                }
                if (targetType.equals(Integer.class) || targetType.equals(int.class))
                    return Integer.valueOf(stringValue);
                if (targetType.equals(Long.class) || targetType.equals(long.class))
                    return Long.valueOf(stringValue);
                if (targetType.equals(java.time.Year.class))
                    return java.time.Year.parse(stringValue);
                if (targetType.equals(Double.class) || targetType.equals(double.class))
                    return Double.valueOf(stringValue);
            } catch (Exception e) {
                System.err.println("Type conversion failed for value '" + value + "' to type " + targetType + ": " + e.getMessage());
            }
        }

        return value; // Return original if conversion fails or not needed
    }
}