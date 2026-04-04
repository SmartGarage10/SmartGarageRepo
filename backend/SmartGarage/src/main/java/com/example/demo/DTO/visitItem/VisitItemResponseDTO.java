package com.example.demo.DTO.visitItem;

import com.example.demo.DTO.pack.PackResponseDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VisitItemResponseDTO(
        Long id,
        ServiceItemResponseDTO serviceItem,
        PackResponseDTO pack,
        BigDecimal price,
        Integer quantity,
        String itemName,
        String itemType,
        BigDecimal subtotal,
        String displayName
) {
}
