package com.example.demo.service;

import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.repositories.PackRepository;
import com.example.demo.repositories.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;
    private final RestrictHelper restrictHelper;
    private final ServiceRepository serviceRepository;

    public PackServiceImpl(PackRepository packRepository,
                           RestrictHelper restrictHelper,
                           ServiceRepository serviceRepository) {
        this.packRepository = packRepository;
        this.restrictHelper = restrictHelper;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    @Override
    public Optional<Pack> getPackById(Long packId) {
        return packRepository.findById(packId);
    }

    @Override
    public Pack createPack(User user, Pack pack) {
        restrictHelper.isUserAdmin(user);

        if (packRepository.existsPacksByPackName(pack.getPackName())) {
            throw new IllegalArgumentException("Pack already exists.");
        }

        if (pack.getServices() != null && !pack.getServices().isEmpty()) {
            for (ServiceItem service : pack.getServices()) {
                // Find service by name instead of ID
                serviceRepository.findByServiceName(service.getServiceName()).orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Service %s not found.", service.getServiceName())));
            }
        }

        return packRepository.save(pack);
    }

    @Override
    public Pack update(User user, Long packId, Pack changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdmin(user);

        // 2. Find existing pack
        Pack existingPack = packRepository.findById(packId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Pack with id %d not found.", packId)));

        // 3. Check for duplicate pack name (if changed)
        if (changes.getPackName() != null && !changes.getPackName().equals(existingPack.getPackName())) {
            if (packRepository.existsPacksByPackName(changes.getPackName())) {
                throw new ResourceConflictException(String.format("Pack with name %s already exists.", changes.getPackName()));
            }
            existingPack.setPackName(changes.getPackName());
        }

        // 4. Update simple fields
        if (changes.getDescription() != null) {
            existingPack.setDescription(changes.getDescription());
        }

        if (changes.getAmount() != null) {
            existingPack.setAmount(changes.getAmount());
        }

        // 5. FIX: Handle services properly - clear and add new services
        if (changes.getServices() != null) {
            existingPack.getServices().clear(); // Clear existing services

            if (!changes.getServices().isEmpty()) {
                List<ServiceItem> managedServices = new ArrayList<>();

                for (ServiceItem service : changes.getServices()) {
                    // Find the managed service entity from database
                    ServiceItem managedService = serviceRepository.findByServiceName(service.getServiceName())
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("Service %s not found.", service.getServiceName())));
                    managedServices.add(managedService);
                }
                existingPack.getServices().addAll(managedServices);
            }
        }

        return packRepository.save(existingPack);
    }

    @Override
    public void deletePack(User user, Long packId) {
        // 1. Validate permissions (admin)
        restrictHelper.isUserAdmin(user);

        // 2. Check if the target user exists
        Pack targetPack = packRepository.findById(packId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Pack with id %d not found.", packId)));

        // 3. Perform deletion
        packRepository.delete(targetPack);
    }

    @Override
    public void deletePacks(User user, List<Long> ids) {
        // 1. Validate permissions (admin)
        restrictHelper.isUserAdmin(user);

        // 2. Perform deletion
        packRepository.deleteAllById(ids);
    }
}
