package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.models.Vehicle;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Vehicle createNewVehicle(Vehicle vehicle);

    Optional<Vehicle> getVehicleById(int vehicleId);

    Vehicle update(User user, Vehicle vehicle, Vehicle changes);

    Optional<Vehicle> getVehicleByLicencePlate(String licencePlate);

    Optional<Vehicle> getVehicleByVin(String vin);

    List<Vehicle> getAllVehicles(User user, String ownerName, String vehicleMake, String vehicleModel, Year year, String sortField, String sortDirection);

    List<Vehicle> getVehiclesByUser(User user);
    void deleteVehicle(User user, int vehicleId);
}
