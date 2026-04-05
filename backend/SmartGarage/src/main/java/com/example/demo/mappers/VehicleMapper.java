package com.example.demo.mappers;

import com.example.demo.DTO.vehicle.VehicleCreateDTO;
import com.example.demo.DTO.vehicle.VehicleResponseDTO;
import com.example.demo.DTO.vehicle.VehicleUpdateDTO;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface VehicleMapper {

    // ENTITY → RESPONSE DTO
    @Mapping(target = "user", source = "client") // MapStruct ще използва UserMapper.toDTO()
    @Mapping(target = "yearOfCreation", source = "year")
    VehicleResponseDTO toDto(Vehicle vehicle);

    // CREATE DTO → ENTITY (без user)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "year", source = "yearOfCreation")
    Vehicle toEntity(VehicleCreateDTO dto);

    // UPDATE DTO → ENTITY (без user)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "year", source = "yearOfCreation")
    Vehicle toEntity(VehicleUpdateDTO dto);

    // CREATE DTO + USER → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", source = "user")
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "year", source = "dto.yearOfCreation")
    Vehicle toEntityWithUser(VehicleCreateDTO dto, User user);
}
