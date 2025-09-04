package com.example.demo.service;


import com.example.demo.DTO.Filter;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.VehicleSpecifications;
import com.example.demo.filter.VisitSpecification;
import com.example.demo.helpers.FilterHelper;
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
import java.util.Optional;

@Service
public class VisitServiceImpl implements VisitService{

    private final VisitRepository repository;
    private final RestrictHelper restrictHelper;
    private final FilterHelper filterHelper;
    private final VisitRepository visitRepository;

    public VisitServiceImpl(VisitRepository repository,
                            RestrictHelper restrictHelper,
                            FilterHelper filterHelper, VisitRepository visitRepository) {
        this.repository = repository;
        this.restrictHelper = restrictHelper;
        this.filterHelper = filterHelper;
        this.visitRepository = visitRepository;
    }


    @Override
    public List<Visit> getAllVisits(MultiValueMap<String, String> allParams) {
        Specification<Visit> spec = Specification.where(null);
        VisitSpecification visitSpecification = new VisitSpecification();

//        // 1. Apply search filter if present
//        if (allParams.containsKey("search")) {
//            String search = allParams.getFirst("search");
//            if (search != null && !search.trim().isEmpty()) {
//                Specification<Visit> searchSpec = visitSpecification.createSearchSpecification(search, "name");
//                spec = spec.and(searchSpec);
//            }
//        }
//
//        // 2. Apply other filters
//        if (allParams.containsKey("filters")) {
//            List<Filter> filters = filterHelper.convertToFilters(allParams);
//            if (filters != null && !filters.isEmpty()) {
//                Specification<Visit> filterSpec = visitSpecification.createSpecification(filters);
//                spec = spec.and(filterSpec);
//            }
//        }

        return visitRepository.findAll(spec);
    }

    @Override
    public Optional<Visit> getVisitById(int visitId) {
        return Optional.empty();
    }

    @Override
    public List<Visit> getVisitsByVehicleId(int vehicleId) {
        return List.of();
    }

    @Override
    public List<Visit> getVisitsByDate(LocalDateTime dateTime) {
        return List.of();
    }

    @Override
    public Visit createVisit(User user, Visit visit) {
        return null;
    }

    @Override
    public Vehicle update(User user, int visitId, Visit changes) {
        return null;
    }

    @Override
    public void deleteVisit(User user, int visitId) {

    }

    @Override
    public void deleteVisits(User user, List<Integer> ids) {

    }
}
