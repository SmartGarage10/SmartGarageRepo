package com.example.demo.mappers;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.models.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleDTO toDto(Vehicle vehicle);
    Vehicle toEntity(VehicleDTO vehicleDTO);
}
