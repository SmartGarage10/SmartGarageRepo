package com.example.demo.service;

import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityDuplicateException("User", "email", user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(int userId, User userDetails) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", "id", String.valueOf(userId));
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Id", userId));

        String newUsername = userDetails.getUsername();

        if (newUsername == null || userRepository.existsByUsername(newUsername)) {
            throw new EntityDuplicateException("User", "username", newUsername);
        }
        existingUser.setUsername(newUsername);

        String newEmail = userDetails.getEmail();
        if (newEmail == null || userRepository.existsByEmail(newEmail)) {
            throw new EntityDuplicateException("User", "email", newEmail);
        }
        existingUser.setEmail(newEmail);

        String newPhone = userDetails.getPhone();
        if (newPhone == null || userRepository.existsByPhone(newPhone)) {
            throw new EntityDuplicateException("User", "phone", newPhone);
        }
        existingUser.setPhone(newPhone);

        return userRepository.save(existingUser);
    }

    public void changePassword(int userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

//        if (!isValidPassword(newPassword)) {
//            throw new IllegalArgumentException("New password does not meet the required criteria.");
//        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        if (username.isEmpty() || username.isBlank()) {
            throw new EntityNotFoundException("User", "username", username);
        }
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        if (email.isEmpty() || email.isBlank()) {
            throw new EntityNotFoundException("User", "email", email);
        }
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Id", userId);
        }

        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole() != null ?
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName())) :
                        Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
