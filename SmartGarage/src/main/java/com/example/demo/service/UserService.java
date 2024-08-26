package com.example.demo.service;

import com.example.demo.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User saveUser(User user);

    User updateUser(int userId, User userDetails);
    public void changePassword(int userId, String oldPassword, String newPassword);

    Optional<User> getUserById(int userId);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByPhone(String phone);

    List<User> getAllUsers();

    void deleteUser(int userId);
}
