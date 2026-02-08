package com.example.demo.service;

import com.example.demo.DTO.ServiceItemDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.EntityServiceHelper;
import com.example.demo.helpers.FieldUpdateHelper;
import com.example.demo.mappers.ServiceItemMapper;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
public class ServiceServiceImpl
        implements ServiceService, EntitySpecificationProvider<ServiceItem> {

    private final ServiceRepository serviceRepository;
    private final ServiceItemMapper serviceMapper;
    private final EntityServiceHelper<ServiceItem, Long, ServiceRepository> entityHelper;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository,
                              ServiceItemMapper serviceMapper,
                              FilterHelper filterHelper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;

        this.entityHelper = new EntityServiceHelper<>(
                serviceRepository,
                filterHelper,
                ServiceItem.class,
                this
        );
    }

    // ========== SPECIFICATION PROVIDER ==========

    @Override
    public Specification<ServiceItem> createFilterSpecification(List<Filter> filters) {
        return null;
    }

    @Override
    public Specification<ServiceItem> createSearchSpecification(String search) {
        return null;
    }

    @Override
    public String getSearchField() {
        return "";
    }

    // ========== READ ==========

    @Override
    public List<ServiceItem> getAllServices(MultiValueMap<String, String> allParams) {
        return entityHelper.getAll(allParams);
    }

    // ========== CREATE ==========

    @Override
    public ServiceItem createNewService(User user, ServiceItemDTO serviceDTO) {
        if (serviceRepository.existsByServiceName(serviceDTO.getServiceName())) {
            throw new ResourceConflictException(
                    String.format("Service with name %s already exists", serviceDTO.getServiceName())
            );
        }
        ServiceItem serviceItem = serviceMapper.toEntity(serviceDTO);
        validate(serviceItem);
        return serviceRepository.save(serviceItem);
    }

    // ========== UPDATE ==========

    @Override
    public ServiceItem update(User user, Long serviceId, ServiceItemDTO changes) {
        ServiceItem existing = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Service with id %s not found", serviceId)
                        ));
        ServiceItem serviceDetails = serviceMapper.toEntity(changes);

        List<FieldUpdateHelper<ServiceItem, ?>> duplicateCheckFields = List.of(
                new FieldUpdateHelper<>(
                        "serviceName",
                        ServiceItem::getServiceName,
                        ServiceItem::setServiceName,
                        serviceDetails.getServiceName()
                )
        );

        List<FieldUpdateHelper<ServiceItem, ?>> nonDuplicateCheckFields = List.of(
                new FieldUpdateHelper<>(
                        "serviceDescription",
                        ServiceItem::getServiceDescription,
                        ServiceItem::setServiceDescription,
                        serviceDetails.getServiceDescription()
                ),
                new FieldUpdateHelper<>(
                        "price",
                        ServiceItem::getPrice,
                        ServiceItem::setPrice,
                        serviceDetails.getPrice()
                )
        );

        ServiceItem updated = entityHelper.update(
                serviceId,
                existing,
                duplicateCheckFields,
                nonDuplicateCheckFields,
                ServiceItem::getId
        );

        validate(updated);
        return updated;
    }

    // ========== DELETE ==========

    @Override
    public void deleteService(User user, Long serviceId) {
        entityHelper.delete(serviceId);
    }

    @Override
    public void deleteServices(User user, List<Long> ids) {
        entityHelper.deleteAllById(ids);
    }

    // ========== VALIDATION ==========

    private void validate(ServiceItem serviceItem) {
        if (serviceItem.getServiceName() == null || serviceItem.getServiceName().isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }

        if (serviceItem.getPrice() == null || serviceItem.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Service price must be greater than 0");
        }
    }
}
