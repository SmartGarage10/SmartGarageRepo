package com.example.demo.DTO.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateDTO(
        @NotNull
        @Size(min = 5, max = 50, message = "The name should be between 5 and 50 symbols")
        private String name,


) {
}
