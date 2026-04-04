package com.example.demo.DTO.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RoleCreateDTO(
        @NotBlank(message = "Role name cannot be empty")
        @Size(min = 3, max = 30, message = "Role name must be between 3 and 30 characters")
        String roleName
) {
}
