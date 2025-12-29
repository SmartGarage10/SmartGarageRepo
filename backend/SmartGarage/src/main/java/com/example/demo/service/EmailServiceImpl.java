package com.example.demo.service;

import com.example.demo.exceptions.RequestValidationException;
import com.example.demo.exceptions.ResourceNotFoundException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendRegistrationEmail(String to, String username, String tempPass) {
        if (to == null || to.isBlank()) {
            throw new RequestValidationException("Recipient email cannot be null or empty");
        }

        // Load template
        String content = loadTemplate("email-templates/registration-email.txt", username, tempPass);

        // Split subject and body
        String[] parts = content.split("Body:", 2);
        if (parts.length < 2) {
            throw new RuntimeException("Template missing 'Body:' section");
        }
        String subject = parts[0].replace("Subject:", "").trim();
        String body = parts[1].trim();

        // Prepare and send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
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
        message.setFrom(fromEmail);

        mailSender.send(message);
    }

    // Helper method to load template and replace placeholders
    private String loadTemplate(String path, String username, String tempPass) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            content = content.replace("{{username}}", username)
                    .replace("{{password}}", tempPass);
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + path, e);
        }
    }
}

