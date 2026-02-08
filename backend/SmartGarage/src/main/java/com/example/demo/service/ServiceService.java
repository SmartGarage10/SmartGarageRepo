package com.example.demo.service;

import com.example.demo.DTO.ServiceItemDTO;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface ServiceService {
    List<ServiceItem> getAllServices(MultiValueMap<String, String> allParams);

    ServiceItem createNewService(User user, ServiceItemDTO serviceItem);
    ServiceItem update(User user, Long serviceId, ServiceItemDTO serviceItem);

    void deleteService(User user, Long serviceId);
    void deleteServices(User user, List<Long> ids);
}
