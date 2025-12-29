package com.example.demo.service;

public interface EmailService {
    void sendRegistrationEmail(String toEmail, String subject, String body);
    void sendEmail(String to, String subject, String body);
}
