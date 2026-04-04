package com.example.demo.DTO.visitItem;

import com.example.demo.validators.OneOfServiceOrPack;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@OneOfServiceOrPack
public record VisitItemCreateDTO(
        Long serviceItemId,

        Long packId,

        @NotNull(message = "Quantity is mandatory.")
        @Positive(message = "Quantity must be positive.")
        Integer quantity,

        @PositiveOrZero(message = "Price must be zero or positive.")
        BigDecimal price
) {
}
