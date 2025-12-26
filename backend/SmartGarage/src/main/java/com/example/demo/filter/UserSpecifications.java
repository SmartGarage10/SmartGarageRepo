package com.example.demo.filter;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserSpecifications extends BaseSpecifications {

    // -------------------------
    // RELATION CONFIG REGISTRY
    // -------------------------
    private static final Map<String, RelationConfig> RELATIONS = Map.of(
            "role", RelationConfig.of(List.of("role"), "roleName") // Join to 'role' and filter on 'roleName'
    );


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

                    // Create the final filter
                    Filter mapped = new Filter(
                            config.targetField(),
                            filter.getValue(),
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