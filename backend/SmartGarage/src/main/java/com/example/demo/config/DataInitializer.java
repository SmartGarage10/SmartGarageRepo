package com.example.demo.config;

import com.example.demo.models.Role;
import com.example.demo.models.Role.RoleType;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(String... args) {
        createDefaultRoles();
        createDefaultAdmin();
    }

    private void createDefaultRoles() {
        if (roleRepository.count() == 0) {
            System.out.println("⚙️ Creating default roles...");

            Role adminRole = new Role();
            adminRole.setRoleName(RoleType.ADMIN);

            Role employeeRole = new Role();
            employeeRole.setRoleName(RoleType.EMPLOYEE);

            Role clientRole = new Role();
            clientRole.setRoleName(RoleType.CLIENT);

            roleRepository.saveAll(List.of(adminRole, employeeRole, clientRole));
            System.out.println("✅ Roles created: ADMIN, EMPLOYEE, CLIENT");
        } else {
            System.out.println("ℹ️ Roles already exist, skipping creation.");
        }
    }

    private void createDefaultAdmin() {
        if (userRepository.count() == 0) {
            System.out.println("⚙️ No users found. Creating default admin...");

            Role adminRole = roleRepository.findAll().stream()
                    .filter(r -> r.getRoleName() == RoleType.ADMIN)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found!"));

            User admin = new User();
            admin.setUsername(adminProperties.username());
            admin.setPassword(passwordEncoder.encode(adminProperties.password()));
            admin.setEmail(adminProperties.email());
            admin.setName(adminProperties.name());
            admin.setAddress(adminProperties.address());
            admin.setPhone(adminProperties.phone());
            admin.setRole(adminRole);

            userRepository.save(admin);
            System.out.println("✅ Default admin created: " + admin.getUsername());
        } else {
            System.out.println("ℹ️ Users already exist, skipping default admin creation.");
        }
    }
}
