package com.example.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RegistrationResponse {
    private String message;
    private String email;
    private LocalDateTime timestamp;
}
