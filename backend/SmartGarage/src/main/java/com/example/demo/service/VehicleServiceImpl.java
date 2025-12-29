package com.example.demo.service;

import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.filter.VehicleSpecifications;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.EntityServiceHelper;
import com.example.demo.helpers.FieldUpdateHelper;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService, EntitySpecificationProvider<Vehicle> {
    private final VehicleRepository vehicleRepository;
    private final RestrictHelper restrictHelper;
    private final FilterHelper filterHelper;
    private final VehicleSpecifications vehicleSpecs;
    private final EntityServiceHelper<Vehicle, Long, VehicleRepository> entityHelper;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              RestrictHelper restrictHelper,
                              FilterHelper filterHelper) {
        this.vehicleRepository = vehicleRepository;
        this.restrictHelper = restrictHelper;
        this.filterHelper = filterHelper;
        this.vehicleSpecs = new VehicleSpecifications();

        // Create EntityServiceHelper instance
        this.entityHelper = new EntityServiceHelper<>(
                vehicleRepository,
                filterHelper,
                Vehicle.class,
                this  // Pass 'this' as specification provider
        );
    }

    // ========== SPECIFICATION PROVIDER IMPLEMENTATION ==========
    @Override
    public Specification<Vehicle> createFilterSpecification(List<Filter> filters) {
        return vehicleSpecs.createSpecification(filters);
    }
    @Override
    public Specification<Vehicle> createSearchSpecification(String search) {
        String searchField = getSearchField();
        return vehicleSpecs.createSearchSpecification(search, searchField);
    }
    @Override
    public String getSearchField() {
        return "vehiclePlate";  // Default search field for vehicles
    }

    // ========== SERVICE METHODS USING HELPER ==========
    @Override
    public List<Vehicle> getAllVehicles(MultiValueMap<String, String> allParams) {
        // Use entityHelper.getAll() - specifications are handled automatically
        return entityHelper.getAll(allParams);
    }

    @Override
    public Optional<Vehicle> getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    @Override
    public Vehicle createNewVehicle(User user, Vehicle vehicle) {
        restrictHelper.isUserAdminOrEmployee(user);

        if (vehicleRepository.existsByVehiclePlate(vehicle.getVehiclePlate())) {
            throw new ResourceConflictException(String.format("Vehicle with plate %s already exists", vehicle.getVehiclePlate()));
        }
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new ResourceConflictException(String.format("Vehicle with VIN %s already exists", vehicle.getVin()));
        }

        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(User user, Long vehicleId, Vehicle changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Find existing vehicle
        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Vehicle with id %s not found", vehicleId)));

        // 3. Prepare FieldUpdateHelper lists WITH CORRECT TYPES

        // String fields with duplicate checking
        List<FieldUpdateHelper<Vehicle, ?>> duplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper<>("vehiclePlate",
                        Vehicle::getVehiclePlate,
                        Vehicle::setVehiclePlate,
                        changes.getVehiclePlate()),
                new FieldUpdateHelper<>("vin",
                        Vehicle::getVin,
                        Vehicle::setVin,
                        changes.getVin())
        );

        // Mixed types - you can't have different types in the same generic list!
        // You need to handle this differently
        List<FieldUpdateHelper<Vehicle, ?>> nonDuplicateCheckFields = Arrays.asList(
                // String fields
                new FieldUpdateHelper<>("brand",
                        Vehicle::getBrand,
                        Vehicle::setBrand,
                        changes.getBrand()),
                new FieldUpdateHelper<>("model",
                        Vehicle::getModel,
                        Vehicle::setModel,
                        changes.getModel()),
                new FieldUpdateHelper<>("client",
                        Vehicle::getClient,
                        Vehicle::setClient,
                        changes.getClient()),

                // Integer field - THIS IS THE PROBLEM!
                new FieldUpdateHelper<>("year",
                        Vehicle::getYear,
                        Vehicle::setYear,
                        changes.getYear()!= null ? Year.of(changes.getYear().getValue()) : null)
        );

        // 4. Use entityHelper.update()
        return entityHelper.update(
                vehicleId,
                existingVehicle,
                duplicateCheckFields,
                nonDuplicateCheckFields,
                Vehicle::getId
        );
    }

    @Override
    public void deleteVehicle(User user, Long vehicleId) {
        // 1. Check if the target vehicle exists
        Vehicle targetVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vehicle with id %s not found", vehicleId)));
        // 2. Use entityHelper.delete()
        entityHelper.delete(vehicleId);
    }

    @Override
    public void deleteVehicles(User user, List<Long> ids) {
        // 1. Use entityHelper.deleteAllById()
        entityHelper.deleteAllById(ids);
    }
}