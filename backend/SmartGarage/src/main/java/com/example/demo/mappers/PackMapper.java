package com.example.demo.mappers;

import com.example.demo.DTO.pack.PackCreateDTO;
import com.example.demo.DTO.pack.PackResponseDTO;
import com.example.demo.DTO.pack.PackUpdateDTO;
import com.example.demo.models.Pack;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PackMapper {
    Pack toEntity(PackCreateDTO packDTO);
    Pack toEntity(PackUpdateDTO packDTO);

    PackResponseDTO toDto(Pack pack);
}
