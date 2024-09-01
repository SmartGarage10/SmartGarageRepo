package com.example.demo.repositories;

import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByVin(String vin);
    Optional<Vehicle> findByClient(User user);
    Optional<Vehicle> findByVehiclePlate(String vehiclePLate);
    List<Vehicle> findAll(Specification<Vehicle> spec, Sort sort);
    boolean existsByVehiclePlate(String vehiclePlate);
    boolean existsByVin(String vin);

}
