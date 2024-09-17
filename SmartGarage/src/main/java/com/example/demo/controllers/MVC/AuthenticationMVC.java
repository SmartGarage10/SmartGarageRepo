package com.example.demo.controllers.mvc;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.RegisterDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.helpers.UserMapper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthenticationMVC {
    private final UserService userService;
    private final RoleService roleService;
    private final RestrictHelper restrictHelper;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationMVC.class);

    @Autowired
    public AuthenticationMVC(UserService userService,
                             RoleService roleService,
                             RestrictHelper restrictHelper,
                             UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.restrictHelper = restrictHelper;
        this.userMapper = userMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDTO login,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            logger.info("Login validation errors: {}", bindingResult.getAllErrors());
            return "login";
        }

        try {
            logger.info("Attempting to authenticate user: {}", login.getUsername());
            User user = restrictHelper.verifyAuthentication(login.getUsername(), login.getPassword());

            if (user == null) {
                logger.error("User authentication failed: User is null");
                return "login";
            }

            logger.info("Authenticated user: {}", user);
            session.setAttribute("currentUser", user);
            logger.info("Set 'currentUser' in session: {}", user);

            if (user.getRole().getRoleName().equals(Role.RoleType.ADMIN)) {
                session.setAttribute("isAdmin", true);
                logger.info("User is an admin, set 'isAdmin' in session");
            }
            if (user.getRole().getRoleName().equals(Role.RoleType.EMPLOYEE)) {
                session.setAttribute("isEmployee", true);
                logger.info("User is an employee, set 'isEmployee' in session");
            }

            return "redirect:/employee/clients";
        } catch (AuthorizationException e) {
            logger.error("Authentication error: {}", e.getMessage());
            bindingResult.rejectValue("password", "auth_error", e.getMessage());
            return "login";
        }
    }


    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/auth/login?logout";
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || (!currentUser.getRole().getRoleName().equals(Role.RoleType.ADMIN) && !currentUser.getRole().getRoleName().equals(Role.RoleType.EMPLOYEE))) {
            return "login";
        }

        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("register", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") RegisterDTO register,
                                 HttpSession session,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!register.getPassword().equals(register.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "register";
        }

        try {
            User user = (User) session.getAttribute("currentUser");

            User newUser = userMapper.fromDto(register);
            userService.register(user, newUser);
            return "redirect:/employee/clients";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "register";
        }
    }
}
