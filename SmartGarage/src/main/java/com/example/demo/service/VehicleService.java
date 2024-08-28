package com.example.demo.service;

import com.example.demo.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Vehicle saveVehicle(Vehicle vehicle);

    Optional<Vehicle> getVehicleById(int vehicleId);

    Optional<Vehicle> getVehicleByLicencePlate(String licencePlate);

    Optional<Vehicle> getVehicleByVin(String vin);

    List<Vehicle> getAllVehicles();

    void deleteVehicle(int vehicleId);
}
