package com.example.demo.DTO.user;

import jakarta.validation.constraints.NotEmpty;

public record UserLoginDTO(
        @NotEmpty(message = "Email can't be empty")
        String email,

        @NotEmpty(message = "Password can't be empty")
        String password
) {
}
