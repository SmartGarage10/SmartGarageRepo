package com.example.demo.filter;

import com.example.demo.models.Vehicle;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;

public class VehicleSpecifications {

    public static Specification<Vehicle> hasOwnerName(String ownerName) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("client").get("username"), "%" + ownerName.toLowerCase() + "%");
        };
    }

    // Filter based on vehicle model
    public static Specification<Vehicle> hasVehicleModel(String model) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(model)) {
                return criteriaBuilder.conjunction(); // No filtering if model is empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + model.toLowerCase() + "%");
        };
    }

    // Filter based on vehicle brand
    public static Specification<Vehicle> hasVehicleBrand(String brand) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(brand)) {
                return criteriaBuilder.conjunction(); // No filtering if brand is empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
        };
    }

    // Filter based on vehicle year
    public static Specification<Vehicle> hasVehicleYear(Year year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null) {
                return criteriaBuilder.conjunction(); // No filtering if year is null
            }
            return criteriaBuilder.equal(root.get("year"), year);
        };
    }
}
