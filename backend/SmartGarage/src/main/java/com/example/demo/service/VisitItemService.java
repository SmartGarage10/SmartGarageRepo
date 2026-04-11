package com.example.demo.service;

import com.example.demo.DTO.visitItem.VisitItemCreateDTO;
import com.example.demo.DTO.visitItem.VisitItemUpdateDTO;
import com.example.demo.models.Visit;
import com.example.demo.models.VisitItem;

public interface VisitItemService {
    VisitItem fromCreateDTO(VisitItemCreateDTO dto, Visit visit);
    VisitItem fromUpdateDTO(VisitItemUpdateDTO dto, Visit visit);
}
