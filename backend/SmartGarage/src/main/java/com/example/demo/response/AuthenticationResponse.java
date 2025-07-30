package com.example.demo.response;

public class AuthenticationResponse {
    private final String message;

    public AuthenticationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

