package com.example.demo.controllers;

import com.example.demo.DTO.visit.VisitCreateDTO;
import com.example.demo.DTO.visit.VisitUpdateDTO;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.service.VisitService;
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
    public ResponseEntity<?> getAllVisits(@RequestParam MultiValueMap<String, String> allParams){
        securityHelper.isAuthenticated();
        return ResponseEntity.ok(visitService.getAllVisits(allParams));
    }

    @PostMapping("/create-visit")
    public ResponseEntity<?> createVisit(@Valid @RequestBody VisitCreateDTO visitDTO,
                                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(visitService.createVisit(visitDTO));
    }

    @PutMapping("/update-visit/{id}")
    public ResponseEntity<?> updateVisit(@Valid @RequestBody VisitUpdateDTO visitDTO,
                                                 @PathVariable Long id,
                                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(visitService.update(id , visitDTO));
    }

    @DeleteMapping("/visits/{id}")
    public ResponseEntity<?> deleteSingleVisit(@PathVariable Long id) {
        visitService.deleteVisit(securityHelper.getCurrentUser(), id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Visit deleted successfully"));
    }

    @DeleteMapping("/visits/delete")
    public ResponseEntity<?> deleteManyVisits(@RequestBody List<Long> ids) {
        visitService.deleteVisits(securityHelper.getCurrentUser(), ids);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Visits deleted successfully"));
    }
}
