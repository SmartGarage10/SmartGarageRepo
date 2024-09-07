package com.example.demo.filter;

import com.example.demo.models.ServiceItem;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class ServiceSpecifications {

    //filter by name
    public static Specification<ServiceItem> hasName(String name){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    //filter by min price
    public static Specification<ServiceItem> hasMinPrice(Double minPrice){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice));
    }

    //filter by max price
    public static Specification<ServiceItem> hasMaxPrice(Double maxPrice){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
    }

    //filter between different prices
    public static Specification<ServiceItem> hasPriceInRange(Double minPrice, Double maxPrice){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"),minPrice, maxPrice));
    }

}
