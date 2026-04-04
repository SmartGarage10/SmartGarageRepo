package com.example.demo.controllers;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.user.UserLoginDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userDTO, HttpServletRequest request) {
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
