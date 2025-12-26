package com.example.demo.service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.filter.Filter;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.FieldHelper;
import com.example.demo.helpers.FieldUpdateHelper;
import com.example.demo.helpers.PasswordGeneratorHelper;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.mappers.UserMapper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGeneratorHelper passwordGeneratorHelper;
    private final FilterHelper filterHelper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           PasswordGeneratorHelper passwordGeneratorHelper, FilterHelper filterHelper,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordGeneratorHelper = passwordGeneratorHelper;
        this.filterHelper = filterHelper;
        this.roleRepository = roleRepository;
    }
    @Override
    public RegistrationResponse register(User user, UserDTO requestDTO) {
        // Get roleName from RoleDTO
        Role.RoleType roleType = Role.RoleType.valueOf(requestDTO.getRole().getRoleName()); // This gets the role name string

        // Find Role entity by role name
        Role role = roleRepository.findByRoleName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role %s not found", roleType)));
        User request = userMapper.userDtoToUserWithRole(requestDTO, role);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException(String.format("User with username %s already exists", request.getUsername()));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException(String.format("User with email %s already exists", request.getEmail()));
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
                request.getEmail(), LocalDateTime.now());
    }

    @Override
    public List<User> getAllUsers(MultiValueMap<String, String> allParams) {
        Specification<User> spec = Specification.where(null);
        UserSpecifications userSpecs = new UserSpecifications();

        // 1. Apply other filters
        if (allParams.containsKey("filters")) {
            List<Filter> filters = filterHelper.convertToFilters(allParams);
            if (filters != null && !filters.isEmpty()) {
                Specification<User> filterSpec = userSpecs.createSpecification(filters);
                spec = spec.and(filterSpec);
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

        return userRepository.findAll(spec);
    }
    @Override
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }
    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(username)
                .filter(u -> !u.isBlank())
                .map(userRepository::findUserByUsername)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Username '%s' not found", username)));
    }
    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(email)
                .filter(e -> !e.isBlank())
                .map(userRepository::findUserByEmail)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Email '%s' not found", email)));
    }

    @Override
    public User updateUser(User user, int userId, UserDTO userDTO) {
        // 1. Convert DTO to User entity
        User userDetails = userMapper.userDtoToUser(userDTO);
        // 2. Find existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %s not found", userId)));
        // 3. Get all users (for duplicate checking)
        List<User> allUsers = userRepository.findAll();

        // 4. Prepare lists of fields that need duplicate checking and those that don't
        List<FieldUpdateHelper> duplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper("username", User::getUsername, User::setUsername,
                        userDetails.getUsername()),
                new FieldUpdateHelper("email", User::getEmail, User::setEmail,
                        userDetails.getEmail()),
                new FieldUpdateHelper("phone", User::getPhone, User::setPhone,
                        userDetails.getPhone())
        );
        List<FieldUpdateHelper> nonDuplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper("name", User::getName, User::setName,
                        userDetails.getName()),
                new FieldUpdateHelper("address", User::getAddress, User::setAddress,
                        userDetails.getAddress())
        );
        // 5. Process fields with duplicate checking and without
        for (FieldUpdateHelper fieldHelper : duplicateCheckFields) {
            FieldHelper.updateWithDuplicateCheck(
                    existingUser,
                    allUsers,
                    fieldHelper.getter(),
                    fieldHelper.setter(),
                    fieldHelper.newValue(),
                    userId,
                    User::getId,
                    fieldHelper.fieldName()
            );
        }
        for (FieldUpdateHelper fieldHelper : nonDuplicateCheckFields) {
            FieldHelper.updateFieldIfChanged(
                    existingUser,
                    fieldHelper.getter(),
                    fieldHelper.setter(),
                    fieldHelper.newValue()
            );
        }

        // 6. Handle role update by NAME (not ID)
        if (userDetails.getRole() != null) {
            String newRoleName = userDetails.getRole().getRoleName().toString(); // Assuming it's enum
            // Check if role is actually changing
            if (existingUser.getRole() == null ||
                    !newRoleName.equals(existingUser.getRole().getRoleName().toString())) {
                // Fetch role from database by NAME
                Role.RoleType roleType = Role.RoleType.valueOf(newRoleName);
                Role role = roleRepository.findByRoleName(roleType)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Role not found with name: " + newRoleName));
                existingUser.setRole(role);
            }
        }

        return userRepository.save(existingUser);
    }
    @Override
    public void changePassword(User user, String oldPassword, String newPassword) {

        if (passwordEncoder.matches(oldPassword, newPassword)) {
            throw new ResourceConflictException("Your new password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user, int userId) {
        // 1. Check if the target user exists
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %s not found", userId)));
        // 2. Perform deletion
        userRepository.delete(targetUser);
    }
    @Override
    public void deleteUsers(User user, List<Integer> ids) {
        // 1. Perform deletion
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with email %s not found", email)));

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
}
