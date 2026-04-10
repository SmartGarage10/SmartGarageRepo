package com.example.demo.mappers;

import com.example.demo.DTO.serviceItem.ServiceItemCreateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import com.example.demo.DTO.serviceItem.ServiceItemUpdateDTO;
import com.example.demo.models.ServiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceName", ignore = true)
    ServiceItem toEntity(ServiceItemCreateDTO serviceItemDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceName", ignore = true)
    ServiceItem toEntity(ServiceItemUpdateDTO serviceItemDTO);

    ServiceItemResponseDTO toDto(ServiceItem serviceItem);
}
