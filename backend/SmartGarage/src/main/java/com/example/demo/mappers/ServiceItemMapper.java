package com.example.demo.mappers;

import com.example.demo.DTO.ServiceItemDTO;
import com.example.demo.models.ServiceItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceItemMapper {
    ServiceItemDTO toDto(ServiceItem serviceItem);
    ServiceItem toEntity(ServiceItemDTO serviceItemDTO);
}
