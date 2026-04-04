package com.example.demo.DTO.role;

import lombok.Builder;

@Builder
public record RoleResponseDTO(
        Long id,
        String roleName
) {
}
