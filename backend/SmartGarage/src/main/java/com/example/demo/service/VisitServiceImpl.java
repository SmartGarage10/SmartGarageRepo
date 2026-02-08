package com.example.demo.service;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.filter.VisitSpecification;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.EntityServiceHelper;
import com.example.demo.helpers.FieldUpdateHelper;
import com.example.demo.mappers.VisitMapper;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class VisitServiceImpl implements VisitService, EntitySpecificationProvider<Visit> {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final EntityServiceHelper<Visit, Long, VisitRepository> entityHelper;
    private final VisitSpecification visitSpecification;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceItemRepository;
    private final PackRepository packRepository;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository,
                            VisitMapper visitMapper,
                            VehicleRepository vehicleRepository,
                            UserRepository userRepository,
                            ServiceRepository serviceItemRepository,
                            PackRepository packRepository,
                            FilterHelper filterHelper,
                            VisitSpecification visitSpecification) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.packRepository = packRepository;
        this.visitSpecification = visitSpecification;

        // Create EntityServiceHelper instance
        this.entityHelper = new EntityServiceHelper<>(
                visitRepository,
                filterHelper,
                Visit.class,
                this  // Pass 'this' as specification provider
        );
    }

    // ========== SPECIFICATION PROVIDER IMPLEMENTATION ==========
    @Override
    public Specification<Visit> createFilterSpecification(List<Filter> filters) {
        return visitSpecification.createSpecification(filters);
    }

    @Override
    public Specification<Visit> createSearchSpecification(String search) {
        String searchField = getSearchField();
        return visitSpecification.createSearchSpecification(search, searchField);
    }

    @Override
    public String getSearchField() {
        return "vehicle.client.name";
    }

    // ========== SERVICE METHODS USING HELPER ==========
    @Override
    public List<Visit> getAllVisits(MultiValueMap<String, String> allParams) {
        // Use entityHelper.getAll() - specifications are handled automatically
        return entityHelper.getAll(allParams);
    }

    @Override
    public Optional<Visit> getVisitById(Long visitId) {
        return visitRepository.findById(visitId);
    }

    @Override
    public List<Visit> getVisitsByVehicleId(Long vehicleId) {
        return visitRepository.findByVehicle_Id(vehicleId);
    }

    @Override
    public List<Visit> getVisitsByDate(LocalDateTime dateTime) {
        return visitRepository.findByVisitDate(dateTime);
    }

    @Override
    public Visit createVisit(User user, VisitDTO visitDTO) {
        // Check if visit already exists with the same date
        if (visitRepository.existsByVisitDate(visitDTO.getVisitDate())) {
            throw new ResourceConflictException(String.format("Visit with date %s already exists", visitDTO.getVisitDate()));
        }

        // Map DTO to entity
        Visit visit = visitMapper.toEntity(visitDTO);

        // Fetch and set related entities
        if (visitDTO.getVehicle() != null && visitDTO.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(visitDTO.getVehicle().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            visit.setVehicle(vehicle);
        }

        if (visitDTO.getEmployee() != null && visitDTO.getEmployee().getId() != null) {
            User employee = userRepository.findById(visitDTO.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
            visit.setEmployee(employee);
        }

        // Validate and process visit items
        validateAndProcessVisitItems(visit);

        // Calculate total amount from visit items
        visit.calculateTotal();

        return visitRepository.save(visit);
    }

    @Override
    public Visit update(User user, Long visitId, VisitDTO changes) {
        // 1. Find existing visit
        Visit existingVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Visit with id %s not found", visitId)));

        // 2. Prepare FieldUpdateHelper lists
        List<FieldUpdateHelper<Visit, ?>> duplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper<>("visitDate",
                        Visit::getVisitDate,
                        Visit::setVisitDate,
                        changes.getVisitDate())
        );

        List<FieldUpdateHelper<Visit, ?>> nonDuplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper<>("employee",
                        Visit::getEmployee,
                        Visit::setEmployee,
                        changes.getEmployee()),
                new FieldUpdateHelper<>("status",
                        Visit::getStatus,
                        Visit::setStatus,
                        changes.getStatus()),
                new FieldUpdateHelper<>("currency",
                        Visit::getCurrency,
                        Visit::setCurrency,
                        changes.getCurrency()),
                new FieldUpdateHelper<>("vehicle",
                        Visit::getVehicle,
                        Visit::setVehicle,
                        changes.getVehicle())
        );

        // 3. Use entityHelper.update()
        Visit updatedVisit = entityHelper.update(
                visitId,
                existingVisit,
                duplicateCheckFields,
                nonDuplicateCheckFields,
                Visit::getId
        );

        // 4. Handle visit items update if provided
        if (changes.getVisitItems() != null && !changes.getVisitItems().isEmpty()) {
            // Clear existing items
            updatedVisit.getVisitItems().clear();

            // Add new items
            for (VisitItem item : changes.getVisitItems()) {
                item.setVisit(updatedVisit);
                validateVisitItem(item);
                updatedVisit.getVisitItems().add(item);
            }

            // Recalculate total
            updatedVisit.calculateTotal();
        }

        return visitRepository.save(updatedVisit);
    }

    @Override
    public void deleteVisit(User user, Long visitId) {
        // 1. Use entityHelper.delete()
        entityHelper.delete(visitId);
    }

    @Override
    public void deleteVisits(User user, List<Long> ids) {
        // 1. Use entityHelper.deleteAllById()
        entityHelper.deleteAllById(ids);
    }

    // ========== HELPER METHODS ==========

    private void validateAndProcessVisitItems(Visit visit) {
        if (visit.getVisitItems() == null || visit.getVisitItems().isEmpty()) {
            return;
        }

        for (VisitItem item : visit.getVisitItems()) {
            item.setVisit(visit); // Set the bidirectional relationship
            validateVisitItem(item);

            // Fetch and set related entities if only IDs are provided
            if (item.getServiceItem() != null && item.getServiceItem().getId() != null) {
                ServiceItem service = serviceItemRepository.findById(item.getServiceItem().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                item.setServiceItem(service);
            }

            if (item.getPack() != null && item.getPack().getId() != null) {
                Pack pack = packRepository.findById(item.getPack().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Pack not found"));
                item.setPack(pack);
            }
        }
    }

    private void validateVisitItem(VisitItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Visit item cannot be null");
        }

        // Ensure exactly one of service or pack is set
        if (item.getServiceItem() == null && item.getPack() == null) {
            throw new ResourceNotFoundException("Visit item must have either a service or a pack");
        }
        if (item.getServiceItem() != null && item.getPack() != null) {
            throw new ResourceConflictException("Visit item cannot have both service and pack");
        }

        // Set item type and defaults
        if (item.getServiceItem() != null) {
            item.setItemType(VisitItem.ItemType.SERVICE);
            if (item.getPrice() == null && item.getServiceItem().getPrice() != null) {
                item.setPrice(item.getServiceItem().getPrice());
            }
            if (item.getItemName() == null && item.getServiceItem().getServiceName() != null) {
                item.setItemName(item.getServiceItem().getServiceName());
            }
        } else {
            item.setItemType(VisitItem.ItemType.PACK);
            if (item.getPrice() == null && item.getPack().getAmount() != null) {
                item.setPrice(item.getPack().getAmount());
            }
            if (item.getItemName() == null && item.getPack().getPackName() != null) {
                item.setItemName(item.getPack().getPackName());
            }
        }

        // Default quantity
        if (item.getQuantity() == null) {
            item.setQuantity(1);
        }

        // Validate price
        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Visit item must have a valid price greater than 0");
        }
    }
}