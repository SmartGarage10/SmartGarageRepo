package com.example.demo.mappers;

import com.example.demo.DTO.PackDTO;
import com.example.demo.models.Pack;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PackMapper {
    PackDTO toDto(Pack pack);
    Pack toEntity(PackDTO packDTO);
}
