package com.example.demo.controllers;

import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.models.User;
import com.example.demo.response.AuthenticationResponse;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserControllerRest {
    private final UserServiceImpl userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserControllerRest(UserServiceImpl userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String vehicleModel,
            @RequestParam(required = false) String vehicleMake,
            @RequestParam(required = false) LocalDateTime visitStartDate,
            @RequestParam(required = false) LocalDateTime visitEndDate,
            @RequestParam(required = false, defaultValue = "username") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);
            userService.getAllUsers(name, email, phone, vehicleModel, vehicleMake, visitStartDate, visitEndDate, sortField, sortDirection);

            return ResponseEntity.ok().build();
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);

            return ResponseEntity.ok(userService.register(request));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
        try {
            return ResponseEntity.ok(userService.authenticate(request));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody User request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);
            userService.updateUser(user.getId(), request);
            return ResponseEntity.ok().build();
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);
            userService.deleteUser(user.getId());
            return ResponseEntity.ok().build();
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
