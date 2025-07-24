package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EditVehicleDTO {
    @JsonProperty("vehiclePlate")
    @Pattern(regexp = "^[A-Z]{1,2}\\d{4}[A-Z]{1,2}$", message = "Invalid Bulgarian license plate format.")
    @NotBlank(message = "License plate is mandatory.")
    private String licensePlate;

    @JsonProperty("username")
    @NotBlank(message = "Username is mandatory.")
    private String username;
}
