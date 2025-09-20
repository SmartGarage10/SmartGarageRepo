package com.example.demo.service;

import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.helpers.GenericFieldAccessor;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.Pack;
import com.example.demo.models.User;
import com.example.demo.repositories.PackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;
    private final RestrictHelper restrictHelper;


    public PackServiceImpl(PackRepository packRepository, RestrictHelper restrictHelper) {
        this.packRepository = packRepository;
        this.restrictHelper = restrictHelper;
    }

    @Override
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    @Override
    public Optional<Pack> getPackById(int packId) {
        return packRepository.findById(packId);
    }

    @Override
    public Pack createPack(User user, Pack pack) {
        restrictHelper.isUserAdmin(user);

        if (packRepository.existsPacksByPackName(pack.getPackName().toString())) {
            throw new IllegalArgumentException("Pack already exists.");
        }

        return packRepository.save(pack);
    }

    @Override
    public Pack update(User user, int packId, Pack changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdmin(user);

        // 2. Find existing vehicle
        Pack eexistingPack = packRepository.findById(packId)
                .orElseThrow(() -> new EntityNotFoundException("Pack", "id", String.valueOf(packId)));

        // 3. Get all vehicles (for duplicate checking)
        List<Pack> allPacks = packRepository.findAll();

        // 4. Process all updatable fields
        Stream.of(
                        Map.entry("pack", changes.getPackName())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(eexistingPack, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .filter(newValue -> !GenericFieldAccessor.isDuplicateField(allPacks, entry.getKey(), newValue, packId))
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(eexistingPack, entry.getKey(), newValue));
                });

        // 5. Process fields without duplicate checking
        Stream.of(
                        Map.entry("description", changes.getDescription())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(eexistingPack, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(eexistingPack, entry.getKey(), newValue));
                });

        return packRepository.save(eexistingPack);
    }

    @Override
    public void deletePack(User user, int packId) {
        // 1. Validate permissions (admin)
        restrictHelper.isUserAdmin(user);

        // 2. Check if the target user exists
        Pack targetPack = packRepository.findById(packId)
                .orElseThrow(() -> new EntityNotFoundException("Pack with ID - " + packId + " not found."));

        // 3. Perform deletion
        packRepository.delete(targetPack);
    }

    @Override
    public void deletePacks(User user, List<Integer> ids) {
        // 1. Validate permissions (admin)
        restrictHelper.isUserAdmin(user);

        // 2. Perform deletion
        packRepository.deleteAllById(ids);
    }
}
