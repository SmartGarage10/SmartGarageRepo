package com.example.demo.mappers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.Visit;
import com.example.demo.models.VisitItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class VisitMapperImpl implements VisitMapper {

    @Override
    public Visit toEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        Visit visit = new Visit();

        if ( visitDTO.getVehicle() != null ) {
            visit.setVehicle( visitDTO.getVehicle() );
        }
        if ( visitDTO.getEmployee() != null ) {
            visit.setEmployee( visitDTO.getEmployee() );
        }
        if ( visitDTO.getVisitDate() != null ) {
            visit.setVisitDate( visitDTO.getVisitDate() );
        }
        if ( visitDTO.getStatus() != null ) {
            visit.setStatus( visitDTO.getStatus() );
        }
        if ( visitDTO.getAmount() != null ) {
            visit.setAmount( visitDTO.getAmount() );
        }
        List<VisitItem> list = visitDTO.getVisitItems();
        if ( list != null ) {
            visit.setVisitItems( new ArrayList<VisitItem>( list ) );
        }
        if ( visitDTO.getCurrency() != null ) {
            visit.setCurrency( visitDTO.getCurrency() );
        }

        return visit;
    }
}
