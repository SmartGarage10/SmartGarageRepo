package com.example.demo.service;

import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.Vehicle;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public Vehicle saveVehicle(Vehicle vehicle){
        if (vehicleRepository.existsByVin(vehicle.getVin()) || vehicleRepository.existsByVehiclePlate(vehicle.getVehiclePlate())){
            throw new EntityDuplicateException("This vehicle already exist!");
        }

        if (!userRepository.existsByUsername(vehicle.getClient().getUsername())){
            throw new EntityDuplicateException("User", "username", vehicle.getClient().getUsername());
        }

        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> getVehicleById(int vehicleId){
        return vehicleRepository.findById(vehicleId);
    }

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
    public List<Vehicle> getAllVehicles(){
        return vehicleRepository.findAll();
    }

    public void deleteVehicle(int vehicleId){
        if (vehicleId < 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than zero.");
        }
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new EntityNotFoundException("Id", vehicleId);
        }

        vehicleRepository.deleteById(vehicleId);
    }
}
