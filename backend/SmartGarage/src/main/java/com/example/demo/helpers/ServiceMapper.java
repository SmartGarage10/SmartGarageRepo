package com.example.demo.helpers;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.models.ServiceItem;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    public ServiceItem fromDto(ServiceDTO serviceDTO){
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setServiceId(serviceDTO.getServiceId());
        serviceItem.setServiceName(serviceDTO.getServiceName());
        serviceItem.setPrice(serviceDTO.getServicePrice());
        return serviceItem;
    }
    public ServiceDTO toDto(ServiceItem serviceItem){
        return new ServiceDTO(
                serviceItem.getServiceId(),
                serviceItem.getServiceName(),
                serviceItem.getServiceDescription(),
                serviceItem.getPrice()
        );
    }
}
