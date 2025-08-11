package com.example.demo.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

public class ValidYearValidator implements ConstraintValidator<ValidYear, Year> {
    private int minYear;

    @Override
    public void initialize(ValidYear constraintAnnotation) {
        this.minYear = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Year value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int yearValue = value.getValue();
        return yearValue >= minYear;
    }
}