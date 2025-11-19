package com.example.demo.service;


import com.example.demo.DTO.Filter;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.VehicleSpecifications;
import com.example.demo.filter.VisitSpecification;
import com.example.demo.helpers.FilterHelper;
import com.example.demo.helpers.GenericFieldAccessor;
import com.example.demo.helpers.RestrictHelper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.models.Visit;
import com.example.demo.repositories.VisitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.webjars.NotFoundException;

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

    public VisitServiceImpl(RestrictHelper restrictHelper,
                            FilterHelper filterHelper,
                            VisitRepository visitRepository) {
        this.restrictHelper = restrictHelper;
        this.filterHelper = filterHelper;
        this.visitRepository = visitRepository;
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
    public Optional<Visit> getVisitById(int visitId) {
        return visitRepository.findById(visitId);
    }

    @Override
    public List<Visit> getVisitsByVehicleId(int vehicleId) {
        return visitRepository.findByVehicleId(vehicleId);
    }

    @Override
    public List<Visit> getVisitsByDate(LocalDateTime dateTime) {
        return visitRepository.findByVisitDate(dateTime);
    }

    @Override
    public Visit createVisit(User user, Visit visit) {
        restrictHelper.isUserAdminOrEmployee(user);

        if (visitRepository.existsByVisitDate(visit.getVisitDate())) {
            throw new IllegalArgumentException("The visit already exists.");
        }

        return visitRepository.save(visit);
    }

    @Override
    public Visit update(User user, int visitId, Visit changes) {
        // 1. Check User permissions
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Find existing visit
        Visit existingVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit", "id", String.valueOf(visitId)));

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

        // 5. Process fields without duplicate checking - MANUAL PACK HANDLING
        if (changes.getPack() != null) {
            // If pack is provided, set it (could be null or actual pack)
            existingVisit.setPack(changes.getPack());
        } else {
            // Explicitly set to null for custom packs
            existingVisit.setPack(null);
        }

        // Process other fields
        Stream.of(
                        Map.entry("employee", changes.getEmployee()),
                        Map.entry("status", changes.getStatus())
                )
                .forEach(entry -> {
                    String currentValue = GenericFieldAccessor.getFieldValue(existingVisit, entry.getKey());
                    Optional.ofNullable(entry.getValue())
                            .filter(newValue -> !newValue.equals(currentValue)) // Skip if unchanged
                            .ifPresent(newValue -> GenericFieldAccessor.setFieldValue(existingVisit, entry.getKey(), newValue));
                });

        // visitServices handled separately because it can be null
        existingVisit.setVisitServices(changes.getVisitServices()); // can be null safely

        // 6. Update amount and currency separately
        if (changes.getAmount() != existingVisit.getAmount()) {
            existingVisit.setAmount(changes.getAmount());
        }
        if (changes.getCurrency() != null && !changes.getCurrency().equals(existingVisit.getCurrency())) {
            existingVisit.setCurrency(changes.getCurrency());
        }

        return visitRepository.save(existingVisit);
    }

    @Override
    public void deleteVisit(User user, int visitId) {
        // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Check if the target user exists
        Visit targetVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit", "id", String.valueOf(visitId)));

        // 3. Perform deletion
        visitRepository.delete(targetVisit);
    }

    @Override
    public void deleteVisits(User user, List<Integer> ids) {
         // 1. Validate permissions (admin or employee)
        restrictHelper.isUserAdminOrEmployee(user);

        // 2. Perform deletion
        visitRepository.deleteAllById(ids);
    }
}
