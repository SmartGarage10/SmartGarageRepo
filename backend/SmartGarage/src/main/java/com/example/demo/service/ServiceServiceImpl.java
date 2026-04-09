package com.example.demo.service;

import com.example.demo.DTO.serviceItem.ServiceItemCreateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import com.example.demo.DTO.serviceItem.ServiceItemUpdateDTO;
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
    public List<ServiceItemResponseDTO> getAllServices(MultiValueMap<String, String> allParams) {
        List<ServiceItem> serviceItems = entityHelper.getAll(allParams);
        return serviceItems.stream().map(serviceMapper::toDto).toList();
    }

    // ========== CREATE ==========

    @Override
    public ServiceItemResponseDTO createNewService(User user, ServiceItemCreateDTO serviceDTO) {
        if (serviceRepository.existsByServiceName(serviceDTO.serviceName())) {
            throw new ResourceConflictException(
                    String.format("Service with name %s already exists", serviceDTO.serviceName())
            );
        }

        ServiceItem entity = serviceMapper.toEntity(serviceDTO);
        serviceRepository.save(entity);

        return serviceMapper.toDto(entity);
    }

    // ========== UPDATE ==========

    @Override
    public ServiceItemResponseDTO update(Long serviceId, ServiceItemUpdateDTO changes) {
        ServiceItem serviceDetails = serviceMapper.toEntity(changes);
        ServiceItem existingService = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Service with id %s not found", serviceId)
                        ));

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

        entityHelper.update(
                serviceId,
                existingService,
                duplicateCheckFields,
                nonDuplicateCheckFields,
                ServiceItem::getId
        );

        return serviceMapper.toDto(existingService);
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
}
