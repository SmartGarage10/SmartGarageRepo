package com.example.demo.DTO.visit;

import com.example.demo.DTO.visitItem.VisitItemUpdateDTO;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VisitUpdateDTO(
        Long vehicleId,

        Long employeeId,

        @Size(min = 1, message = "At least one visit item is required.")
        List<VisitItemUpdateDTO> visitItems,

        LocalDateTime visitDate,

        String status
) {
}
