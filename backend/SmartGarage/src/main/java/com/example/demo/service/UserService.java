package com.example.demo.service;

import com.example.demo.DTO.Filter;
import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> authenticate(User user, HttpServletRequest request);
    RegistrationResponse register(User user, User request);

    List<User> getAllUsers(MultiValueMap<String, String> allParams);
    Optional<User> getUserById(int userId);
    Optional<User> getUserByUsername(String username);

//    List<User> searchByName(String name);

    User updateUser(User user, int userId, User userDetails);

    void changePassword(User user, String oldPassword, String newPassword);

    void deleteUser(User user, int userId);
    void deleteUsers(User user, List<Integer> ids);
}
