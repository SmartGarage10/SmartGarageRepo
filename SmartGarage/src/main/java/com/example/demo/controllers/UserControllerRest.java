package com.example.demo.controllers;

import com.example.demo.DTO.UserDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.helpers.UserMapper;
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
@RequestMapping("/api/user")
public class UserControllerRest {
    private final UserServiceImpl userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserControllerRest(UserServiceImpl userService,
                              AuthenticationHelper authenticationHelper,
                              UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String username,
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

            return ResponseEntity.ok(userService.getAllUsers(username, email, phone, vehicleModel, vehicleMake, visitStartDate, visitEndDate, sortField, sortDirection));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserDTO request){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);

            User createUser = userMapper.fromDto(request);

            return ResponseEntity.ok(userService.register(user, createUser));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDTO request) {
        try {
            User user = userMapper.fromDto(request);
            return ResponseEntity.ok(userService.authenticate(user));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody UserDTO request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);
            User updateUser = userMapper.fromDto(request);
            userService.updateUser(user, id, updateUser);
            return ResponseEntity.ok().build();
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);
            userService.deleteUser(user, id);
            return ResponseEntity.ok().build();
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
