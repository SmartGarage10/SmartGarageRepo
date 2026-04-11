package com.example.demo.service;

import com.example.demo.DTO.visit.VisitCreateDTO;
import com.example.demo.DTO.visit.VisitResponseDTO;
import com.example.demo.DTO.visit.VisitUpdateDTO;
import com.example.demo.models.User;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface VisitService {
        List<VisitResponseDTO> getAllVisits(MultiValueMap<String, String> allParams);

        VisitResponseDTO createVisit(VisitCreateDTO visitDTO);
       VisitResponseDTO update(Long visitId, VisitUpdateDTO changes);

        void deleteVisit(User user ,Long visitId);
        void deleteVisits(User user, List<Long> ids);
}
