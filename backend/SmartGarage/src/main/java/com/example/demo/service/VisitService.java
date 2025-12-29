package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.models.Visit;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitService {
        List<Visit> getAllVisits(MultiValueMap<String, String> allParams);

        Optional<Visit> getVisitById(Long visitId);
        List<Visit> getVisitsByVehicleId(Long vehicleId);
        List<Visit> getVisitsByDate(LocalDateTime dateTime);

        Visit createVisit(User user, Visit visit);
        Visit update(User user, Long visitId, Visit changes);

        void deleteVisit(User user ,Long visitId);
        void deleteVisits(User user, List<Long> ids);
}
