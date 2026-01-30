package com.example.demo.service;

import com.example.demo.DTO.RoleDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.PasswordGeneratorHelper;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.mappers.UserMapper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.response.RegistrationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordGeneratorHelper passwordGeneratorHelper;
    @Mock
    private FilterHelper filterHelper;

    @InjectMocks
    private UserServiceImpl userService;

    private User adminUser;
    private UserDTO testUser;
    private Role testRoleEntity;
    private User mappedUser;

    @BeforeEach
    void setUp() {
        // Admin user setup
        this.adminUser = User.builder()
                .email("admin@smartgarage.com")
                .username("admin")
                .build();

        // Test role DTO
        RoleDTO testRoleDto = RoleDTO.builder()
                .roleName("CLIENT")
                .build();

        // Test user DTO
        this.testUser = UserDTO.builder()
                .email("john.doe@example.com")
                .username("john.doe")
                .name("John Doe")
                .role(testRoleDto)
                .phone("1234567890")
                .address("123 Main St")
                .build();

        // Test role entity (reusable across tests)
        this.testRoleEntity = new Role();
        // You can also set role properties if needed:
        // testRoleEntity.setRoleName(Role.RoleType.CLIENT);

        // Mapped user entity (reusable across tests)
        this.mappedUser = User.builder()
                .username(testUser.getUsername())
                .email(testUser.getEmail())
                .name(testUser.getName())
                .role(testRoleEntity)
                .phone(testUser.getPhone())
                .address(testUser.getAddress())
                .build();
    }

    @Nested
    @DisplayName("Create Registered User Tests")
    class CreateRegisteredUserTests {

        @Test
        @DisplayName("createRegisteredUser - Success")
        void testCreateRegisteredUser_Success() {
            // GIVEN
            User creator = adminUser;
            UserDTO userDTO = testUser;
            RoleDTO roleDTO = userDTO.getRole();

            // Mocks using the reusable test data
            Mockito.when(roleRepository.findRoleByRoleName(Role.RoleType.valueOf(roleDTO.getRoleName())))
                    .thenReturn(Optional.of(testRoleEntity));
            Mockito.when(userMapper.userDtoToUserWithRole(userDTO, testRoleEntity)).thenReturn(mappedUser);
            Mockito.when(userRepository.existsByUsername(mappedUser.getUsername())).thenReturn(false);
            Mockito.when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(false);
            Mockito.when(passwordGeneratorHelper.generatePassayPassword()).thenReturn("tempPass");
            Mockito.when(passwordEncoder.encode("tempPass")).thenReturn("encodedPass");

            // Set the encoded password on the mapped user
            mappedUser.setPassword("encodedPass");
            Mockito.when(userRepository.save(mappedUser)).thenReturn(mappedUser);

            Mockito.doNothing().when(emailService).sendRegistrationEmail(
                    anyString(), anyString(), anyString()
            );

            // WHEN
            RegistrationResponse response = userService.register(creator, userDTO);

            // THEN
            assertNotNull(response);
            assertEquals("john.doe@example.com", response.getEmail());

            Mockito.verify(userRepository, Mockito.times(1)).save(mappedUser);
            Mockito.verify(emailService, Mockito.times(1))
                    .sendRegistrationEmail(
                            Mockito.eq(creator.getEmail()),
                            Mockito.eq(userDTO.getEmail()),
                            anyString()
                    );
        }

        @Test
        @DisplayName("createRegisteredUser - Username Already Exists")
        void testCreateRegisteredUser_UsernameAlreadyExists() {
            // GIVEN
            User creator = adminUser;
            UserDTO userDTO = testUser;
            RoleDTO roleDTO = userDTO.getRole();

            // Mock role repository
            Mockito.when(roleRepository.findRoleByRoleName(Role.RoleType.valueOf(roleDTO.getRoleName())))
                    .thenReturn(Optional.of(testRoleEntity));
            Mockito.when(userMapper.userDtoToUserWithRole(userDTO, testRoleEntity)).thenReturn(mappedUser);

            // Mock that username already exists
            Mockito.when(userRepository.existsByUsername(mappedUser.getUsername())).thenReturn(true);

            // WHEN & THEN
            RuntimeException exception = assertThrows(ResourceConflictException.class, () -> {
                userService.register(creator, userDTO);
            });

            // Verify the exception message
            assertNotNull(exception);

            // Verify repository interactions
            Mockito.verify(userRepository, Mockito.times(1)).existsByUsername(mappedUser.getUsername());
            Mockito.verify(userRepository, Mockito.times(0)).existsByEmail(mappedUser.getEmail());
            Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
        }

        @Test
        @DisplayName("createRegisteredUser - Email Already Exists")
        void testCreateRegisteredUser_EmailAlreadyExists() {
            // GIVEN
            User creator = adminUser;
            UserDTO userDTO = testUser;
            RoleDTO roleDTO = userDTO.getRole();

            // Mock role repository
            Mockito.when(roleRepository.findRoleByRoleName(Role.RoleType.valueOf(roleDTO.getRoleName())))
                    .thenReturn(Optional.of(testRoleEntity));
            Mockito.when(userMapper.userDtoToUserWithRole(userDTO, testRoleEntity)).thenReturn(mappedUser);

            // Mock that username doesn't exist but email does
            Mockito.when(userRepository.existsByUsername(mappedUser.getUsername())).thenReturn(false);
            Mockito.when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(true);

            // WHEN & THEN
            RuntimeException exception = assertThrows(ResourceConflictException.class, () -> {
                userService.register(creator, userDTO);
            });

            // Verify the exception message
            assertNotNull(exception);

            // Verify repository interactions
            Mockito.verify(userRepository, Mockito.times(1)).existsByUsername(mappedUser.getUsername());
            Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(mappedUser.getEmail());
            Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
        }

        @Test
        @DisplayName("createRegisteredUser - Role Not Found")
        void testCreateRegisteredUser_RoleNotFound() {
            // GIVEN
            User creator = adminUser;
            UserDTO userDTO = testUser;
            RoleDTO roleDTO = userDTO.getRole();

            // Mock role repository to return empty
            Mockito.when(roleRepository.findRoleByRoleName(Role.RoleType.valueOf(roleDTO.getRoleName())))
                    .thenReturn(Optional.empty());

            // WHEN & THEN
            RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.register(creator, userDTO);
            });

            // Verify the exception message
            assertNotNull(exception);

            // Verify no other interactions
            Mockito.verify(userMapper, Mockito.times(0)).userDtoToUserWithRole(Mockito.any(), Mockito.any());
            Mockito.verify(userRepository, Mockito.times(0)).existsByUsername(Mockito.anyString());
            Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
        }
    }
}