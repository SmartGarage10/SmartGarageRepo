package com.example.demo.mappers;

import com.example.demo.DTO.vehicle.VehicleCreateDTO;
import com.example.demo.DTO.vehicle.VehicleResponseDTO;
import com.example.demo.DTO.vehicle.VehicleUpdateDTO;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class VehicleMapperImpl implements VehicleMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public VehicleResponseDTO toDto(Vehicle vehicle) {
        if ( vehicle == null ) {
            return null;
        }

        VehicleResponseDTO.VehicleResponseDTOBuilder vehicleResponseDTO = VehicleResponseDTO.builder();

        vehicleResponseDTO.user( userMapper.toDTO( vehicle.getClient() ) );
        vehicleResponseDTO.yearOfCreation( vehicle.getYear() );
        vehicleResponseDTO.id( vehicle.getId() );
        vehicleResponseDTO.vehiclePlate( vehicle.getVehiclePlate() );
        vehicleResponseDTO.vin( vehicle.getVin() );
        vehicleResponseDTO.model( vehicle.getModel() );
        vehicleResponseDTO.brand( vehicle.getBrand() );

        return vehicleResponseDTO.build();
    }

    @Override
    public Vehicle toEntity(VehicleCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setYear( dto.yearOfCreation() );
        vehicle.setVehiclePlate( dto.vehiclePlate() );
        vehicle.setVin( dto.vin() );
        vehicle.setBrand( dto.brand() );
        vehicle.setModel( dto.model() );

        return vehicle;
    }

    @Override
    public Vehicle toEntity(VehicleUpdateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setYear( dto.yearOfCreation() );
        vehicle.setVehiclePlate( dto.vehiclePlate() );
        vehicle.setVin( dto.vin() );
        vehicle.setBrand( dto.brand() );
        vehicle.setModel( dto.model() );

        return vehicle;
    }

    @Override
    public Vehicle toEntityWithUser(VehicleCreateDTO dto, User user) {
        if ( dto == null && user == null ) {
            return null;
        }

        Vehicle vehicle = new Vehicle();

        if ( dto != null ) {
            vehicle.setYear( dto.yearOfCreation() );
            vehicle.setVehiclePlate( dto.vehiclePlate() );
            vehicle.setVin( dto.vin() );
            vehicle.setBrand( dto.brand() );
            vehicle.setModel( dto.model() );
        }
        vehicle.setClient( user );

        return vehicle;
    }
}
