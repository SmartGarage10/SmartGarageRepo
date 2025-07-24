package com.example.demo.filter;

import com.example.demo.models.Visit;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VisitSpecification {

    //filter by chosen date
    public static Specification<Visit> byDate(LocalDateTime visitDate){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("visitdate"), visitDate));

    }

    //filter between dates
    public static Specification<Visit> betweenDates(LocalDateTime startDate, LocalDateTime endDate){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("visitdate"), startDate, endDate);
    }

    public static Specification<Visit> byVehicleId(int vehicleId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("vehicle").get("id"), vehicleId);
    }

}
