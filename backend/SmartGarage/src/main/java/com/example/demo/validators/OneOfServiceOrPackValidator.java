package com.example.demo.validators;

import com.example.demo.DTO.visitItem.VisitItemCreateDTO;
import com.example.demo.DTO.visitItem.VisitItemUpdateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneOfServiceOrPackValidator implements ConstraintValidator<OneOfServiceOrPack, Object> {
    private boolean allowNull;

    @Override
    public void initialize(OneOfServiceOrPack constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        Long serviceItemId = null;
        Long packId = null;

        if (value instanceof VisitItemCreateDTO dto) {
            serviceItemId = dto.serviceItemId();
            packId = dto.packId();
        } else if (value instanceof VisitItemUpdateDTO dto) {
            serviceItemId = dto.serviceItemId();
            packId = dto.packId();
        }

        // UpdateDTO allows both null (no change)
        if (allowNull && serviceItemId == null && packId == null) {
            return true;
        }

        // Exactly one must be provided
        return (serviceItemId != null) ^ (packId != null);
    }
}
