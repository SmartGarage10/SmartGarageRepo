package com.example.demo.service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface AuthenticationService {
    Optional<User> authenticate(UserDTO user, HttpServletRequest request);
}
