package com.example.demo.helpers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public class ValidationHelper {

    public static ResponseEntity<Map<String, String>> validate(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return null;
        }

        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 + "; " + msg2
                ));

        return ResponseEntity.badRequest().body(errors);
    }
}
