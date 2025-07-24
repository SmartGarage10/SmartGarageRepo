package com.example.demo.service;

import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.UserSpecifications;
import com.example.demo.filter.VehicleSpecifications;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService{
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final RestrictHelper restrictHelper;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, UserRepository userRepository, RestrictHelper restrictHelper) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.restrictHelper = restrictHelper;
    }

    @Override
    public Vehicle update(User user, Vehicle vehicle, Vehicle changes) {
        // Check if the user has permission to perform the update
        restrictHelper.isUserAdminOrEmployee(user);

        // Validate and update vehicle license plate
        if (changes.getVehiclePlate() != null && !changes.getVehiclePlate().isEmpty() && !userRepository.existsByUsername(changes.getVehiclePlate())) {
            vehicle.setVehiclePlate(changes.getVehiclePlate());
        }

        // Update vehicle owner
        if (changes.getClient().getUsername() != null) {
            // Validate if the owner is valid and exists
            if (!userRepository.existsByUsername(changes.getClient().getUsername())) {
                throw new IllegalArgumentException("Owner does not exist.");
            }
            vehicle.setClient(changes.getClient());
        }

        // Save and return the updated vehicle
        return vehicleRepository.save(vehicle);
    }


    @Override
    public Vehicle createNewVehicle(Vehicle vehicle){
        if (vehicleRepository.existsByVin(vehicle.getVin()) || vehicleRepository.existsByVehiclePlate(vehicle.getVehiclePlate())){
            throw new EntityDuplicateException("This vehicle already exist!");
        }

        if (!userRepository.existsByUsername(vehicle.getClient().getUsername())){
            throw new EntityDuplicateException("User", "username", vehicle.getClient().getUsername());
        }

        return vehicleRepository.save(vehicle);
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
    public Optional<Vehicle> getVehicleByVin(String vin){
        if (vin.isEmpty() || vin.isBlank()){
            throw new EntityNotFoundException("Vehicle", "vin", vin);
        }
        return vehicleRepository.findByVehiclePlate(vin);
    }
    @Override
    public List<Vehicle> getAllVehicles(User user, String ownerName, String brand, String model, Year year, String sortField, String sortDirection) {
        boolean isClient = user.getRole().getRoleName().equals(Role.RoleType.CLIENT);

        // Build the Specification for filtering
        Specification<Vehicle> spec = Specification.where(null);

        if (isClient) {
            spec = spec.and(VehicleSpecifications.hasOwnerName(user.getUsername()));
        } else {
            if (ownerName != null && !ownerName.isEmpty()) {
                spec = spec.and(VehicleSpecifications.hasOwnerName(ownerName));
            }
        }

        if (model != null && !model.isEmpty()) {
            spec = spec.and(VehicleSpecifications.hasVehicleModel(model));
        }

        if (brand != null && !brand.isEmpty()) {
            spec = spec.and(VehicleSpecifications.hasVehicleBrand(brand));
        }
        if (year != null) {
            spec = spec.and(VehicleSpecifications.hasVehicleYear(year));
        }

        // Set default sort field if none provided
        if (sortField == null || sortField.isEmpty()) {
            sortField = "brand";  // Default field to sort by
        }

        // Set default sort direction if none provided
        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";  // Default sort direction
        }

        // Create the sort order
        Sort.Order order = "desc".equalsIgnoreCase(sortDirection) ? Sort.Order.desc(sortField) : Sort.Order.asc(sortField);
        Sort sort = Sort.by(order);

        // Fetch and return the filtered and sorted list of vehicles
        return vehicleRepository.findAll(spec, sort);
    }

    @Override
    public List<Vehicle> getVehiclesByUser(User user) {
        return vehicleRepository.findVehiclesByClient(user);
    }

    @Override
    public void deleteVehicle(User user, int vehicleId){
        restrictHelper.isUserAdminOrEmployee(user);
        if (vehicleId < 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than zero.");
        }
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new EntityNotFoundException("Id", vehicleId);
        }

        vehicleRepository.deleteById(vehicleId);
    }
}
