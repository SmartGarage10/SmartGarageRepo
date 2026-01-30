package com.example.demo.DTO;

import com.example.demo.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
public class VehicleDTO {

    @JsonProperty("vehiclePlate")
    @Pattern(regexp = "^[A-Z]{1,2}\\d{4}[A-Z]{1,2}$",
            message = "Invalid Bulgarian license plate format.")
    @NotBlank(message = "License plate is mandatory.")
    private String vehiclePlate;

    @JsonProperty("vin")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters long.")
    @NotBlank(message = "VIN is mandatory.")
    private String vin;

    @JsonProperty("year")
    private Year yearOfCreation;

    @JsonProperty("model")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters.")
    @NotBlank(message = "Model is mandatory.")
    private String model;

    @JsonProperty("brand")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters.")
    @NotBlank(message = "Brand is mandatory.")
    private String brand;

    @JsonProperty("user")
    @NotNull(message = "User is mandatory.")
    private User user;
}

