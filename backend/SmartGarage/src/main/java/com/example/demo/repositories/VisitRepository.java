package com.example.demo.repositories;

import com.example.demo.models.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    List<Visit> findByVehicle_Id(Long vehicleId);
    List<Visit> findByEmployeeId(Long employeeId);
    List<Visit> findByVisitDate(LocalDateTime dateTime);

    boolean existsByVisitDate(LocalDateTime dateTime);
}
