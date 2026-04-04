package com.example.demo.DTO.user;

import com.example.demo.DTO.role.RoleResponseDTO;
import lombok.Builder;

@Builder
public record UserResponseDTO(
        Long id,
        String name,
        String username,
        String email,
        String phone,
        String address,
        RoleResponseDTO role
) {
}
