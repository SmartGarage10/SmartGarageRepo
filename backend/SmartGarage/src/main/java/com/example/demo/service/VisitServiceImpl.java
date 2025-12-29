package com.example.demo.service;

import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.Filter;
import com.example.demo.filter.VisitSpecification;
import com.example.demo.filter.FilterHelper;
import com.example.demo.helpers.GenericFieldAccessor;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class VisitServiceImpl implements VisitService{

    private final RestrictHelper restrictHelper;
    private final FilterHelper filterHelper;
    private final VisitRepository visitRepository;
    private final ServiceRepository serviceItemRepository;
    private final PackRepository packRepository;

    public VisitServiceImpl(RestrictHelper restrictHelper,
                            FilterHelper filterHelper,
                            VisitRepository visitRepository,
                            ServiceRepository serviceItemRepository,
                            PackRepository packRepository) {
        this.restrictHelper = restrictHelper;
        this.filterHelper = filterHelper;
        this.visitRepository = visitRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.packRepository = packRepository;
    }


    @Override
    public List<Visit> getAllVisits(MultiValueMap<String, String> allParams) {
        Specification<Visit> spec = Specification.where(null);
        VisitSpecification visitSpecification = new VisitSpecification();

        // 1. Apply search filter if present
        if (allParams.containsKey("search")) {
            String search = allParams.getFirst("search");
            if (search != null && !search.trim().isEmpty()) {
                Specification<Visit> searchSpec = visitSpecification.createSearchSpecification(search, "name");
                spec = spec.and(searchSpec);
            }
        }

        // 2. Apply other filters
        if (allParams.containsKey("filters")) {
            List<Filter> filters = filterHelper.convertToFilters(allParams);
            if (filters != null && !filters.isEmpty()) {
                Specification<Visit> filterSpec = visitSpecification.createSpecification(filters);
                spec = spec.and(filterSpec);
            }
        }

        return visitRepository.findAll(spec);
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
    @Transactional
    public Visit createVisit(User user, Visit visit) {
        restrictHelper.isUserAdminOrEmployee(user);

        if (visitRepository.existsByVisitDate(visit.getVisitDate())) {
            throw new ResourceConflictException(String.format("Visit with id %s is already exists", visit.getVisitDate()));
        }

        // Validate and calculate total for visit items
        validateAndProcessVisitItems(visit);

        // Calculate total amount from visit items
        visit.calculateTotal();

        return visitRepository.save(visit);
    }

    @Override
    @Transactional
    public Visit update(User user, Long visitId, Visit changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Find existing visit
        Visit existingVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Visit with id %s not found", visitId)));

        // 3. Get all visits (for duplicate checking)
        List<Visit> allVisits = visitRepository.findAll();

        // 4. Process all updatable fields
        Stream.of(
                        Map.entry("visitDate", changes.getVisitDate())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingVisit, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .filter(newValue -> !GenericFieldAccessor.isDuplicateField(allVisits, entry.getKey(), newValue, visitId))
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingVisit, entry.getKey(), newValue));
                });

        // 5. Process fields without duplicate checking
        Stream.of(
                        Map.entry("employee", changes.getEmployee()),
                        Map.entry("status", changes.getStatus()),
                        Map.entry("currency", changes.getCurrency())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingVisit, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingVisit, entry.getKey(), newValue));
                });

        // 6. Handle vehicle update
        if (changes.getVehicle() != null) {
            existingVisit.setVehicle(changes.getVehicle());
        }

        // 7. Handle visit items update (NEW LOGIC)
        if (changes.getVisitItems() != null) {
            // Clear existing items
            existingVisit.getVisitItems().clear();

            // Add new items
            for (VisitItem item : changes.getVisitItems()) {
                item.setVisit(existingVisit);
                validateVisitItem(item);
                existingVisit.getVisitItems().add(item);
            }
        }

        // 8. Recalculate amount from visit items
        existingVisit.calculateTotal();

        return visitRepository.save(existingVisit);
    }

    @Override
    @Transactional
    public void deleteVisit(User user, Long visitId) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Check if the target user exists
        Visit targetVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Visit with id %s not found", visitId)));

        // 3. Perform deletion (cascade will delete visit items)
        visitRepository.delete(targetVisit);
    }

    @Override
    @Transactional
    public void deleteVisits(User user, List<Long> ids) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Perform deletion
        visitRepository.deleteAllById(ids);
    }

    // ========== HELPER METHODS ==========

    private void validateAndProcessVisitItems(Visit visit) {
        if (visit.getVisitItems() == null || visit.getVisitItems().isEmpty()) {
            return;
        }

        for (VisitItem item : visit.getVisitItems()) {
            item.setVisit(visit); // Set the bidirectional relationship
            validateVisitItem(item);
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
            if (item.getPrice() == null) item.setPrice(item.getServiceItem().getPrice());
            if (item.getItemName() == null) item.setItemName(item.getServiceItem().getServiceName());
        } else {
            item.setItemType(VisitItem.ItemType.PACK);
            if (item.getPrice() == null) item.setPrice(
                    item.getPack().getAmount() != null ? item.getPack().getAmount() : BigDecimal.ZERO
            );
            if (item.getItemName() == null) item.setItemName(item.getPack().getPackName());
        }

        // Default quantity
        if (item.getQuantity() == null) {
            item.setQuantity(1);
        }
    }

}