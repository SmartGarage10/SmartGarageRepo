package com.example.demo.service;

import com.example.demo.models.Visit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitService {
        List<Visit> getAllVisits();
        Optional<Visit> getVisitById(int visitId);
        List<Visit> getVisitsByVehicleId(int vehicleId);
        List<Visit> getVisitsByDate(LocalDateTime dateTime);
        Visit createVisit(Visit visit);
        void deleteVisit(int visitId);

}
