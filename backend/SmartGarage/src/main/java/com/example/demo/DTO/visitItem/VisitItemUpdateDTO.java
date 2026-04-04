package com.example.demo.DTO.visitItem;

import com.example.demo.validators.OneOfServiceOrPack;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@OneOfServiceOrPack(allowNull = true)
public record VisitItemUpdateDTO(
        Long serviceItemId,

        Long packId,

        @Positive(message = "Quantity must be positive.")
        Integer quantity,

        @PositiveOrZero(message = "Price must be zero or positive.")
        BigDecimal price
) {
}
