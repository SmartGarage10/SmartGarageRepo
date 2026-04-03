package com.example.demo.DTO.user;

import com.example.demo.DTO.RoleDTO;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.checkerframework.common.aliasing.qual.Unique;

@Builder
public record UserCreateDTO(
        @NotNull
        @Size(min = 5, max = 50, message = "The name should be between 5 and 50 symbols")
        String name,

        @NotNull
        @Size(min = 2, max = 20, message = "Username should be between 2 and 20 symbols")
        String username,

        @Unique
        @Email(message = "Email should be valid")
        @NotEmpty(message = "Email cannot be empty")
        String email,

        @Unique
        @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
        String phone,

        @NotNull
        @Size(min = 10, max = 40, message = "Address should be between 10 and 40 symbols")
        String address,

        @NotNull
        RoleDTO role
) {
}
