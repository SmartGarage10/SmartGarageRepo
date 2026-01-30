package com.example.demo.mappers;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleDTO toDto(Vehicle vehicle);

    @Mapping(target = "client", source = "user")
    @Mapping(target = "year", source = "yearOfCreation")
    Vehicle vehicleDtoToVehicle(VehicleDTO vehicleDTO);

    @Mapping(target = "client", source = "user") // Vehicle.client <- method param User
    @Mapping(target = "year", source = "vehicleDTO.yearOfCreation") // Vehicle.year <- DTO field
    @Mapping(target = "id", ignore = true)
    Vehicle vehicleDtoToVehicleWithUser(VehicleDTO vehicleDTO, User user);
}
