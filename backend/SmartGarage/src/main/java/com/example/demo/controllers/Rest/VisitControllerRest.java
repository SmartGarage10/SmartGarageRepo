package com.example.demo.controllers.Rest;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.DTO.VisitDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.helpers.VisitMapper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.models.Visit;
import com.example.demo.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VisitControllerRest {
    private final VisitService visitService;
    private final SecurityHelper securityHelper;
    private final VisitMapper visitMapper;

    @Autowired
    public VisitControllerRest(VisitService visitService,
                               SecurityHelper securityHelper,
                               VisitMapper visitMapper) {
        this.visitService = visitService;
        this.securityHelper = securityHelper;
        this.visitMapper = visitMapper;
    }


    @GetMapping("/visits")
    public ResponseEntity<List<Visit>> getAllVehicles(@RequestParam MultiValueMap<String, String> allParams){
        try {
            securityHelper.isAuthenticated();
            return ResponseEntity.ok(visitService.getAllVisits(allParams));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/create-visit")
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VisitDTO visitDTO,
                                                 BindingResult bindingResult){
        try {
            // Check for validation errors
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            Visit visit = visitMapper.fromDto(visitDTO);

            return ResponseEntity.ok(visitService.createVisit(currentUser, visit));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("update-visit/{id}")
    public ResponseEntity<?> updateVehicle(@Valid @RequestBody VisitDTO visitDTO,
                                                 @PathVariable int id,
                                                 BindingResult bindingResult){
        try {
            ValidationHelper.validate(bindingResult);

            User currentUser = securityHelper.getCurrentUser();
            Visit visit = visitMapper.fromDto(visitDTO);

            return ResponseEntity.ok(visitService.update(currentUser, id , visit));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/visits/{id}")
    public ResponseEntity<?> deleteSingleVehicle(@PathVariable int id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            visitService.deleteVisit(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/visits/delete")
    public ResponseEntity<?> deleteManyVehicles(@RequestBody List<Integer> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            visitService.deleteVisits(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
