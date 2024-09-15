package com.example.demo.service;

import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.helpers.RestrictHelper;
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
    private final RestrictHelper restrictHelper;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JWTService jwtService,
                           @Lazy AuthenticationManager authenticationManager,
                           @Lazy RestrictHelper restrictHelper,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.restrictHelper = restrictHelper;
        this.emailService = emailService;
    }


    @Override
    public AuthenticationResponse register(User user, User request) {
        restrictHelper.isUserAdminOrEmployee(user);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EntityDuplicateException("User", "username", request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityDuplicateException("User", "email", request.getEmail());
        }

        String fromEmail = user.getEmail();
        String toEmail = request.getEmail();
        String subject = "Welcome to BMW Garage";
        String body = "We are thrilled to have you on board. Your registration has been successfully completed, and we are excited for you to begin your journey with us.\n" +
                "Username: " + request.getUsername() + "\n" +
                "Password: " + request.getPassword() + "\n\n" +
                "Please make sure to keep this information secure. You can log in and change your password after your first login.\n\n" +
                "If you have any questions or need assistance, feel free to reach out.\n\n" +
                "We look forward to working with you!\n\n" +
                "Best regards,\n" +
                "BMW Garage";

        emailService.sendRegistrationEmail(fromEmail, toEmail, subject, body);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(request);
        String token = jwtService.generateToken(request);
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
    public User updateUser(User user, int userId, User userDetails) {
        restrictHelper.isUserAdminOrEmployee(user);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", "id", String.valueOf(userId));
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Id", userId));

        String newUsername = userDetails.getUsername();
        if (newUsername != null && !userRepository.existsByUsername(newUsername)) {
            existingUser.setUsername(newUsername);
        }

        String newEmail = userDetails.getEmail();
        if (newEmail != null && !userRepository.existsByEmail(newEmail)) {
            existingUser.setEmail(newEmail);
        }

        String newPhone = userDetails.getPhone();
        if (newPhone != null && !userRepository.existsByPhone(newPhone)) {
            existingUser.setPhone(newPhone);
        }

        return userRepository.save(existingUser);
    }
    @Override
    public void changePassword(User user, String oldPassword, String newPassword) {

        if (passwordEncoder.matches(oldPassword, newPassword)) {
            throw new IllegalArgumentException("Your new password can't be the same as old one ");
        }

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
    public List<User> getAllUsers(String username, String email, String phone, String vehicleModel, String vehicleMake,
                                  LocalDateTime visitStartDate, LocalDateTime visitEndDate, String sortField, String sortDirection) {
        Specification<User> spec = Specification.where(null);

        if (username != null && !username.isEmpty()) {
            spec = spec.and(UserSpecifications.hasName(username));
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
    public void deleteUser(User user, int userId) {
        restrictHelper.isUserAdminOrEmployee(user);
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
