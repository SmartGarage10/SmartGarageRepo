package com.example.demo.mappers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.Visit;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class VisitMapperImpl implements VisitMapper {

    @Override
    public VisitDTO toDto(Visit visit) {
        if ( visit == null ) {
            return null;
        }

        VisitDTO visitDTO = new VisitDTO();

        visitDTO.setVehicle( visit.getVehicle() );
        visitDTO.setEmployee( visit.getEmployee() );
        visitDTO.setVisitDate( visit.getVisitDate() );
        visitDTO.setStatus( visit.getStatus() );
        visitDTO.setAmount( visit.getAmount() );
        visitDTO.setCurrency( visit.getCurrency() );

        return visitDTO;
    }

    @Override
    public Visit toEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        Visit visit = new Visit();

        visit.setVehicle( visitDTO.getVehicle() );
        visit.setEmployee( visitDTO.getEmployee() );
        visit.setVisitDate( visitDTO.getVisitDate() );
        visit.setStatus( visitDTO.getStatus() );
        visit.setAmount( visitDTO.getAmount() );
        visit.setCurrency( visitDTO.getCurrency() );

        return visit;
    }
}
