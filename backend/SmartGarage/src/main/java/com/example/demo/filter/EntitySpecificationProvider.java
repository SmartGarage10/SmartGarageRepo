package com.example.demo.filter;

import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public interface EntitySpecificationProvider<T> {
    Specification<T> createFilterSpecification(List<Filter> filters);
    Specification<T> createSearchSpecification(String search);
    String getSearchField();
}