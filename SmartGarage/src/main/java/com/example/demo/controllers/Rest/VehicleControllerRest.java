package com.example.demo.controllers.Rest;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.helpers.VehicleMapper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleControllerRest {
    private final AuthenticationHelper authenticationHelper;
    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;

    @Autowired
    public VehicleControllerRest(AuthenticationHelper authenticationHelper, VehicleService vehicleService, VehicleMapper vehicleMapper) {
        this.authenticationHelper = authenticationHelper;
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Vehicle>> getAllVehicles(@RequestParam(required = false) String username,  @RequestParam(required = false, defaultValue = "asc") String sortDirection){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);

            return ResponseEntity.ok(vehicleService.getAllVehicles(user, username, sortDirection));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody VehicleDTO vehicleDTO){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);

            Vehicle newVehicle = vehicleMapper.fromDto(vehicleDTO);

            return ResponseEntity.ok(vehicleService.createNewVehicle(newVehicle));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Vehicle> updateVehicle(@RequestBody VehicleDTO vehicleDTO){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(auth);

            Vehicle vehicle = vehicleService.getVehicleByLicencePlate(vehicleDTO.getLicensePlate())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle", "licence plate", vehicleDTO.getLicensePlate()));

            return ResponseEntity.ok(vehicleService.update(user, vehicle));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable int id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = authenticationHelper.extractUserFromToken(auth);

        vehicleService.deleteVehicle(user, id);
        return ResponseEntity.ok().build();
    }
}
