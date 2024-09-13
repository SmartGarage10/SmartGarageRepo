package com.example.demo.service;

import com.example.demo.models.ServiceItem;

import java.util.List;

public interface ServiceService {
    List<ServiceItem> allServices();
    List<ServiceItem> filterServices(String name, Double minPrice, Double maxPrice);

    ServiceItem getServiceById(int serviceId);
    ServiceItem updateService(int serviceId, ServiceItem updateServiceItem);
    void deleteService(int serviceId);
    ServiceItem createService(ServiceItem newServiceItem);

}
