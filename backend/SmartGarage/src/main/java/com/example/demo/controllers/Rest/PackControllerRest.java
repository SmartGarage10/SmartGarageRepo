package com.example.demo.controllers.Rest;

import com.example.demo.DTO.PackDTO;
import com.example.demo.DTO.ServiceDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.PackMapper;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.service.PackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PackControllerRest {

    private final PackService packService;
    private final SecurityHelper securityHelper;
    private final PackMapper packMapper;

    @Autowired
    public PackControllerRest(PackService packService,
                              SecurityHelper securityHelper,
                              PackMapper packMapper) {
        this.packService = packService;
        this.securityHelper = securityHelper;
        this.packMapper = packMapper;
    }

    @GetMapping("/packs")
    public ResponseEntity<List<Pack>> getAllVehicles() {
        try {
            securityHelper.isAuthenticated();
            return ResponseEntity.ok(packService.getAllPacks());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/create-pack")
    public ResponseEntity<?> createService(@Valid @RequestBody PackDTO packDTO,
                                           BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                ValidationHelper.validate(bindingResult);
            }

            User currentUser = securityHelper.getCurrentUser();
            Pack pack = packMapper.fromDto(packDTO);
            return ResponseEntity.ok(packService.createPack(currentUser, pack));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("update-pack/{id}")
    public ResponseEntity<?> updateService(@Valid @RequestBody PackDTO packDTO,
                                           @PathVariable Long id,
                                           BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                ValidationHelper.validate(bindingResult);
            }

            User currentUser = securityHelper.getCurrentUser();
            Pack pack = packMapper.fromDto(packDTO);
            return ResponseEntity.ok(packService.update(currentUser, id , pack));
        }
        catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/packs/{id}")
    public ResponseEntity<?> deleteSingleService(@PathVariable Long id) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            packService.deletePack(currentUser, id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Service deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/packs/delete")
    public ResponseEntity<?> deleteManyService(@RequestBody List<Long> ids) {
        try {
            User currentUser = securityHelper.getCurrentUser();
            packService.deletePacks(currentUser, ids);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
