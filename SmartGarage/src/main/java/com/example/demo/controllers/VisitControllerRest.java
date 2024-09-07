package com.example.demo.controllers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.helpers.VisitMapper;
import com.example.demo.models.User;
import com.example.demo.models.Visit;
import com.example.demo.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/visits")
public class VisitControllerRest {
    private final VisitService service;
    private final VisitMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VisitControllerRest(VisitService service, VisitMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }
    

    @PostMapping("/create")
    public ResponseEntity<VisitDTO> createVisit(@RequestBody VisitDTO visitDTO){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(authentication);

            Visit visit= mapper.fromDto(visitDTO);
            service.createVisit(user,visit);
            return ResponseEntity.ok(mapper.toDto(visit));
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> deleteVisits(@PathVariable int id){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = authenticationHelper.extractUserFromToken(authentication);

            service.deleteVisit(user, id);
            return ResponseEntity.ok().build();
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
