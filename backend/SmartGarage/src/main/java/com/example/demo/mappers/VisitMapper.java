package com.example.demo.mappers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, VehicleMapper.class, PackMapper.class, ServiceItemMapper.class})
public interface VisitMapper {
    VisitDTO toDto(Visit visit);

    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "vehicle", source = "vehicle")
    @Mapping(target = "pack", source = "pack")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "visitDate", source = "visitDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "visitServices", source = "visitServices")
    Visit toEntity(VisitDTO visitDTO);

}
