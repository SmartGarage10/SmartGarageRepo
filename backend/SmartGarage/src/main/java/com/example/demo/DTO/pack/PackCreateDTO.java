package com.example.demo.DTO.pack;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PackCreateDTO(
        @NotBlank(message = "Pack name is mandatory.")
        @Size(min = 2, max = 50, message = "Pack name must be between 2 and 50 characters.")
        String packName,

        @NotBlank(message = "Description is mandatory.")
        @Size(min = 5, max = 1024, message = "Description must be between 5 and 1024 characters.")
        String description,

        @NotNull(message = "Amount is mandatory.")
        @PositiveOrZero(message = "Amount must be zero or positive.")
        BigDecimal amount,

        @NotNull(message = "Service IDs list cannot be null.")
        List<Long> serviceIds
) {
}
