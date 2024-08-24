package com.example.demo.repositories;

import com.example.demo.models.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Integer> {
    List<Visit> findByVehicleId(int vehicleId);
    List<Visit> findByEmployeeId(int employeeId);
    List<Visit> findByVisitDate(LocalDateTime dateTime);
}
