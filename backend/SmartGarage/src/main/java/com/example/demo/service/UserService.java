package com.example.demo.service;

import com.example.demo.DTO.user.UserCreateDTO;
import com.example.demo.DTO.user.UserResponseDTO;
import com.example.demo.DTO.user.UserUpdateDTO;
import com.example.demo.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserResponseDTO register(User user, UserCreateDTO request);

    List<UserResponseDTO> getAllUsers(MultiValueMap<String, String> allParams);
    Optional<User> getUserById(Long userId);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);

//    List<User> searchByName(String name);

    UserResponseDTO updateUser(User user, Long userId, UserUpdateDTO userDTO);

    void changePassword(User user, String oldPassword, String newPassword);

    void deleteUser(User user, Long userId);
    void deleteUsers(User user, List<Long> ids);
}
