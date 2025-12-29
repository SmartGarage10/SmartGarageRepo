package com.example.demo.service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.models.User;
import com.example.demo.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    RegistrationResponse register(User user, UserDTO request);

    List<User> getAllUsers(MultiValueMap<String, String> allParams);
    Optional<User> getUserById(Long userId);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);

//    List<User> searchByName(String name);

    User updateUser(User user, Long userId, UserDTO userDTO);

    void changePassword(User user, String oldPassword, String newPassword);

    void deleteUser(User user, Long userId);
    void deleteUsers(User user, List<Long> ids);
}
