package com.example.demo.mappers;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.models.Vehicle;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-13T09:46:43+0200",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class VehicleMapperImpl implements VehicleMapper {

    @Override
    public VehicleDTO toDto(Vehicle vehicle) {
        if ( vehicle == null ) {
            return null;
        }

        VehicleDTO vehicleDTO = new VehicleDTO();

        return vehicleDTO;
    }

    @Override
    public Vehicle toEntity(VehicleDTO vehicleDTO) {
        if ( vehicleDTO == null ) {
            return null;
        }

        Vehicle vehicle = new Vehicle();

        return vehicle;
    }
}
