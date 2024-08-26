package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserControllerRest {
    private final UserServiceImpl userService;

    @Autowired
    public UserControllerRest(UserServiceImpl userService) {
        this.userService = userService;
    }

    public ResponseEntity<List<User>> getListOfAllUsers(){
        try {
            return null;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
