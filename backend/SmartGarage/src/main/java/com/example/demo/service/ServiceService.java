package com.example.demo.service;

import com.example.demo.DTO.serviceItem.ServiceItemCreateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import com.example.demo.DTO.serviceItem.ServiceItemUpdateDTO;
import com.example.demo.models.User;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface ServiceService {
    List<ServiceItemResponseDTO> getAllServices(MultiValueMap<String, String> allParams);

    ServiceItemResponseDTO createNewService(ServiceItemCreateDTO serviceItem);
    ServiceItemResponseDTO update(Long serviceId, ServiceItemUpdateDTO serviceItem);

    void deleteService(User user, Long serviceId);
    void deleteServices(User user, List<Long> ids);
}
