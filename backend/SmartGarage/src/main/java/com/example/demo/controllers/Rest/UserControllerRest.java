package com.example.demo.controllers.Rest;

import com.example.demo.DTO.*;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.*;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
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
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Autowired
    public UserControllerRest(UserService userService, SecurityHelper securityHelper, ObjectMapper objectMapper, UserMapper userMapper) {
        this.userService = userService;
        this.securityHelper = securityHelper;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam MultiValueMap<String, String> allParams) {
        try {
            securityHelper.isAuthenticated();

            List<Filter> filterList = convertToFilters(allParams);
            return ResponseEntity.ok(userService.getAllUsers(filterList));
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
            validateRequest(bindingResult);

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
            validateRequest(bindingResult);

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
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            User currentUser = securityHelper.getCurrentUser();

            userService.deleteUser(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }



    private List<Filter> convertToFilters(MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyList();
        }

        // Special handling for JSON-encoded filters parameter
        if (params.containsKey("filters")) {
            try {
                String filtersJson = params.getFirst("filters");
                return objectMapper.readValue(filtersJson, new TypeReference<List<Filter>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse filters parameter", e);
            }
        }

        // Default conversion for regular parameters
        return params.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("page") &&
                        !entry.getKey().equals("size") &&
                        !entry.getKey().equals("sort"))
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> {
                            Filter filter = new Filter();
                            filter.setId(entry.getKey());
                            filter.setValue(value);
                            filter.setVariant("text");  // Default variant
                            filter.setOperator("iLike"); // Default operator
                            filter.setFilterId(UUID.randomUUID().toString()); // Generate unique ID
                            return filter;
                        })
                )
                .collect(Collectors.toList());
    }

    private ResponseEntity<Map<String, String>> validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }
}
