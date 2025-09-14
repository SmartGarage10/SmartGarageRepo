package com.example.demo.controllers.Rest;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ServiceMapper;
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
    private final ServiceMapper serviceMapper;
    private final SecurityHelper securityHelper;

    public ServiceControllerRest(ServiceService service, ServiceMapper serviceMapper, SecurityHelper securityHelper) {
        this.service = service;
        this.serviceMapper = serviceMapper;
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
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceDTO serviceDTO,
                                           BindingResult bindingResult){
        try {
            // Check for validation errors
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            ServiceItem serviceItem = serviceMapper.fromDto(serviceDTO);

            return ResponseEntity.ok(service.createNewService(currentUser, serviceItem));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("update-service/{id}")
    public ResponseEntity<?> updateService(@Valid @RequestBody ServiceDTO serviceDTO,
                                           @PathVariable int id,
                                           BindingResult bindingResult){
        try {
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            ServiceItem serviceItem = serviceMapper.fromDto(serviceDTO);

            return ResponseEntity.ok(service.update(currentUser, id , serviceItem));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/service/{id}")
    public ResponseEntity<?> deleteSingleService(@PathVariable int id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            service.deleteService(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/services/delete")
    public ResponseEntity<?> deleteManyService(@RequestBody List<Integer> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            service.deleteServices(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
