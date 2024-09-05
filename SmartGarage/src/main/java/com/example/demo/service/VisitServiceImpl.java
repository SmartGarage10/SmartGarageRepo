package com.example.demo.service;


import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.User;
import com.example.demo.models.Visit;
import com.example.demo.repositories.VisitRepository;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VisitServiceImpl implements VisitService{

    private final VisitRepository repository;

    public VisitServiceImpl(VisitRepository repository) {
        this.repository = repository;
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
