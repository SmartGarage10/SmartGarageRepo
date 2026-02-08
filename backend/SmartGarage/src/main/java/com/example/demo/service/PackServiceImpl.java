package com.example.demo.service;

import com.example.demo.DTO.PackDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.EntityServiceHelper;
import com.example.demo.helpers.FieldUpdateHelper;
import com.example.demo.mappers.PackMapper;
import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.repositories.PackRepository;
import com.example.demo.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PackServiceImpl implements PackService, EntitySpecificationProvider<Pack> {
    private final PackRepository packRepository;
    private final ServiceRepository serviceRepository;
    private final PackMapper packMapper;
    private final EntityServiceHelper<Pack, Long, PackRepository> entityHelper;

    @Autowired
    public PackServiceImpl(PackRepository packRepository,
                           ServiceRepository serviceRepository,
                           PackMapper packMapper,
                           FilterHelper filterHelper) {
        this.packRepository = packRepository;
        this.serviceRepository = serviceRepository;
        this.packMapper = packMapper;
        this.entityHelper = new EntityServiceHelper<>(
                packRepository,
                filterHelper,
                Pack.class,
                this
        );
    }

    // ========== SPECIFICATION PROVIDER ==========

    @Override
    public Specification<Pack> createFilterSpecification(List<Filter> filters) {
        return null;
    }

    @Override
    public Specification<Pack> createSearchSpecification(String search) {
        return null;
    }

    @Override
    public String getSearchField() {
        return "";
    }

    // ========== READ ==========

    @Override
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    // ========== CREATE ==========

    @Override
    public Pack createPack(User user, PackDTO packDTO) {
        if (packRepository.existsPacksByPackName(packDTO.getPackName())) {
            throw new ResourceConflictException(
                    String.format("Pack with name %s already exists", packDTO.getPackName())
            );
        }

        Pack pack = packMapper.toEntity(packDTO);
        // Use helper method to resolve services
        List<ServiceItem> managedServices = resolveAndValidateServices(pack.getServices());
        pack.setServices(managedServices);

        validate(pack);
        return packRepository.save(pack);
    }

    // ========== UPDATE ==========

    @Override
    public Pack update(User user, Long packId, PackDTO changes) {
        Pack existing = packRepository.findById(packId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Pack with id %s not found", packId)
                ));

        Pack packDetails = packMapper.toEntity(changes);

        List<FieldUpdateHelper<Pack, ?>> duplicateCheckFields = List.of(
                new FieldUpdateHelper<>(
                        "packName",
                        Pack::getPackName,
                        Pack::setPackName,
                        packDetails.getPackName()
                )
        );

        List<FieldUpdateHelper<Pack, ?>> nonDuplicateCheckFields = List.of(
                new FieldUpdateHelper<>(
                        "description",
                        Pack::getDescription,
                        Pack::setDescription,
                        packDetails.getDescription()
                ),
                new FieldUpdateHelper<>(
                        "amount",
                        Pack::getAmount,
                        Pack::setAmount,
                        packDetails.getAmount()
                )
        );

        Pack updated = entityHelper.update(
                packId,
                existing,
                duplicateCheckFields,
                nonDuplicateCheckFields,
                Pack::getId
        );

        // Handle services using helper method
        if (packDetails.getServices() != null) {
            List<ServiceItem> managedServices = resolveAndValidateServices(packDetails.getServices());
            updated.getServices().clear();
            updated.getServices().addAll(managedServices);
        }

        validate(updated);
        return packRepository.save(updated);
    }

    // ========== DELETE ==========

    @Override
    public void deletePack(User user, Long packId) {
        entityHelper.delete(packId);
    }

    @Override
    public void deletePacks(User user, List<Long> ids) {
        entityHelper.deleteAllById(ids);
    }

    // ========== VALIDATION ==========

    private void validate(Pack pack) {
        if (pack.getPackName() == null || pack.getPackName().isBlank()) {
            throw new IllegalArgumentException("Pack name is required");
        }

        if (pack.getAmount() == null || pack.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Pack amount must be greater than 0");
        }

        if (pack.getServices() != null) {
            for (ServiceItem service : pack.getServices()) {
                if (!serviceRepository.existsById(service.getId())) {
                    throw new ResourceNotFoundException(
                            String.format("Service with id %s does not exist", service.getId())
                    );
                }
            }
        }
    }

    // ========== PRIVATE HELPER METHOD (EXTRACTED AT BOTTOM) ==========

    private List<ServiceItem> resolveAndValidateServices(List<ServiceItem> requestedServices) {
        if (requestedServices == null || requestedServices.isEmpty()) {
            return new ArrayList<>();
        }

        List<ServiceItem> managedServices = new ArrayList<>();
        Set<String> addedServiceNames = new HashSet<>();

        for (ServiceItem service : requestedServices) {
            ServiceItem managedService = serviceRepository.findByServiceName(service.getServiceName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Service %s not found", service.getServiceName())));

            // Check for duplicate service in the request
            if (!addedServiceNames.add(managedService.getServiceName())) {
                throw new ResourceConflictException(
                        String.format("Duplicate service %s in the pack", managedService.getServiceName())
                );
            }

            managedServices.add(managedService);
        }

        return managedServices;
    }
}