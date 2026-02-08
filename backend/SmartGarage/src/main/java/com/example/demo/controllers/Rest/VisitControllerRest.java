package com.example.demo.controllers.Rest;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.mappers.VisitMapper;
import com.example.demo.models.User;
import com.example.demo.models.Visit;
import com.example.demo.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
public class VisitControllerRest {
    private final VisitService visitService;
    private final SecurityHelper securityHelper;

    @Autowired
    public VisitControllerRest(VisitService visitService,
                               SecurityHelper securityHelper) {
        this.visitService = visitService;
        this.securityHelper = securityHelper;
    }


    @GetMapping("/visits")
    public ResponseEntity<List<Visit>> getAllVisits(@RequestParam MultiValueMap<String, String> allParams){
        try {
            securityHelper.isAuthenticated();
            List<Visit> visits = visitService.getAllVisits(allParams);
            System.out.println("Visits: " + visits);
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/create-visit")
    public ResponseEntity<?> createVisit(@Valid @RequestBody VisitDTO visitDTO,
                                                 BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                ValidationHelper.validate(bindingResult);
            }

            User currentUser = securityHelper.getCurrentUser();
            return ResponseEntity.ok(visitService.createVisit(currentUser, visitDTO));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/update-visit/{id}")
    public ResponseEntity<?> updateVisit(@Valid @RequestBody VisitDTO visitDTO,
                                                 @PathVariable Long id,
                                                 BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                ValidationHelper.validate(bindingResult);
            }

            User currentUser = securityHelper.getCurrentUser();
            return ResponseEntity.ok(visitService.update(currentUser, id , visitDTO));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/visits/{id}")
    public ResponseEntity<?> deleteSingleVisit(@PathVariable Long id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            visitService.deleteVisit(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/visits/delete")
    public ResponseEntity<?> deleteManyVisits(@RequestBody List<Long> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            visitService.deleteVisits(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
