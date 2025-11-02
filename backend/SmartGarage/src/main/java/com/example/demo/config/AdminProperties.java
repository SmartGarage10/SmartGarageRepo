package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record AdminProperties(@Value("${app.admin.username}") String username,
                              @Value("${app.admin.password}") String password,
                              @Value("${app.admin.email}") String email,
                              @Value("${app.admin.name:Admin}") String name,
                              @Value("${app.admin.address:Admin HQ}") String address,
                              @Value("${app.admin.phone:0000000000}") String phone) {
}
