package com.example.demo.controllers;

import com.example.demo.DTO.pack.PackCreateDTO;
import com.example.demo.DTO.pack.PackUpdateDTO;
import com.example.demo.helpers.SecurityHelper;
import com.example.demo.helpers.ValidationHelper;
import com.example.demo.service.PackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PackControllerRest {

    private final PackService packService;
    private final SecurityHelper securityHelper;

    @Autowired
    public PackControllerRest(PackService packService,
                              SecurityHelper securityHelper) {
        this.packService = packService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/packs")
    public ResponseEntity<?> getAllVehicles() {
        securityHelper.isAuthenticated();
        return ResponseEntity.ok(packService.getAllPacks());
    }

    @PostMapping("/create-pack")
    public ResponseEntity<?> createService(@Valid @RequestBody PackCreateDTO packDTO,
                                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(packService.createPack(packDTO));
    }

    @PutMapping("update-pack/{id}")
    public ResponseEntity<?> updateService(@Valid @RequestBody PackUpdateDTO packDTO,
                                           @PathVariable Long id,
                                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ValidationHelper.validate(bindingResult);
        }

        securityHelper.isAuthenticated();
        return ResponseEntity.ok(packService.update(id , packDTO));
    }

    @DeleteMapping("/packs/{id}")
    public ResponseEntity<?> deleteSingleService(@PathVariable Long id) {
        packService.deletePack(securityHelper.getCurrentUser(), id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Pack deleted successfully"));
    }

    @DeleteMapping("/packs/delete")
    public ResponseEntity<?> deleteManyService(@RequestBody List<Long> ids) {
        packService.deletePacks(securityHelper.getCurrentUser(), ids);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Packs deleted successfully"));
    }
}
