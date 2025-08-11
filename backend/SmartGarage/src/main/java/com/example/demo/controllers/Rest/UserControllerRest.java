package com.example.demo.controllers.Rest;

import com.example.demo.DTO.*;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.*;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserControllerRest {
    private final UserService userService;
    private final SecurityHelper securityHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserControllerRest(UserService userService,
                              SecurityHelper securityHelper,
                              UserMapper userMapper) {
        this.userService = userService;
        this.securityHelper = securityHelper;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam MultiValueMap<String, String> allParams) {
        try {
            securityHelper.isAuthenticated();
            return ResponseEntity.ok(userService.getAllUsers(allParams));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserDTO request,
            BindingResult bindingResult) {
        try {
            // Check for validation errors
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            User newUser = userMapper.fromDto(request);

            return ResponseEntity.ok(userService.register(currentUser, newUser));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @Valid @RequestBody UserDTO request,
            BindingResult bindingResult) {

        try {
            // Check for validation errors
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            User updateUser = userMapper.fromDto(request);

            return ResponseEntity.ok(userService.updateUser(currentUser, id, updateUser));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    //    @PutMapping("/password")
//    public ResponseEntity<Void> updateUserPassword(@RequestBody PasswordDTO request) {
//        try {
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            User user = authenticationHelper.extractUserFromToken(auth);
//            userService.changePassword(user, user.getPassword(), request.getPassword());
//            return ResponseEntity.ok().build();
//        } catch (AuthorizationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        }
//    }
//
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            userService.deleteUser(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity<?> deleteUser(@RequestBody List<Integer> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            userService.deleteUsers(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
