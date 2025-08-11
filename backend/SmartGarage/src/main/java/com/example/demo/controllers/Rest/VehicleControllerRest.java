package com.example.demo.controllers.Rest;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.helpers.VehicleMapper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class VehicleControllerRest {
    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final SecurityHelper securityHelper;

    @Autowired
    public VehicleControllerRest(VehicleService vehicleService,
                                 VehicleMapper vehicleMapper,
                                 SecurityHelper securityHelper) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles(@RequestParam MultiValueMap<String, String> allParams){
        try {
            securityHelper.isAuthenticated();
            return ResponseEntity.ok(vehicleService.getAllVehicles(allParams));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/create-vehicle")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO,
                                                 BindingResult bindingResult){
        try {
            // Check for validation errors
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            Vehicle vehicle = vehicleMapper.fromDto(vehicleDTO);

            return ResponseEntity.ok(vehicleService.createNewVehicle(currentUser, vehicle));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("update-vehicle/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@Valid @RequestBody VehicleDTO vehicleDTO,
                                                 @PathVariable int id,
                                                 BindingResult bindingResult){
        try {
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            Vehicle vehicle = vehicleMapper.fromDto(vehicleDTO);

            return ResponseEntity.ok(vehicleService.update(currentUser, id , vehicle));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<?> deleteSingleVehicle(@PathVariable int id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            vehicleService.deleteVehicle(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/vehicles/delete")
    public ResponseEntity<?> deleteManyVehicles(@RequestBody List<Integer> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            vehicleService.deleteVehicles(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteVehicle(@PathVariable int id){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = authenticationHelper.extractUserFromToken(auth);
//
//        vehicleService.deleteVehicle(user, id);
//        return ResponseEntity.ok().build();
//    }
}
