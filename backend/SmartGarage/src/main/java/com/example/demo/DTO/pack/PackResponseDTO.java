package com.example.demo.DTO.pack;

import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PackResponseDTO(
        Long id,
        String packName,
        String description,
        BigDecimal amount,
        List<ServiceItemResponseDTO> services,
        double totalPrice
) {
}
