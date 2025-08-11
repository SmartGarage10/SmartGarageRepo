package com.example.demo.filter;

import com.example.demo.DTO.Filter;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleSpecifications extends BaseSpecifications{

    @Override
    public <T> Specification<T> createSpecification(List<Filter> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (Filter filter : filters) {
                if (filter == null) continue;

                if ("client".equals(filter.getId())) {
                    Join<Vehicle, User> clientJoin = root.join("client", JoinType.LEFT);
                    predicates.add(buildPredicate(
                            new Filter(
                                    "name",  // Change the filter to target the name field
                                    filter.getValue(),
                                    filter.getVariant(),
                                    filter.getOperator(),
                                    filter.getFilterId()
                            ),
                            clientJoin,  // Apply to the joined User entity
                            cb
                    ));
                } else if ("year".equals(filter.getId())) {
                    if (filter.getValue() instanceof List<?> yearStrings && !yearStrings.isEmpty()) {
                        List<Year> yearList = yearStrings.stream()
                                .map(Object::toString)
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(Year::parse)
                                .toList();

                        if (!yearList.isEmpty()) {
                            // Create a new filter with the parsed Year objects
                            predicates.add(buildPredicate(
                                    new Filter(
                                            filter.getId(), // "year"
                                            yearList,
                                            filter.getVariant(),
                                            filter.getOperator(),
                                            filter.getFilterId()
                                    ), root, cb));
                        }
                    }
                } else {
                    predicates.add(buildPredicate(filter, root, cb));
                }
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
