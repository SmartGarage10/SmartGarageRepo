package com.example.demo.DTO.role;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RoleUpdateDTO(
        @Size(min = 3, max = 30, message = "Role name must be between 3 and 30 characters")
        String roleName
) {
}
