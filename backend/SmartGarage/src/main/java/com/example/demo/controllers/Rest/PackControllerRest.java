package com.example.demo.controllers.Rest;

import com.example.demo.helpers.SecurityHelper;
import com.example.demo.models.Pack;
import com.example.demo.service.PackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api")
public class PackControllerRest {

    private final PackService packService;
    private final SecurityHelper securityHelper;

    @Autowired
    public PackControllerRest(PackService packService, SecurityHelper securityHelper) {
        this.packService = packService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/packs")
    public ResponseEntity<List<Pack>> getAllVehicles(){
        try {
            securityHelper.isAuthenticated();
            return ResponseEntity.ok(packService.getAllPacks());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
