package com.example.demo.controllers;

import com.example.demo.DTO.vehicle.VehicleCreateDTO;
import com.example.demo.DTO.vehicle.VehicleUpdateDTO;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.service.VehicleService;
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
public class VehicleControllerRest {
    private final VehicleService vehicleService;
    private final SecurityHelper securityHelper;

    @Autowired
    public VehicleControllerRest(VehicleService vehicleService,
                                 SecurityHelper securityHelper) {
        this.vehicleService = vehicleService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> getAllVehicles(@RequestParam MultiValueMap<String, String> allParams){
        securityHelper.isAuthenticated();
        return ResponseEntity.ok(vehicleService.getAllVehicles(allParams));
    }

    @PostMapping("/create-vehicle")
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleCreateDTO vehicleDTO,
                                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(vehicleService.createNewVehicle(vehicleDTO));
    }

    @PutMapping("update-vehicle/{id}")
    public ResponseEntity<?> updateVehicle(@Valid @RequestBody VehicleUpdateDTO vehicleDTO,
                                                 @PathVariable Long id,
                                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(vehicleService.update(id , vehicleDTO));
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<?> deleteSingleVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(securityHelper.getCurrentUser(), id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Vehicle deleted successfully"));
    }

    @DeleteMapping("/vehicles/delete")
    public ResponseEntity<?> deleteManyVehicles(@RequestBody List<Long> ids) {
        vehicleService.deleteVehicles(securityHelper.getCurrentUser(), ids);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Vehicles deleted successfully"));
    }
}
