package com.example.demo.service;

import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.response.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityDuplicateException("User", "email", user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    @Override
    public AuthenticationResponse authenticate(User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword())
        );
        Optional<User> findUser = userRepository.findUserByUsername(user.getUsername());
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    @Override
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
    @Override
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
    @Override
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }
    @Override
    public Optional<User> getUserByUsername(String username) {
        if (username.isEmpty() || username.isBlank()) {
            throw new EntityNotFoundException("User", "username", username);
        }
        return userRepository.findUserByUsername(username);
    }
    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email.isEmpty() || email.isBlank()) {
            throw new EntityNotFoundException("User", "email", email);
        }
        return userRepository.findUserByEmail(email);
    }
    @Override
    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
    @Override
    public List<User> getAllUsers(String name, String email, String phone, String vehicleModel, String vehicleMake,
                                  LocalDateTime visitStartDate, LocalDateTime visitEndDate, String sortField, String sortDirection) {
        Specification<User> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(UserSpecifications.hasName(name));
        }

        if (email != null && !email.isEmpty()) {
            spec = spec.and(UserSpecifications.hasEmail(email));
        }

        if (phone != null && !phone.isEmpty()) {
            spec = spec.and(UserSpecifications.hasPhone(phone));
        }

        if ((vehicleModel != null && !vehicleModel.isEmpty()) || (vehicleMake != null && !vehicleMake.isEmpty())) {
            spec = spec.and(UserSpecifications.hasVehicleModelOrMake(vehicleModel, vehicleMake));
        }

        if (visitStartDate != null && visitEndDate != null) {
            spec = spec.and(UserSpecifications.visitsInRange(visitStartDate, visitEndDate));
        }

        if (sortField == null || sortField.isEmpty()) {
            sortField = "username";
        }
        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";
        }

        Sort.Order order = "desc".equalsIgnoreCase(sortDirection) ? Sort.Order.desc(sortField) : Sort.Order.asc(sortField);
        Sort sort = Sort.by(order);

        return userRepository.findAll(spec, sort);
    }

    @Override
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
