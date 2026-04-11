package com.example.demo.DTO.visit;

import com.example.demo.DTO.user.UserResponseDTO;
import com.example.demo.DTO.vehicle.VehicleResponseDTO;
import com.example.demo.DTO.visitItem.VisitItemResponseDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VisitResponseDTO(
        Long id,
        VehicleResponseDTO vehicle,
        UserResponseDTO employee,
        List<VisitItemResponseDTO> visitItems,
        BigDecimal amount,
        LocalDateTime visitDate,
        String status,
        String currency
) {
}
