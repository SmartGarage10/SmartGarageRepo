package com.example.demo.mappers;

import com.example.demo.DTO.serviceItem.ServiceItemCreateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import com.example.demo.DTO.serviceItem.ServiceItemUpdateDTO;
import com.example.demo.models.ServiceItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceItemMapper {
    ServiceItemResponseDTO toDto(ServiceItem serviceItem);

    ServiceItem toEntity(ServiceItemCreateDTO serviceItemDTO);
    ServiceItem toEntity(ServiceItemUpdateDTO serviceItemDTO);

}
