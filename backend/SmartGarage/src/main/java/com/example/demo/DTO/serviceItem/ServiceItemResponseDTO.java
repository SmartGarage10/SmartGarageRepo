package com.example.demo.DTO.serviceItem;

import lombok.Builder;

@Builder
public record ServiceItemResponseDTO(
        Long id,
        String name,
        String description,
        Double price
) {
}
