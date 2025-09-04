package com.example.demo.controllers.Rest;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.LoginMapper;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationControllerRest {
    private final UserService userService;
    private final LoginMapper loginMapper;
    private final SecurityHelper securityHelper;

    @Autowired
    public AuthenticationControllerRest(UserService userService, LoginMapper loginMapper, SecurityHelper securityHelper) {
        this.userService = userService;
        this.loginMapper = loginMapper;
        this.securityHelper = securityHelper;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            System.out.println(passwordEncoder.encode("pass123"));
            User user = loginMapper.fromDto(loginDTO);
            return ResponseEntity.ok(userService.authenticate(user, request));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Add CSRF check if you're using session-based auth
        if (request.getSession(false) != null && request.getSession(false).getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            // Clear authentication specifically
            request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthenticationResponse("Successfully logged out."));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        securityHelper.isAuthenticated();
        return ResponseEntity.ok().body(securityHelper.getCurrentUser());
    }

}
