package com.example.demo.controllers;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.helpers.ServiceMapper;
import com.example.demo.models.ServiceItem;
import com.example.demo.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service")
public class ServiceControllerRest {

    private final ServiceService service;
    private final ServiceMapper mapper;

    public ServiceControllerRest(ServiceService service, ServiceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable int id) {
        Optional<ServiceItem> serviceItem = Optional.ofNullable(service.getServiceById(id));
        if (serviceItem.isPresent()) {
            return ResponseEntity.ok(mapper.toDto(serviceItem.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable int id, @RequestBody ServiceDTO serviceDTO) {
        ServiceItem serviceItem = mapper.fromDto(serviceDTO);
        ServiceItem updatedService = service.updateService(id, serviceItem);
        return ResponseEntity.ok(mapper.toDto(updatedService));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable int id) {
        service.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        List<ServiceItem> services = service.allServices();
        List<ServiceDTO> serviceDTOs = services.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(serviceDTOs);
    }
}
