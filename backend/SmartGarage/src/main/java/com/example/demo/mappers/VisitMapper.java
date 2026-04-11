package com.example.demo.mappers;

import com.example.demo.DTO.visit.VisitCreateDTO;
import com.example.demo.DTO.visit.VisitResponseDTO;
import com.example.demo.DTO.visit.VisitUpdateDTO;
import com.example.demo.models.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                VehicleMapper.class,
                UserMapper.class,
                VisitItemMapper.class
        }
)
public interface VisitMapper {

    // ENTITY → RESPONSE DTO
    VisitResponseDTO toDto(Visit visit);

    List<VisitResponseDTO> toDtoList(List<Visit> visits);


    // CREATE DTO → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "visitItems", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "currency", constant = "EUR")
    Visit toEntity(VisitCreateDTO dto);


    // UPDATE DTO → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "visitItems", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "currency", ignore = true)
    Visit toEntity(VisitUpdateDTO dto);
}