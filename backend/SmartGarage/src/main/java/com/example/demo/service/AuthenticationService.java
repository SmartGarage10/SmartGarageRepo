package com.example.demo.service;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface AuthenticationService {
    Optional<User> authenticate(LoginDTO user, HttpServletRequest request);
}
