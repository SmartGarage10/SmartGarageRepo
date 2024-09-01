package com.example.demo.service;

import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.VehicleSpecifications;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    public Vehicle update(User user, Vehicle vehicle) {
        restrictHelper.isUserAdminOrEmployee(user);

        String licencePlate = vehicle.getVehiclePlate();
        if (licencePlate != null && !userRepository.existsByUsername(licencePlate)) {
            vehicle.setVehiclePlate(licencePlate);
        }

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
    public List<Vehicle> getAllVehicles(String ownerName, String sortDirection){
        Specification<Vehicle> spec = Specification.where(null);
        if (ownerName != null && !ownerName.isEmpty()) {
            spec = spec.and(VehicleSpecifications.hasOwnerName(ownerName));
        }

        String sortField = "client";
        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";
        }
        Sort.Order order = "desc".equalsIgnoreCase(sortDirection) ? Sort.Order.desc(sortField) : Sort.Order.asc(sortField);
        Sort sort = Sort.by(order);

        List<Vehicle> vehicles = vehicleRepository.findAll(spec, sort);
        return vehicles;
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
