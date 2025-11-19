package com.example.demo.mappers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, VehicleMapper.class, PackMapper.class, ServiceItemMapper.class})
public interface VisitMapper {
    VisitDTO toDto(Visit visit);

    @Mapping(target = "visitServices", source = "visitServices")
    Visit toEntity(VisitDTO visitDTO);
}

