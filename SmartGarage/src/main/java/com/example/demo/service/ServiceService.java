package com.example.demo.service;

import com.example.demo.models.ServiceItem;
import com.example.demo.models.ServiceOrder;

import java.util.List;
import java.util.Optional;

public interface ServiceService {
    List<ServiceItem> allServices();
    List<ServiceItem> filterServices(String name, Double minPrice, Double maxPrice);

    Optional<ServiceItem> getServiceById(int serviceId);

    ServiceItem updateService(int serviceId, ServiceItem updateServiceItem);
    void deleteService(int serviceId);

}
