package com.example.demo.filter;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class VehicleSpecifications extends BaseSpecifications {

    private record RelationConfig(List<String> path, String targetField) {}

    // -------------------------
    // RELATION CONFIG REGISTRY
    // -------------------------
    private static final Map<String, RelationConfig> RELATIONS = Map.of(
            "client", new RelationConfig(List.of("client"), "name"),
            "year", new RelationConfig(List.of(), "year") // No join needed for year
    );

    // -------------------------
    // YEAR TRANSFORMER (keep this separate since it's value transformation, not relation)
    // -------------------------
    private static Object convertYearList(Object raw) {
        if (raw instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Year::parse)
                    .toList();
        }
        return raw;
    }

    // -------------------------
    // MAIN SPECIFICATION
    // -------------------------
    @Override
    public <T> Specification<T> createSpecification(List<Filter> filters) {
        return (root, query, cb) -> {

            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (Filter filter : filters) {
                if (filter == null) continue;

                RelationConfig config = RELATIONS.get(filter.getId());

                if (config != null) {
                    // Perform dynamic join chain
                    From<?, ?> path = root;
                    for (String joinName : config.path()) {
                        path = path.join(joinName, JoinType.LEFT);
                    }

                    // Apply value transformation for year field
                    Object transformedValue = filter.getValue();
                    if ("year".equals(filter.getId())) {
                        transformedValue = convertYearList(filter.getValue());
                    }

                    // Create the final filter
                    Filter mapped = new Filter(
                            config.targetField(),
                            transformedValue,
                            filter.getVariant(),
                            filter.getOperator(),
                            filter.getFilterId()
                    );

                    predicates.add(buildPredicate(mapped, path, cb));

                } else {
                    // Default (no special rule)
                    predicates.add(buildPredicate(filter, root, cb));
                }
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}