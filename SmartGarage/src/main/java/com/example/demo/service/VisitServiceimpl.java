package com.example.demo.service;

import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.Visit;
import com.example.demo.repositories.VisitRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitServiceimpl implements VisitService{

    private final VisitRepository repository;

    public VisitServiceimpl(VisitRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Visit> getAllVisits() {
        return repository.findAll();
    }

    @Override
    public Optional<Visit> getVisitById(int visitId) {
        return repository.findById(visitId);
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
    public Visit createVisit(Visit visit) {
        return repository.save(visit);
    }


    @Override
    public void deleteVisit(int visitId) {
        repository.findById(visitId)
                .orElseThrow(() -> new NotFoundException("Visit with ID " + visitId + " not found"));
        repository.deleteById(visitId);
    }

}
