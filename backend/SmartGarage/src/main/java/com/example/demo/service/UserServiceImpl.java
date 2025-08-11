package com.example.demo.service;

import com.example.demo.DTO.Filter;
import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.helpers.FilterHelper;
import com.example.demo.helpers.PasswordGeneratorHelper;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.response.RegistrationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final RestrictHelper restrictHelper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGeneratorHelper passwordGeneratorHelper;
    private final FilterHelper filterHelper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Lazy AuthenticationManager authenticationManager,
                           @Lazy RestrictHelper restrictHelper,
                           EmailService emailService,
                           PasswordGeneratorHelper passwordGeneratorHelper,
                           FilterHelper filterHelper,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.restrictHelper = restrictHelper;
        this.emailService = emailService;
        this.passwordGeneratorHelper = passwordGeneratorHelper;
        this.filterHelper = filterHelper;
        this.roleRepository = roleRepository;
    }


    @Override
    public Optional<User> authenticate(User user, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword())
        );
        // Create new security context and set authentication
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Store security context in session
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        return userRepository.findUserByEmail(user.getEmail());
    }
    @Override
    public RegistrationResponse register(User user, User request) {
        restrictHelper.isUserAdminOrEmployee(user);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EntityDuplicateException("User", "username", request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityDuplicateException("User", "email", request.getEmail());
        }

        String tempPass = passwordGeneratorHelper.generatePassayPassword();

        String fromEmail = user.getEmail();
        String toEmail = request.getEmail();
        String subject = "Welcome to BMW Garage";
        String body = "We are thrilled to have you on board. Your registration has been successfully completed, and we are excited for you to begin your journey with us.\n" +
                "Username: " + request.getUsername() + "\n" +
                "Password: " + tempPass + "\n\n" +
                "Please make sure to keep this information secure. You can log in and change your password after your first login.\n\n" +
                "If you have any questions or need assistance, feel free to reach out.\n\n" +
                "We look forward to working with you!\n\n" +
                "Best regards,\n" +
                "BMW Garage";

        request.setPassword(passwordEncoder.encode(tempPass));
        emailService.sendRegistrationEmail(fromEmail, toEmail, subject, body);
        userRepository.save(request);

        return new RegistrationResponse(
                "Registration successful. User can now login with their credentials",
                request.getUsername(), LocalDateTime.now());
    }

    @Override
    public List<User> getAllUsers(MultiValueMap<String, String> allParams) {
        Specification<User> spec = Specification.where(null);
        UserSpecifications userSpecs = new UserSpecifications();

        // 1. Apply role filters if present
        if (allParams.containsKey("role")) {
            List<String> roles = allParams.get("role");
            List<Role> roleList = roleRepository.findByRoleNameIn(roles);
            if (!roles.isEmpty()) {
                Specification<User> roleSpec = userSpecs.createRoleSpecification(roleList);
                spec = spec.and(roleSpec);
            }
        }

        // 2. Apply search filter if present
        if (allParams.containsKey("search")) {
            String search = allParams.getFirst("search");
            if (search != null && !search.trim().isEmpty()) {
                Specification<User> searchSpec = userSpecs.createSearchSpecification(search, "name");
                spec = spec.and(searchSpec);
            }
        }

        // 3. Apply other filters
        if (allParams.containsKey("filters")) {
            List<Filter> filters = filterHelper.convertToFilters(allParams);
            if (filters != null && !filters.isEmpty()) {
                Specification<User> searchSpec = userSpecs.createSpecification(filters);
                spec = spec.and(searchSpec);
            }
        }

        return userRepository.findAll(spec);
    }
    @Override
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }
    @Override
    public Optional<User> getUserByUsername(String username) {
        if (username.isEmpty()) {
            throw new EntityNotFoundException("User", "username", username);
        }
        return userRepository.findUserByUsername(username);
    }
    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email.isEmpty()) {
            throw new EntityNotFoundException("Email", "email", email);
        }
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User updateUser(User user, int userId, User userDetails) {
        // 1. Check User permissions
        restrictHelper.isUserAdminEmployeeOrOwner(user, userId);

        // 2. Find existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(userId)));

        // 3. Get all users (for duplicate checking)
        List<User> allUsers = userRepository.findAll();

        // 4. Process all updatable fields
        Stream.of(
                        Map.entry("username", userDetails.getUsername()),
                        Map.entry("email", userDetails.getEmail()),
                        Map.entry("phone", userDetails.getPhone())
                )
                .forEach(entry -> {
                    String currentValue = getCurrentFieldValue(existingUser, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .filter(newValue -> !isDuplicateField(allUsers, entry.getKey(), newValue, userId))
                            .ifPresent(newValue -> setUserField(existingUser, entry.getKey(), newValue));
                });

        // 5. Process fields without duplicate checking
        Stream.of(
                        Map.entry("name", userDetails.getName()),
                        Map.entry("address", userDetails.getAddress())
                )
                .forEach(entry -> {
                    String currentValue = getCurrentFieldValue(existingUser, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue))
                            .ifPresent(newValue -> setUserField(existingUser, entry.getKey(), newValue));
                });

        // 6. Handle role update separately (admin only)

        if (userDetails.getRole() != null && !userDetails.getRole().equals(existingUser.getRole())) {
            existingUser.setRole(userDetails.getRole());
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
    public void deleteUser(User user, int userId) {
        // 1. Validate permissions (admin, employee, or owner)
        restrictHelper.isUserAdminEmployeeOrOwner(user, userId);

        // 2. Check if the target user exists
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // 3. Perform deletion
        userRepository.delete(targetUser);
    }
    @Override
    public void deleteUsers(User user, List<Integer> ids) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Perform deletion
        userRepository.deleteAllById(ids);
    }

    /**
     * Loads the user details based on the provided email.
     * <p>
     * This method is used by Spring Security during authentication to retrieve
     * user credentials and authorities from the database. It assumes that the
     * user's email is used as the username identifier.
     * </p>
     *
     * @param email the user's email address, used as the login identifier
     * @return a Spring Security {@link UserDetails} object containing the user's credentials and authorities
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // or user.getUsername() if needed
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

    // Helper to check for duplicates in memory
    private boolean isDuplicateField(List<User> allUsers, String field, String value, int currentUserId) {
        return allUsers.stream()
                .filter(user -> user.getId() != currentUserId) // Exclude current user (int comparison)
                .anyMatch(user -> value.equals(getCurrentFieldValue(user, field)));
    }
    // Helper to get field value
    private String getCurrentFieldValue(User user, String field) {
        return switch (field) {
            case "name" -> user.getName();
            case "username" -> user.getUsername();
            case "email" -> user.getEmail();
            case "phone" -> user.getPhone();
            case "role" -> user.getRole().toString();
            default -> null;
        };
    }
    // Helper to set field value
    private void setUserField(User user, String field, String value) {
        switch (field) {
            case "name" -> user.setName(value);
            case "username" -> user.setUsername(value);
            case "email" -> user.setEmail(value);
            case "phone" -> user.setPhone(value);
            case "address" -> user.setAddress(value);
        }
    }
}
