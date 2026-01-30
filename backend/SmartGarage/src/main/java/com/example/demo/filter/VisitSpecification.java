package com.example.demo.filter;

import com.example.demo.models.Visit;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class VisitSpecification extends BaseSpecifications {
    // Transformer method for visitDate
    private static Object transformVisitDate(Object value) {
        if (value instanceof String) {
            try {
                long timestamp = Long.parseLong((String) value);
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            } catch (NumberFormatException e) {
                return value;
            }
        } else if (value instanceof List) {
            List<LocalDateTime> converted = new ArrayList<>();
            for (Object item : (List<?>) value) {
                if (item instanceof String) {
                    try {
                        long timestamp = Long.parseLong((String) item);
                        converted.add(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
                    } catch (NumberFormatException e) {
                        // Skip invalid timestamps
                    }
                }
            }
            return converted;
        }
        return value;
    }

    // Central config for all JOIN-based filters
    private static final Map<String, RelationConfig> RELATIONS = Map.of(
            "client", RelationConfig.of(List.of("vehicle", "client"), "name"),
            "brand", RelationConfig.of(List.of("vehicle"), "brand"),
            "employee", RelationConfig.of(List.of("employee"), "name"),
            "pack", RelationConfig.of(List.of("visitItems", "pack"), "packName"),
            "visitDate", RelationConfig.of(List.of(), "visitDate", VisitSpecification::transformVisitDate)
    );
    @Override
    public <T> Specification<T> createSearchSpecification(String search, String fieldName) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return cb.conjunction();
            }
            String searchPattern = search.toLowerCase() + "%";
            try {
                return cb.like(
                        cb.lower(root.get("vehicle").get("client").get("name")),
                        searchPattern
                );
            } catch (Exception e) {
                return cb.conjunction();
            }
        };
    }

    @Override
    public <T> Specification<T> createSpecification(List<Filter> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (Filter filter : filters) {
                if (filter == null) continue;

                System.out.println("=== PROCESSING FILTER: " + filter.getId() + " ===");
                System.out.println("Original value: " + filter.getValue() + " (type: " +
                        (filter.getValue() != null ? filter.getValue().getClass().getSimpleName() : "null") + ")");

                RelationConfig config = RELATIONS.get(filter.getId());

                if (config != null) {
                    // APPLY THE TRANSFORMER HERE
                    Object valueToUse = filter.getValue();
                    if (config.transformer() != null) {
                        valueToUse = config.transformer().apply(filter.getValue());
                        System.out.println("Transformed value: " + valueToUse + " (type: " +
                                (valueToUse != null ? valueToUse.getClass().getSimpleName() : "null") + ")");
                    } else {
                        System.out.println("No transformer applied");
                    }
                    // SPECIAL HANDLING FOR CUSTOM PACK FILTER - FIXED
                    if ("pack".equals(filter.getId()) && filter.getValue() != null &&
                            "CUSTOM PACK".equalsIgnoreCase(filter.getValue().toString())) {
                        System.out.println("=== CUSTOM PACK FILTER ACTIVATED ===");

                        // For custom packs, we want visits where ALL visitItems have pack = null
                        // OR the visit has no pack items at all
                        Join<Object, Object> visitItemsJoin = root.join("visitItems", JoinType.LEFT);
                        Predicate customPackPredicate = cb.isNull(visitItemsJoin.get("pack"));
                        predicates.add(customPackPredicate);

                        // Ensure the visit has at least one item
                        Predicate hasItems = cb.isNotEmpty(root.get("visitItems"));
                        predicates.add(hasItems);

                        System.out.println("=== CUSTOM PACK FILTER COMPLETE ===\n");
                        continue; // Skip normal processing for this filter
                    }

                    // SPECIAL HANDLING FOR DATE EQUALITY - CONVERT TO DATE RANGE
                    if ("visitDate".equals(config.targetField()) && "eq".equals(filter.getOperator())) {
                        if (valueToUse instanceof LocalDateTime dateTime) {
                            System.out.println("=== DATE RANGE FILTER ACTIVATED ===");

                            // Create date range for the entire day
                            LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
                            LocalDateTime endOfDay = dateTime.toLocalDate().atTime(23, 59, 59, 999999999);

                            System.out.println("Date range: " + startOfDay + " to " + endOfDay);

                            // Create between predicate for the date range
                            Predicate dateRangePredicate = cb.between(
                                    root.get("visitDate"),
                                    startOfDay,
                                    endOfDay
                            );
                            predicates.add(dateRangePredicate);

                            System.out.println("=== DATE RANGE FILTER COMPLETE ===\n");
                            continue; // Skip normal predicate building for this filter
                        }
                    }

                    // Create the transformed filter for normal cases
                    Filter transformedFilter = new Filter(
                            config.targetField(),
                            valueToUse,
                            filter.getVariant(),
                            filter.getOperator(),
                            filter.getFilterId()
                    );

                    System.out.println("Final filter - Field: " + transformedFilter.getId() +
                            ", Value: " + transformedFilter.getValue() +
                            " (type: " + (transformedFilter.getValue() != null ?
                            transformedFilter.getValue().getClass().getSimpleName() : "null") + ")");

                    // Build join path if needed
                    if (!config.path().isEmpty()) {
                        Join<?, ?> join = null;
                        for (String path : config.path()) {
                            join = (join == null) ? root.join(path, JoinType.LEFT) : join.join(path, JoinType.LEFT);
                        }
                        predicates.add(buildPredicate(transformedFilter, join, cb));
                    } else {
                        predicates.add(buildPredicate(transformedFilter, root, cb));
                    }
                } else {
                    System.out.println("No config found for filter: " + filter.getId());
                    predicates.add(buildPredicate(filter, root, cb));
                }
                System.out.println("=== FILTER PROCESSING COMPLETE ===\n");
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}