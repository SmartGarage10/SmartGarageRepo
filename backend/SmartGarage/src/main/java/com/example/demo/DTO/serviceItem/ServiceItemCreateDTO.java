package com.example.demo.DTO.serviceItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ServiceItemCreateDTO(
        @NotBlank(message = "Name is mandatory.")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
        String name,

        @NotBlank(message = "Description is mandatory.")
        @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters.")
        String description,

        @NotNull(message = "Price is mandatory.")
        @Positive(message = "Price must be positive.")
        Double price
) {
}
