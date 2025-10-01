package com.example.demo.helpers;

import com.example.demo.DTO.PackDTO;
import com.example.demo.DTO.ServiceDTO;
import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackMapper {
    private final ServiceMapper serviceMapper;

    @Autowired
    public PackMapper(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    public Pack fromDto(int id, PackDTO packageDTO){
        Pack pack = fromDto(packageDTO);
        pack.setId(id);
        return pack;
    }

    public Pack fromDto(PackDTO packDTO){
        Pack pack = new Pack();
        pack.setPackName(packDTO.getPackName().toUpperCase());
        pack.setDescription(packDTO.getDescription());
        pack.setAmount(packDTO.getAmount());
        pack.setServices(packDTO.getServices());

        return pack;
    }

    // You might also need this method for converting Pack to PackDTO
//    public PackDTO toDto(Pack pack) {
//        PackDTO dto = new PackDTO();
//        dto.setId(pack.getId());
//        dto.setPackName(pack.getPackName());
//        dto.setDescription(pack.getDescription());
//        dto.setAmount(pack.getAmount());
//
//        if (pack.getServices() != null) {
//            List<ServiceDTO> serviceDTOs = pack.getServices().stream()
//                    .map(serviceMapper::toDto) // Convert each ServiceItem to ServiceDTO
//                    .collect(Collectors.toList());
//            dto.setServices(serviceDTOs);
//        }
//
//        return dto;
//    }
}