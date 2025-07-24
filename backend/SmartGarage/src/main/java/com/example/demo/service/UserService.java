package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    AuthenticationResponse register(User user, User request);
    AuthenticationResponse authenticate(User user);

    User updateUser(User user, int userId, User userDetails);
    void changePassword(User user, String oldPassword, String newPassword);

    Optional<User> getUserById(int userId);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByPhone(String phone);

    List<User> getAllUsers(String username, String email, String phone, String roleName, String sortField, String sortDirection);
    void deleteUser(User user, int userId);
}
