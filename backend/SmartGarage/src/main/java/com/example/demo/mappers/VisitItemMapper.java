package com.example.demo.mappers;

import com.example.demo.DTO.visitItem.VisitItemCreateDTO;
import com.example.demo.DTO.visitItem.VisitItemResponseDTO;
import com.example.demo.DTO.visitItem.VisitItemUpdateDTO;
import com.example.demo.models.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ServiceItemMapper.class, PackMapper.class}
)
public interface VisitItemMapper {

    // ========== ENTITY → RESPONSE ==========
    VisitItemResponseDTO toDto(VisitItem visitItem);
    List<VisitItemResponseDTO> toDtoList(List<VisitItem> visitItems);

    // ========== CREATE DTO → ENTITY ==========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "serviceItem", source = "serviceItemId", qualifiedByName = "mapService")
    @Mapping(target = "pack", source = "packId", qualifiedByName = "mapPack")
    @Mapping(target = "itemType", ignore = true)
    @Mapping(target = "itemName", ignore = true)
    VisitItem toEntity(VisitItemCreateDTO dto);


    // ========== UPDATE DTO → ENTITY ==========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "serviceItem", source = "serviceItemId", qualifiedByName = "mapService")
    @Mapping(target = "pack", source = "packId", qualifiedByName = "mapPack")
    @Mapping(target = "itemType", ignore = true)
    @Mapping(target = "itemName", ignore = true)
    VisitItem toEntity(VisitItemUpdateDTO dto);


    // ========== ID → ENTITY HELPERS ==========
    @Named("mapService")
    default ServiceItem mapService(Long id) {
        if (id == null) return null;
        ServiceItem service = new ServiceItem();
        service.setId(id);
        return service;
    }

    @Named("mapPack")
    default Pack mapPack(Long id) {
        if (id == null) return null;
        Pack pack = new Pack();
        pack.setId(id);
        return pack;
    }
}