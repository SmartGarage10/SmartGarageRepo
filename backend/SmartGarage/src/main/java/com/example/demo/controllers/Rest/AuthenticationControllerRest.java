package com.example.demo.controllers.Rest;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.LoginMapper;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationControllerRest {
    private final AuthenticationService authenticationService;
    private final SecurityHelper securityHelper;

    @Autowired
    public AuthenticationControllerRest(AuthenticationService authenticationService, SecurityHelper securityHelper) {
        this.authenticationService = authenticationService;
        this.securityHelper = securityHelper;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO userDTO, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(userDTO, request));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        securityHelper.isAuthenticated();
        return ResponseEntity.ok().body(securityHelper.getCurrentUser());
    }

}
