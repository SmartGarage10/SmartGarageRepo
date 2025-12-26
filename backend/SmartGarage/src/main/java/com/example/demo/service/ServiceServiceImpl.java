package com.example.demo.service;

import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.Filter;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.ServiceSpecifications;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.GenericFieldAccessor;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository serviceRepository;
    private final RestrictHelper restrictHelper;
    private final FilterHelper filterHelper;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository,
                              RestrictHelper restrictHelper,
                              FilterHelper filterHelper) {
        this.serviceRepository = serviceRepository;
        this.restrictHelper = restrictHelper;
        this.filterHelper = filterHelper;
    }

    @Override
    public List<ServiceItem> getAllServices(MultiValueMap<String, String> allParams) {
        Specification<ServiceItem> spec = Specification.where(null);
        ServiceSpecifications serviceSpecifications = new ServiceSpecifications();

        // 1. Apply search filter if present
        if (allParams.containsKey("search")) {
            String search = allParams.getFirst("search");
            if (search != null && !search.trim().isEmpty()) {
                Specification<ServiceItem> searchSpec = serviceSpecifications.createSearchSpecification(search, "serviceName");
                spec = spec.and(searchSpec);
            }
        }

        // 2. Apply other filters
        if (allParams.containsKey("filters")) {
            List<Filter> filters = filterHelper.convertToFilters(allParams);
            if (filters != null && !filters.isEmpty()) {
                Specification<ServiceItem> filterSpec = serviceSpecifications.createSpecification(filters);
                spec = spec.and(filterSpec);
            }
        }

        return serviceRepository.findAll(spec);
    }

    @Override
    public Optional<ServiceItem> getServiceById(int serviceId) {
        return serviceRepository.findById(serviceId);
    }

    @Override
    public ServiceItem createNewService(User user, ServiceItem serviceItem) {
        restrictHelper.isUserAdminOrEmployee(user);

        if (serviceRepository.existsByServiceName(serviceItem.getServiceName())) {
            throw new ResourceConflictException(String.format("Service with name %s already exists", serviceItem.getServiceName()));
        }

        return serviceRepository.save(serviceItem);
    }

    @Override
    public ServiceItem update(User user, int serviceId, ServiceItem changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Find existing service
        ServiceItem existingServiceItem = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service", "id", String.valueOf(serviceId)));

        // 3. Get all vehicles (for duplicate checking)
        List<ServiceItem> allServices = serviceRepository.findAll();

        // 4. Process all updatable fields
        Stream.of(
                        Map.entry("serviceName", changes.getServiceName()),
                        Map.entry("serviceDescription", changes.getServiceDescription())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingServiceItem, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .filter(newValue -> !GenericFieldAccessor.isDuplicateField(allServices, entry.getKey(), newValue, serviceId))
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingServiceItem, entry.getKey(), newValue));
                });

        // 5. Process fields without duplicate checking
        Stream.of(
                        Map.entry("price", changes.getPrice())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingServiceItem, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingServiceItem, entry.getKey(), newValue));
                });

        // 6. Save and return updated service
        return serviceRepository.save(existingServiceItem);
    }

    @Override
    public void deleteService(User user, int serviceId) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Check if the target user exists
        ServiceItem targetServiceItem = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Service with id %s not found", serviceId)));

        // 3. Perform deletion
        serviceRepository.delete(targetServiceItem);
    }

    @Override
    public void deleteServices(User user, List<Integer> ids) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Perform deletion
        serviceRepository.deleteAllById(ids);
    }
}
