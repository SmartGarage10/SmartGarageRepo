package com.example.demo.mappers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.Visit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    VisitDTO visitDtoToVisit(Visit visit);
    Visit visitDtoToVisit(VisitDTO visitDTO);
}
