package com.example.demo.DTO.vehicle;

import com.example.demo.DTO.user.UserResponseDTO;
import lombok.Builder;

@Builder
public record VehicleResponseDTO(
        Long id,
        String vehiclePlate,
        String vin,
        String year,
        String model,
        String brand,
        UserResponseDTO client
) {
}