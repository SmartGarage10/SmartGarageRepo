package com.example.demo.DTO.visit;

import com.example.demo.DTO.visitItem.VisitItemCreateDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VisitCreateDTO(
        @NotNull(message = "Vehicle ID is mandatory.")
        Long vehicleId,

        Long employeeId,

        @NotNull(message = "Visit date is mandatory.")
        LocalDateTime visitDate,

        @NotNull(message = "Visit items cannot be null.")
        @Size(min = 1, message = "At least one visit item is required.")
        List<VisitItemCreateDTO> visitItems,

        @NotNull
        String status,

        @NotNull
        String currency
) {
}
