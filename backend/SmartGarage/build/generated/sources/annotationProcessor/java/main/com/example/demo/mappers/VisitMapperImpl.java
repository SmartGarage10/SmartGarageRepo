package com.example.demo.mappers;

import com.example.demo.DTO.visit.VisitCreateDTO;
import com.example.demo.DTO.visit.VisitResponseDTO;
import com.example.demo.DTO.visit.VisitUpdateDTO;
import com.example.demo.models.Visit;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class VisitMapperImpl implements VisitMapper {

    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VisitItemMapper visitItemMapper;

    @Override
    public VisitResponseDTO toDto(Visit visit) {
        if ( visit == null ) {
            return null;
        }

        VisitResponseDTO.VisitResponseDTOBuilder visitResponseDTO = VisitResponseDTO.builder();

        visitResponseDTO.id( visit.getId() );
        visitResponseDTO.vehicle( vehicleMapper.toDto( visit.getVehicle() ) );
        visitResponseDTO.employee( userMapper.toDTO( visit.getEmployee() ) );
        visitResponseDTO.visitItems( visitItemMapper.toDtoList( visit.getVisitItems() ) );
        visitResponseDTO.amount( visit.getAmount() );
        visitResponseDTO.visitDate( visit.getVisitDate() );
        visitResponseDTO.status( visit.getStatus() );
        visitResponseDTO.currency( visit.getCurrency() );

        return visitResponseDTO.build();
    }

    @Override
    public List<VisitResponseDTO> toDtoList(List<Visit> visits) {
        if ( visits == null ) {
            return null;
        }

        List<VisitResponseDTO> list = new ArrayList<VisitResponseDTO>( visits.size() );
        for ( Visit visit : visits ) {
            list.add( toDto( visit ) );
        }

        return list;
    }

    @Override
    public Visit toEntity(VisitCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Visit visit = new Visit();

        visit.setVisitDate( dto.visitDate() );
        visit.setStatus( dto.status() );

        visit.setCurrency( "EUR" );

        return visit;
    }

    @Override
    public Visit toEntity(VisitUpdateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Visit visit = new Visit();

        visit.setVisitDate( dto.visitDate() );
        visit.setStatus( dto.status() );

        return visit;
    }
}
