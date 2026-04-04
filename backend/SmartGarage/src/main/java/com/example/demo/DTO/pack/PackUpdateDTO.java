package com.example.demo.DTO.pack;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PackUpdateDTO(
        @Size(min = 2, max = 50, message = "Pack name must be between 2 and 50 characters.")
        String packName,

        @Size(min = 5, max = 1024, message = "Description must be between 5 and 1024 characters.")
        String description,

        @PositiveOrZero(message = "Amount must be zero or positive.")
        BigDecimal amount,

        List<Long> serviceIds
) {
}
