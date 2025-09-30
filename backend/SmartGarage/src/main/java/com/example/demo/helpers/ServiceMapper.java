package com.example.demo.helpers;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.DTO.VehicleDTO;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    public ServiceItem fromDto(int id, ServiceDTO serviceDTO){
        ServiceItem serviceItem = fromDto(serviceDTO);
        serviceItem.setServiceId(id);

        return serviceItem;
    }

    public ServiceItem fromDto(ServiceDTO serviceDTO){
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setServiceName(serviceDTO.getServiceName());
        serviceItem.setServiceDescription(serviceDTO.getServiceDescription());
        serviceItem.setPrice(serviceDTO.getPrice());
        return serviceItem;
    }
//    public ServiceDTO toDto(ServiceItem serviceItem){
//        return new ServiceDTO(
//                serviceItem.getServiceId(),
//                serviceItem.getServiceName(),
//                serviceItem.getServiceDescription(),
//                serviceItem.getPrice()
//        );
//    }
}
