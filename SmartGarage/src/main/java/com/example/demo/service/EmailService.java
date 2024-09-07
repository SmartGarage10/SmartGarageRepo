package com.example.demo.service;

public interface EmailService {
    void sendRegistrationEmail(String fromEmail, String toEmail, String subject, String body);
}
