package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.springframework.util.MultiValueMap;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllVehicles(MultiValueMap<String, String> allParams);
    Optional<Vehicle> getVehicleById(Long vehicleId);

    Vehicle createNewVehicle(User user, Vehicle vehicle);
    Vehicle update(User user, Long vehicleId, Vehicle changes);

    void deleteVehicle(User user, Long vehicleId);
    void deleteVehicles(User user, List<Long> ids);
}
