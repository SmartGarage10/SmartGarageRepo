package com.example.demo.service;

import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.ServiceItem;
import com.example.demo.repositories.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository repository;

    public ServiceServiceImpl(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ServiceItem> allServices() {
        return repository.findAll();
    }

    @Override
    public List<ServiceItem> filterServices(String name, Double minPrice, Double maxPrice) {
        if (name != null && !name.isEmpty()) {
            return repository.findByServiceNameContaining(name);
        } else if (minPrice != null && maxPrice != null) {
            return repository.findByPriceBetween(minPrice, maxPrice);
        }
        return repository.findAll();
    }

    @Override
    public Optional<ServiceItem> getServiceById(int serviceId) {
        return repository.findById(serviceId);
    }

    @Override
    public ServiceItem updateService(int serviceId, ServiceItem updateServiceItem) {
        ServiceItem service = repository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service with ID" + serviceId + "not found"));

        service.setServiceName(updateServiceItem.getServiceName());
        service.setPrice(updateServiceItem.getPrice());
        return repository.save(service);
    }

    @Override
    public void deleteService(int serviceId) {
        repository.deleteById(serviceId);

    }
}
