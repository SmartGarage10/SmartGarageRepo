package com.example.demo.controllers;

import com.example.demo.DTO.user.UserCreateDTO;
import com.example.demo.DTO.user.UserResponseDTO;
import com.example.demo.DTO.user.UserUpdateDTO;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserControllerRest {
    private final UserService userService;
    private final SecurityHelper securityHelper;

    @Autowired
    public UserControllerRest(UserService userService,
                              SecurityHelper securityHelper) {
        this.userService = userService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(@RequestParam MultiValueMap<String, String> allParams) {
        securityHelper.isAuthenticated();
        return ResponseEntity.ok(userService.getAllUsers(allParams));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserCreateDTO request,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        return ResponseEntity.ok(userService.register(securityHelper.getCurrentUser(), request));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO request,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        return ResponseEntity.ok(userService.updateUser(securityHelper.getCurrentUser(), id, request));
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
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(securityHelper.getCurrentUser(), id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity<?> deleteUser(@RequestBody List<Long> ids) {
        userService.deleteUsers(securityHelper.getCurrentUser(), ids);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Users deleted successfully"));
    }
}
