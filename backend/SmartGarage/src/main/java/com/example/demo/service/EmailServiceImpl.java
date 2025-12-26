package com.example.demo.service;

import com.example.demo.exceptions.RequestValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendRegistrationEmail(String fromEmail, String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) {
            throw new RequestValidationException("Recipient email cannot be null or empty");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(fromEmail);

        mailSender.send(message);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new RequestValidationException("Recipient email cannot be null or empty");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("emanuilpavlov2002@gmail.com");

        mailSender.send(message);
    }
}
