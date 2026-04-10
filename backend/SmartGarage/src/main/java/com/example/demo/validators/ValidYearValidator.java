package com.example.demo.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidYearValidator implements ConstraintValidator<ValidYear, String> {

    private int minYear;

    @Override
    public void initialize(ValidYear constraintAnnotation) {
        this.minYear = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false; // ако year е задължително
        }

        // трябва да е точно 4 цифри
        if (!value.matches("^[0-9]{4}$")) {
            return false;
        }

        int year = Integer.parseInt(value);
        int currentYear = java.time.Year.now().getValue();

        return year >= minYear && year <= currentYear;
    }
}
