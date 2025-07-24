package com.example.demo.service;


import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.filter.VisitSpecification;
import com.example.demo.models.User;
import com.example.demo.models.Visit;
import com.example.demo.repositories.VisitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitServiceImpl implements VisitService{

    private final VisitRepository repository;

    public VisitServiceImpl(VisitRepository repository) {
        this.repository = repository;
    }

    public List<Visit> filterVisits(LocalDateTime visitDate, LocalDateTime startDate, LocalDateTime endDate,
                                    Integer vehicleId){
        Specification<Visit> specification = Specification.where(null);

        if (visitDate != null){
            specification = specification.and(VisitSpecification.byDate(visitDate));
        }

        if (startDate != null && endDate != null){
            specification = specification.and(VisitSpecification.betweenDates(startDate, endDate));
        }

        if (vehicleId != null){
            specification = specification.and(VisitSpecification.byVehicleId(vehicleId));
        }

        return repository.findAll((Sort) specification);
    }

    @Override
    public List<Visit> getAllVisits() {
        return repository.findAll();
    }

    @Override
    public Optional<Visit> getVisitById(int visitId) {
        return Optional.ofNullable(repository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit with id" + visitId + "not found")));
    }

    @Override
    public List<Visit> getVisitsByVehicleId(int vehicleId) {
        return repository.findByVehicleId(vehicleId);
    }

    @Override
    public List<Visit> getVisitsByDate(LocalDateTime dateTime) {
        return repository.findByVisitDate(dateTime);
    }

    @Override
    public Visit createVisit(User user, Visit visit) {
        visit.setEmployee(user);
        return repository.save(visit);
    }


    @Override
    public void deleteVisit(User user, int visitId) {
        Visit visit = repository.findById(visitId)
                .orElseThrow(() -> new NotFoundException("Visit with ID " + visitId + " not found"));
        repository.deleteById(visitId);

        if (!userHasPermisiion(user, visit)){
            throw new AuthorizationException("User does not have permission to delete!");
        }
    }

    private boolean userHasPermisiion(User user, Visit visit){
        return user.getRole().equals("ADMIN") && user.getRole().equals("EMPLOYEE")
                || user.getId() == visit.getEmployee().getId();
    }

}
