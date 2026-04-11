package com.example.demo.service;

import com.example.demo.DTO.visit.VisitCreateDTO;
import com.example.demo.DTO.visit.VisitResponseDTO;
import com.example.demo.DTO.visit.VisitUpdateDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.filter.FilterHelper;
import com.example.demo.filter.VisitSpecification;
import com.example.demo.helpers.EntityServiceHelper;
import com.example.demo.helpers.FieldUpdateHelper;
import com.example.demo.mappers.VisitItemMapper;
import com.example.demo.mappers.VisitMapper;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

@Service
public class VisitServiceImpl implements VisitService, EntitySpecificationProvider<Visit> {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final VisitItemMapper visitItemMapper;
    private final EntityServiceHelper<Visit, Long, VisitRepository> entityHelper;
    private final VisitSpecification visitSpecification;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VisitItemService visitItemService;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository,
                            VisitMapper visitMapper,
                            VisitItemMapper visitItemMapper,
                            VehicleRepository vehicleRepository,
                            UserRepository userRepository,
                            VisitItemService visitItemService,
                            FilterHelper filterHelper,
                            VisitSpecification visitSpecification) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
        this.visitItemMapper = visitItemMapper;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.visitItemService = visitItemService;
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
    public List<VisitResponseDTO> getAllVisits(MultiValueMap<String, String> allParams) {
        List<Visit> visits = entityHelper.getAll(allParams);
        return visits.stream().map(visitMapper::toDto).toList();
    }

    @Override
    public VisitResponseDTO createVisit(VisitCreateDTO visitDTO) {
        if (visitRepository.existsByVisitDate(visitDTO.visitDate())) {
            throw new ResourceConflictException("Visit already exists for this date");
        }

        Visit visit = visitMapper.toEntity(visitDTO);

        visit.setVehicle(
                vehicleRepository.findById(visitDTO.vehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"))
        );

        visit.setEmployee(
                userRepository.findById(visitDTO.employeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found"))
        );

        if (visitDTO.visitItems() != null && !visitDTO.visitItems().isEmpty()) {

            List<VisitItem> items = visitDTO.visitItems()
                    .stream()
                    .map(itemDto -> visitItemService.fromCreateDTO(itemDto, visit))
                    .toList();

            visit.getVisitItems().clear();
            visit.getVisitItems().addAll(items);
        }

        visit.calculateTotal();

        return visitMapper.toDto(visitRepository.save(visit));
    }

    @Override
    public VisitResponseDTO update(Long visitId, VisitUpdateDTO changes) {

        Visit existingVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        Vehicle vehicle = vehicleRepository.findById(changes.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        User employee = userRepository.findById(changes.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        List<FieldUpdateHelper<Visit, ?>> duplicateCheckFields = List.of(
                new FieldUpdateHelper<>(
                        "visitDate",
                        Visit::getVisitDate,
                        Visit::setVisitDate,
                        changes.visitDate()
                )
        );

        List<FieldUpdateHelper<Visit, ?>> nonDuplicateCheckFields = Arrays.asList(
                new FieldUpdateHelper<>("employee", Visit::getEmployee, Visit::setEmployee, employee),
                new FieldUpdateHelper<>("status", Visit::getStatus, Visit::setStatus, changes.status()),
                new FieldUpdateHelper<>("currency", Visit::getCurrency, Visit::setCurrency, changes.currency()),
                new FieldUpdateHelper<>("vehicle", Visit::getVehicle, Visit::setVehicle, vehicle)
        );

        Visit updated = entityHelper.update(
                visitId,
                existingVisit,
                duplicateCheckFields,
                nonDuplicateCheckFields,
                Visit::getId
        );

        if (changes.visitItems() != null) {

            updated.getVisitItems().clear();

            List<VisitItem> items = changes.visitItems()
                    .stream()
                    .map(itemDto -> visitItemService.fromUpdateDTO(itemDto, updated))
                    .toList();

            updated.getVisitItems().addAll(items);
        }

        updated.calculateTotal();

        return visitMapper.toDto(visitRepository.save(updated));

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
}