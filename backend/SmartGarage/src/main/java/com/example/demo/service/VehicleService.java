package com.example.demo.service;

import com.example.demo.DTO.vehicle.VehicleCreateDTO;
import com.example.demo.DTO.vehicle.VehicleResponseDTO;
import com.example.demo.DTO.vehicle.VehicleUpdateDTO;
import com.example.demo.models.User;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<VehicleResponseDTO> getAllVehicles(MultiValueMap<String, String> allParams);
    Optional<VehicleResponseDTO> getVehicleById(Long vehicleId);

    VehicleResponseDTO createNewVehicle(VehicleCreateDTO vehicleDTO);
    VehicleResponseDTO update(Long vehicleId, VehicleUpdateDTO vehicleDTO);

    void deleteVehicle(User user, Long vehicleId);
    void deleteVehicles(User user, List<Long> ids);
}
