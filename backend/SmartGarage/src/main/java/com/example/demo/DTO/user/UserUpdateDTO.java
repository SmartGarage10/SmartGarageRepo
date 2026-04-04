package com.example.demo.DTO.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserUpdateDTO(
        @Size(min = 5, max = 50, message = "The name should be between 5 and 50 symbols")
        String name,

        @Size(min = 2, max = 20, message = "Username should be between 2 and 20 symbols")
        String username,

        @Email(message = "Email should be valid")
        String email,

        @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
        String phone,

        @Size(min = 10, max = 40, message = "Address should be between 10 and 40 symbols")
        String address,

        String role

) {
}
