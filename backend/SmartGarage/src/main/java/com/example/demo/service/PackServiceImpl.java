package com.example.demo.service;

import com.example.demo.DTO.pack.PackCreateDTO;
import com.example.demo.DTO.pack.PackResponseDTO;
import com.example.demo.DTO.pack.PackUpdateDTO;
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
    public List<PackResponseDTO> getAllPacks() {
        List<Pack> packs =  packRepository.findAll();
        return packs.stream().map(packMapper::toDto).toList();
    }

    // ========== CREATE ==========

    @Override
    public PackResponseDTO createPack(PackCreateDTO packDTO) {
        if (packRepository.existsPacksByPackName(packDTO.packName())) {
            throw new ResourceConflictException(
                    String.format("Pack with name %s already exists", packDTO.packName())
            );
        }

        Pack pack = packMapper.toEntity(packDTO);
        // Use helper method to resolve services
        List<ServiceItem> managedServices = resolveAndValidateServices(packDTO.serviceIds());
        pack.setServices(managedServices);

        packRepository.save(pack);
        return packMapper.toDto(pack);
    }

    // ========== UPDATE ==========

    @Override
    public PackResponseDTO update(Long packId, PackUpdateDTO changes) {
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
            List<ServiceItem> managedServices = resolveAndValidateServices(changes.serviceIds());
            updated.getServices().clear();
            updated.getServices().addAll(managedServices);
        }

        packRepository.save(updated);
        return packMapper.toDto(updated);
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

    // ========== PRIVATE HELPER METHOD (EXTRACTED AT BOTTOM) ==========
    private List<ServiceItem> resolveAndValidateServices(List<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ServiceItem> managedServices = new ArrayList<>();
        Set<String> addedServiceNames = new HashSet<>();

        for (Long id : serviceIds) {
            ServiceItem managedService = serviceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Service with id %s not found", id)));

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