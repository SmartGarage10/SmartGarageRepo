package com.example.demo.DTO.vehicle;

import com.example.demo.validators.ValidYear;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Year;

@Builder
public record VehicleCreateDTO(
        @Pattern(
                regexp = "^[A-Z]{1,2}\\d{4}[A-Z]{1,2}$",
                message = "Invalid Bulgarian license plate format."
        )
        @NotBlank(message = "License plate is mandatory.")
        String vehiclePlate,

        @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters long.")
        @NotBlank(message = "VIN is mandatory.")
        String vin,

        @NotNull(message = "Year of creation is mandatory.")
        @ValidYear(min = 1950, message = "Year must be 1950 or newer.")
        Year yearOfCreation,

        @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters.")
        @NotBlank(message = "Model is mandatory.")
        String model,

        @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters.")
        @NotBlank(message = "Brand is mandatory.")
        String brand,

        @NotNull(message = "User ID is mandatory.")
        Long userId
) {
}
