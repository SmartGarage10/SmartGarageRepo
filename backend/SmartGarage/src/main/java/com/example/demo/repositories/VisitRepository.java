package com.example.demo.repositories;

import com.example.demo.models.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Integer>, JpaSpecificationExecutor<Visit> {
    List<Visit> findByVehicleId(int vehicleId);
    List<Visit> findByEmployeeId(int employeeId);
    List<Visit> findByVisitDate(LocalDateTime dateTime);
}
