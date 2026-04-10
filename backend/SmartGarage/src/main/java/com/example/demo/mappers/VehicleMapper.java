package com.example.demo.mappers;

import com.example.demo.DTO.vehicle.VehicleCreateDTO;
import com.example.demo.DTO.vehicle.VehicleResponseDTO;
import com.example.demo.DTO.vehicle.VehicleUpdateDTO;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Year;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface VehicleMapper {

    // ENTITY → RESPONSE DTO// MapStruct ще използва UserMapper.toDTO()
    @Mapping(target = "year", source = "year", qualifiedByName = "yearToString")
    VehicleResponseDTO toDto(Vehicle vehicle);

    // CREATE DTO → ENTITY (без user)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "year", source = "year", qualifiedByName = "stringToYear")
    Vehicle toEntity(VehicleCreateDTO dto);

    // UPDATE DTO → ENTITY (без user)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "year", source = "year", qualifiedByName = "stringToYear")
    Vehicle toEntity(VehicleUpdateDTO dto);

    // CREATE DTO + USER → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", source = "user")
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "year", source = "dto.year", qualifiedByName = "stringToYear")
    Vehicle toEntityWithUser(VehicleCreateDTO dto, User user);

    @Named("yearToString")
    default String yearToString(Year year) {
        return year != null ? year.toString() : null;
    }

    @Named("stringToYear")
    default Year stringToYear(String year) {
        return (year != null && !year.isBlank()) ? Year.parse(year) : null;
    }

}
