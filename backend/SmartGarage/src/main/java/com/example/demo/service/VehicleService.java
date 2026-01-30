package com.example.demo.service;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllVehicles(MultiValueMap<String, String> allParams);
    Optional<Vehicle> getVehicleById(Long vehicleId);

    Vehicle createNewVehicle(User user, VehicleDTO vehicleDTO);
    Vehicle update(User user, Long vehicleId, VehicleDTO vehicleDTO);

    void deleteVehicle(User user, Long vehicleId);
    void deleteVehicles(User user, List<Long> ids);
}
