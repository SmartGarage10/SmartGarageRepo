package com.example.demo.filter;

import com.example.demo.DTO.Filter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VisitSpecification extends BaseSpecifications {

    @Override
    public <T> Specification<T> createSpecification(List<Filter> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (Filter filter : filters) {
                if (filter == null) continue;

                // -----------------------------------------
                // CLIENT FILTER
                // -----------------------------------------
                if ("client".equals(filter.getId())) {
                    Join<?, ?> vehicleJoin = root.join("vehicle", JoinType.LEFT);
                    Join<?, ?> clientJoin = vehicleJoin.join("client", JoinType.LEFT);

                    predicates.add(buildPredicate(
                            new Filter(
                                    "name",  // target client's name
                                    filter.getValue(),
                                    filter.getVariant(),
                                    filter.getOperator(),
                                    filter.getFilterId()
                            ),
                            clientJoin,
                            cb
                    ));
                }
                // -----------------------------------------
                // BRAND FILTER
                // -----------------------------------------
                else if ("brand".equals(filter.getId())) {
                    Join<?, ?> vehicleJoin = root.join("vehicle", JoinType.LEFT);

                    predicates.add(buildPredicate(
                            new Filter(
                                    "brand",  // target vehicle's brand
                                    filter.getValue(),
                                    filter.getVariant(),
                                    filter.getOperator(),
                                    filter.getFilterId()
                            ),
                            vehicleJoin,
                            cb
                    ));
                }
                // -----------------------------------------
                // EMPLOYEE FILTER
                // -----------------------------------------
                else if ("employee".equals(filter.getId())) {
                    Join<?, ?> employeeJoin = root.join("employee", JoinType.LEFT);

                    predicates.add(buildPredicate(
                            new Filter(
                                    "name", // filter by employee's name
                                    filter.getValue(),
                                    filter.getVariant(),
                                    filter.getOperator(),
                                    filter.getFilterId()
                            ),
                            employeeJoin,
                            cb
                    ));
                }
                // -----------------------------------------
                // DEFAULT FILTER (Visit fields)
                // -----------------------------------------
                else {
                    predicates.add(buildPredicate(filter, root, cb));
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
