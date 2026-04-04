package com.example.demo.DTO.vehicle;

import com.example.demo.DTO.user.UserResponseDTO;
import lombok.Builder;

import java.time.Year;

@Builder
public record VehicleResponseDTO(
        Long id,
        String vehiclePlate,
        String vin,
        Year yearOfCreation,
        String model,
        String brand,
        UserResponseDTO user
) {
}
