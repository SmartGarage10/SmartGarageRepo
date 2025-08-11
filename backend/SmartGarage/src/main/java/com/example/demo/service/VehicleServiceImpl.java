package com.example.demo.service;

import com.example.demo.DTO.Filter;
import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.filter.VehicleSpecifications;
import com.example.demo.helpers.FilterHelper;
import com.example.demo.helpers.GenericFieldAccessor;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VehicleServiceImpl implements VehicleService{
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final RestrictHelper restrictHelper;
    private final FilterHelper filterHelper;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              UserRepository userRepository,
                              RestrictHelper restrictHelper,
                              FilterHelper filterHelper) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
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
    public Optional<Vehicle> getVehicleByLicencePlate(String licencePLate){
        if (licencePLate.isEmpty() || licencePLate.isBlank()){
            throw new EntityNotFoundException("Vehicle", "licence plate", licencePLate);
        }
        return vehicleRepository.findByVehiclePlate(licencePLate);
    }
    @Override
    public Optional<Vehicle> getVehicleByVin(String vin){
        if (vin.isEmpty() || vin.isBlank()){
            throw new EntityNotFoundException("Vehicle", "vin", vin);
        }
        return vehicleRepository.findByVehiclePlate(vin);
    }
    @Override
    public List<Vehicle> getVehiclesByUser(User user) {
        return vehicleRepository.findVehiclesByClient(user);
    }

    @Override
    public Vehicle createNewVehicle(User user, Vehicle vehicle){
        restrictHelper.isUserAdminOrEmployee(user);

        if (vehicleRepository.existsByVehiclePlate(vehicle.getVehiclePlate())) {
            throw new IllegalArgumentException("Vehicle Plate already exists.");
        }
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new IllegalArgumentException("Vehicle VIN already exists.");
        }

        return vehicleRepository.save(vehicle);
    }
    @Override
    public Vehicle update(User user, int vehicleId, Vehicle changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Find existing user
        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", "id", String.valueOf(vehicleId)));

        // 3. Get all users (for duplicate checking)
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
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with ID - " + vehicleId + " not found."));

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
