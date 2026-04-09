package com.example.demo.DTO.serviceItem;

import lombok.Builder;

@Builder
public record ServiceItemResponseDTO(
        Long id,
        String serviceName,
        String serviceDescription,
        Double price
) {
}
