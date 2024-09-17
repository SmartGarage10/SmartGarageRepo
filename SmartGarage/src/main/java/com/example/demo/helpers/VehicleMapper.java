package com.example.demo.helpers;

import com.example.demo.DTO.EditVehicleDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.DTO.VehicleDTO;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.service.UserService;
import com.example.demo.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class VehicleMapper {
    private final VehicleService vehicleService;
    private final UserService userService;

    public Vehicle fromDto(int id, VehicleDTO vehicleDTO){
        Vehicle vehicle = fromDto(vehicleDTO);
        vehicle.setId(id);

        return vehicle;
    }


    public Vehicle fromDto(VehicleDTO vehicleDTO){
        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(vehicleDTO.getLicensePlate());
        vehicle.setVin(vehicleDTO.getVin());
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setYear(vehicleDTO.getYearOfCreation());

        User user = userService.getUserByUsername(vehicleDTO.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User", "username", vehicleDTO.getUsername()));
        vehicle.setClient(user);

        return vehicle;
    }

    public Vehicle fromDto(EditVehicleDTO vehicleDTO){
        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(vehicleDTO.getLicensePlate());
        User user = userService.getUserByUsername(vehicleDTO.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User", "username", vehicleDTO.getUsername()));
        vehicle.setClient(user);

        return vehicle;
    }
}
