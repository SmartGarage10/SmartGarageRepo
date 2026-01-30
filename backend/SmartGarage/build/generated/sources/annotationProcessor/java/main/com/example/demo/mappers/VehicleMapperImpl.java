package com.example.demo.mappers;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
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

        vehicleDTO.setVehiclePlate( vehicle.getVehiclePlate() );
        vehicleDTO.setVin( vehicle.getVin() );
        vehicleDTO.setModel( vehicle.getModel() );
        vehicleDTO.setBrand( vehicle.getBrand() );

        return vehicleDTO;
    }

    @Override
    public Vehicle vehicleDtoToVehicle(VehicleDTO vehicleDTO) {
        if ( vehicleDTO == null ) {
            return null;
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setClient( vehicleDTO.getUser() );
        vehicle.setYear( vehicleDTO.getYearOfCreation() );
        vehicle.setVehiclePlate( vehicleDTO.getVehiclePlate() );
        vehicle.setVin( vehicleDTO.getVin() );
        vehicle.setBrand( vehicleDTO.getBrand() );
        vehicle.setModel( vehicleDTO.getModel() );

        return vehicle;
    }

    @Override
    public Vehicle vehicleDtoToVehicleWithUser(VehicleDTO vehicleDTO, User user) {
        if ( vehicleDTO == null && user == null ) {
            return null;
        }

        Vehicle vehicle = new Vehicle();

        if ( vehicleDTO != null ) {
            vehicle.setYear( vehicleDTO.getYearOfCreation() );
            vehicle.setVehiclePlate( vehicleDTO.getVehiclePlate() );
            vehicle.setVin( vehicleDTO.getVin() );
            vehicle.setBrand( vehicleDTO.getBrand() );
            vehicle.setModel( vehicleDTO.getModel() );
        }
        vehicle.setClient( user );

        return vehicle;
    }
}
