package com.example.demo.filter;

import com.example.demo.models.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecifications {
    public static Specification<Vehicle> hasOwnerName(String ownerName) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);  // Ensure distinct results if there are joins

            // Join the 'client' relationship and fetch the 'name' attribute from the 'User' entity
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("client").get("username")),
                    "%" + ownerName.toLowerCase() + "%"
            );
        };
    }
}
