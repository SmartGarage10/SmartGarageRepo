package com.example.demo.controllers;

import com.example.demo.DTO.serviceItem.ServiceItemCreateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemUpdateDTO;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getAllServices(@RequestParam MultiValueMap<String, String> allParams){
        securityHelper.isAuthenticated();
        return ResponseEntity.ok(service.getAllServices(allParams));
    }

    @PostMapping("/create-service")
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceItemCreateDTO serviceDTO,
                                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        return ResponseEntity.ok(service.createNewService(securityHelper.getCurrentUser(), serviceDTO));
    }

    @PutMapping("update-service/{id}")
    public ResponseEntity<?> updateService(@Valid @RequestBody ServiceItemUpdateDTO serviceDTO,
                                           @PathVariable Long id,
                                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(service.update(id , serviceDTO));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteSingleService(@PathVariable Long id) {
        service.deleteService(securityHelper.getCurrentUser(), id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Service deleted successfully"));
    }

    @DeleteMapping("/services/delete")
    public ResponseEntity<?> deleteManyService(@RequestBody List<Long> ids) {
        service.deleteServices(securityHelper.getCurrentUser(), ids);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Services deleted successfully"));
    }
}
