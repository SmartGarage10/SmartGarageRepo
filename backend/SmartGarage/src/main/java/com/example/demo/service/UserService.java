package com.example.demo.service;

import com.example.demo.DTO.Filter;
import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    RegistrationResponse register(User user, User request);
    Optional<User> authenticate(User user, HttpServletRequest request);

    User updateUser(User user, int userId, User userDetails);
    void changePassword(User user, String oldPassword, String newPassword);
    List<User> getAllUsers();

    Optional<User> getUserById(int userId);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByPhone(String phone);

    List<User> getAllUsers(List<Filter> filters);
    void deleteUser(User user, int userId);
}
