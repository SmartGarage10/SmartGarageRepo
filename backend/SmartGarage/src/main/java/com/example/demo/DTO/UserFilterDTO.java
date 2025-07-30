package com.example.demo.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;

@Data
public class UserFilterDTO {
    @NotNull
    @Size(min = 5, max = 50, message = "The name should be between 5 and 50 symbols")
    private String name;

    @NotNull
    @Size(min = 2, max = 20, message = "Username should be between 2 and 20 symbols")
    private String username;

    @Unique
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Unique
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotNull
    @Size(min = 10, max = 40, message = "Address should be between 10 and 40 symbols")
    private String address;
}
