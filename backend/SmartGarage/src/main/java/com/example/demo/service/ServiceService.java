package com.example.demo.service;

import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface ServiceService {
    List<ServiceItem> getAllServices(MultiValueMap<String, String> allParams);
    Optional<ServiceItem> getServiceById(Long serviceId);

    ServiceItem createNewService(User user, ServiceItem serviceItem);
    ServiceItem update(User user, Long serviceId, ServiceItem serviceItem);

    void deleteService(User user, Long serviceId);
    void deleteServices(User user, List<Long> ids);
}
