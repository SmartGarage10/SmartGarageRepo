package com.example.demo.controllers;

import com.example.demo.DTO.ServiceItemDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiceControllerRest {

    private final ServiceService service;
    private final SecurityHelper securityHelper;

    public ServiceControllerRest(ServiceService service,
                                 SecurityHelper securityHelper) {
        this.service = service;
        this.securityHelper = securityHelper;
    }
    @GetMapping("/services")
    public ResponseEntity<List<ServiceItem>> getAllServices(@RequestParam MultiValueMap<String, String> allParams){
        try {
            securityHelper.isAuthenticated();
            return ResponseEntity.ok(service.getAllServices(allParams));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/create-service")
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceItemDTO serviceDTO,
                                           BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                ValidationHelper.validate(bindingResult);
            }

            User currentUser = securityHelper.getCurrentUser();
            return ResponseEntity.ok(service.createNewService(currentUser, serviceDTO));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("update-service/{id}")
    public ResponseEntity<?> updateService(@Valid @RequestBody ServiceItemDTO serviceDTO,
                                           @PathVariable Long id,
                                           BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                ValidationHelper.validate(bindingResult);
            }

            User currentUser = securityHelper.getCurrentUser();
            return ResponseEntity.ok(service.update(currentUser, id , serviceDTO));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteSingleService(@PathVariable Long id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            service.deleteService(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Service deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/services/delete")
    public ResponseEntity<?> deleteManyService(@RequestBody List<Long> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            service.deleteServices(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
