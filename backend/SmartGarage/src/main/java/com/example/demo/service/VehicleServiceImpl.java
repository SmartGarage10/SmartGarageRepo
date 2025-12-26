package com.example.demo.service;

import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.Filter;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.VehicleSpecifications;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.GenericFieldAccessor;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class VehicleServiceImpl implements VehicleService{
    private final VehicleRepository vehicleRepository;
    private final RestrictHelper restrictHelper;
    private final FilterHelper filterHelper;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              RestrictHelper restrictHelper,
                              FilterHelper filterHelper) {
        this.vehicleRepository = vehicleRepository;
        this.restrictHelper = restrictHelper;
        this.filterHelper = filterHelper;
    }

    @Override
    public List<Vehicle> getAllVehicles(MultiValueMap<String, String> allParams) {
        Specification<Vehicle> spec = Specification.where(null);
        VehicleSpecifications vehicleSpecs = new VehicleSpecifications();

        // 1. Apply search filter if present
        if (allParams.containsKey("search")) {
            String search = allParams.getFirst("search");
            if (search != null && !search.trim().isEmpty()) {
                Specification<Vehicle> searchSpec = vehicleSpecs.createSearchSpecification(search, "vehiclePlate");
                spec = spec.and(searchSpec);
            }
        }

        // 2. Apply other filters
        if (allParams.containsKey("filters")) {
            List<Filter> filters = filterHelper.convertToFilters(allParams);
            if (filters != null && !filters.isEmpty()) {
                Specification<Vehicle> filterSpec = vehicleSpecs.createSpecification(filters);
                spec = spec.and(filterSpec);
            }
        }

        return vehicleRepository.findAll(spec);
    }
    @Override
    public Optional<Vehicle> getVehicleById(int vehicleId){
        return vehicleRepository.findById(vehicleId);
    }

    @Override
    public Vehicle createNewVehicle(User user, Vehicle vehicle){
        restrictHelper.isUserAdminOrEmployee(user);

        if (vehicleRepository.existsByVehiclePlate(vehicle.getVehiclePlate())) {
            throw new ResourceConflictException(String.format("Vehicle with plate %s already exists", vehicle.getVehiclePlate()));
        }
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new ResourceConflictException(String.format("Vehicle with plate %s already exists", vehicle.getVin()));
        }

        return vehicleRepository.save(vehicle);
    }
    @Override
    public Vehicle update(User user, int vehicleId, Vehicle changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Find existing vehicle
        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vehicle with id %s not found", vehicleId)));

        // 3. Get all vehicles (for duplicate checking)
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        // 4. Process all updatable fields
        Stream.of(
                        Map.entry("vehiclePlate", changes.getVehiclePlate()),
                        Map.entry("vin", changes.getVin())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingVehicle, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .filter(newValue -> !GenericFieldAccessor.isDuplicateField(allVehicles, entry.getKey(), newValue, vehicleId))
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingVehicle, entry.getKey(), newValue));
                });

        // 5. Process fields without duplicate checking
        Stream.of(
                        Map.entry("brand", changes.getBrand()),
                        Map.entry("model", changes.getModel()),
                        Map.entry("year", changes.getYear()),
                        Map.entry("client", changes.getClient())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingVehicle, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingVehicle, entry.getKey(), newValue));
                });

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public void deleteVehicle(User user, int vehicleId) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Check if the target user exists
        Vehicle targetVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vehicle with id %s not found", vehicleId)));

        // 3. Perform deletion
        vehicleRepository.delete(targetVehicle);
    }

    @Override
    public void deleteVehicles(User user, List<Integer> ids) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Perform deletion
        vehicleRepository.deleteAllById(ids);
    }
}
