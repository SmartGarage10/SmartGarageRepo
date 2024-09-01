package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Vehicle createNewVehicle(Vehicle vehicle);

    Optional<Vehicle> getVehicleById(int vehicleId);
    Vehicle update(User user, Vehicle vehicle);
    Optional<Vehicle> getVehicleByLicencePlate(String licencePlate);

    Optional<Vehicle> getVehicleByVin(String vin);

    List<Vehicle> getAllVehicles(String ownerName, String sortDirection);

    void deleteVehicle(User user, int vehicleId);
}
