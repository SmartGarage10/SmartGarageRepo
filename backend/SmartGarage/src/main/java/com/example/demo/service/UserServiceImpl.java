package com.example.demo.service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.*;
import com.example.demo.mappers.UserMapper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.response.RegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService, EntitySpecificationProvider<User> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGeneratorHelper passwordGeneratorHelper;
    private final UserSpecifications userSpecifications;
    private final EntityServiceHelper<User, Long, UserRepository> entityHelper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           PasswordGeneratorHelper passwordGeneratorHelper,
                           FilterHelper filterHelper,
                           RoleRepository roleRepository,
                           @Lazy UserSpecifications userSpecifications) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordGeneratorHelper = passwordGeneratorHelper;
        this.roleRepository = roleRepository;
        this.userSpecifications = userSpecifications;
        // Create EntityServiceHelper instance manually
        this.entityHelper = new EntityServiceHelper<>(
                userRepository,
                filterHelper,
                User.class,
                this
        );
    }

    @Override
    public Specification<User> createFilterSpecification(List<Filter> filters) {
        return userSpecifications.createSpecification(filters);
    }

    @Override
    public Specification<User> createSearchSpecification(String search) {
        String searchField = getSearchField();
        return userSpecifications.createSearchSpecification(search, searchField);
    }

    @Override
    public String getSearchField() {
        return "name";
    }

    @Override
    public List<User> getAllUsers(MultiValueMap<String, String> params) {
        return entityHelper.getAll(params);
    }
    @Override
    public RegistrationResponse register(User user, UserDTO requestDTO) {
        // Find Role entity by role name
        Role role = roleRepository.findRoleByRoleName(Role.RoleType.valueOf(requestDTO.getRole().getRoleName()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role %s not found", requestDTO.getRole().getRoleName())));
        User request = userMapper.userDtoToUserWithRole(requestDTO, role);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException(String.format("User with username %s already exists", request.getUsername()));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException(String.format("User with email %s already exists", request.getEmail()));
        }

        String tempPass = passwordGeneratorHelper.generatePassayPassword();
        request.setPassword(passwordEncoder.encode(tempPass));

        String toEmail = request.getEmail();
        emailService.sendRegistrationEmail(toEmail, request.getUsername(), tempPass);
        userRepository.save(request);

        return new RegistrationResponse(
                "Registration successful. User can now login with their credentials",
                request.getEmail(), LocalDateTime.now());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
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
    public User updateUser(User user, Long userId, UserDTO userDTO) {
        // 1. Convert DTO to User entity
        User userDetails = userMapper.userDtoToUser(userDTO);
        // 2. Find existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with id %s not found", userId)));

        // 3. Prepare lists of fields that need duplicate checking and those that don't
        List<FieldUpdateHelper<User, ?>> duplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper<>("username", User::getUsername, User::setUsername,
                        userDetails.getUsername()),
                new FieldUpdateHelper<>("email", User::getEmail, User::setEmail,
                        userDetails.getEmail()),
                new FieldUpdateHelper<>("phone", User::getPhone, User::setPhone,
                        userDetails.getPhone())
        );
        List<FieldUpdateHelper<User, ?>> nonDuplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper<>("name", User::getName, User::setName,
                        userDetails.getName()),
                new FieldUpdateHelper<>("address", User::getAddress, User::setAddress,
                        userDetails.getAddress())
        );

        // 4. Handle role update by NAME (not ID)
        if (userDetails.getRole() != null) {
            String newRoleName = userDetails.getRole().getRoleName().toString(); // Assuming it's enum
            // Check if role is actually changing
            if (existingUser.getRole() == null ||
                    !newRoleName.equals(existingUser.getRole().getRoleName().toString())) {
                // Fetch role from database by NAME
                Role.RoleType roleType = Role.RoleType.valueOf(newRoleName);
                Role role = roleRepository.findRoleByRoleName(roleType)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Role not found with name: " + newRoleName));
                existingUser.setRole(role);
            }
        }

        return entityHelper.update(userId, existingUser, duplicateCheckFields, nonDuplicateCheckFields, User::getId);
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
    public void deleteUser(User user, Long userId) {
        // 1. Perform deletion
        entityHelper.delete(userId);
    }
    @Override
    public void deleteUsers(User user, List<Long> ids) {
        // 1. Perform deletion
        entityHelper.deleteAllById(ids);
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
