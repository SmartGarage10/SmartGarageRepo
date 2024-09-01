package com.example.demo.filter;

import com.example.demo.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserSpecifications {
    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
    }

    public static Specification<User> hasVehicleModelOrMake(String model, String make) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.join("vehicles").get("model")), "%" + model.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.join("vehicles").get("make")), "%" + make.toLowerCase() + "%")
            );
        };
    }

    public static Specification<User> visitsInRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.between(root.join("vehicles").join("visits").get("date"), startDate, endDate);
        };
    }
}
