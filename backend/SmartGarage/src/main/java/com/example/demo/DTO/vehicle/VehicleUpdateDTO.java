package com.example.demo.DTO.vehicle;

import com.example.demo.validators.ValidYear;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Year;

@Builder
public record VehicleUpdateDTO(
        @Pattern(
                regexp = "^[A-Z]{1,2}\\d{4}[A-Z]{1,2}$",
                message = "Invalid Bulgarian license plate format."
        )
        String vehiclePlate,

        @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters long.")
        String vin,

        @ValidYear(min = 1950, message = "Year must be 1950 or newer.")
        Year year,

        @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters.")
        String model,

        @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters.")
        String brand,

        Long userId
) {
}
