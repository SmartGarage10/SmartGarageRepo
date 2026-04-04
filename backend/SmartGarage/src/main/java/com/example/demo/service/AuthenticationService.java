package com.example.demo.service;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.user.UserLoginDTO;
import com.example.demo.DTO.user.UserResponseDTO;
import com.example.demo.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface AuthenticationService {
    UserResponseDTO authenticate(UserLoginDTO user, HttpServletRequest request);
}
