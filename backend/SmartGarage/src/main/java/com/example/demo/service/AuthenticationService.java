package com.example.demo.service;

import com.example.demo.DTO.user.UserLoginDTO;
import com.example.demo.DTO.user.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;


public interface AuthenticationService {
    UserResponseDTO authenticate(UserLoginDTO user, HttpServletRequest request);
}
