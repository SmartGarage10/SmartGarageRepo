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

        Optional<Visit> getVisitById(int visitId);
        List<Visit> getVisitsByVehicleId(int vehicleId);
        List<Visit> getVisitsByDate(LocalDateTime dateTime);

        Visit createVisit(User user, Visit visit);
        Visit update(User user, int visitId, Visit changes);

        void deleteVisit(User user ,int visitId);
        void deleteVisits(User user, List<Integer> ids);
}
