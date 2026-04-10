package com.example.demo.mappers;

import com.example.demo.DTO.serviceItem.ServiceItemCreateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import com.example.demo.DTO.serviceItem.ServiceItemUpdateDTO;
import com.example.demo.models.ServiceItem;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class ServiceItemMapperImpl implements ServiceItemMapper {

    @Override
    public ServiceItem toEntity(ServiceItemCreateDTO serviceItemDTO) {
        if ( serviceItemDTO == null ) {
            return null;
        }

        ServiceItem serviceItem = new ServiceItem();

        serviceItem.setServiceDescription( serviceItemDTO.serviceDescription() );
        if ( serviceItemDTO.price() != null ) {
            serviceItem.setPrice( BigDecimal.valueOf( serviceItemDTO.price() ) );
        }

        return serviceItem;
    }

    @Override
    public ServiceItem toEntity(ServiceItemUpdateDTO serviceItemDTO) {
        if ( serviceItemDTO == null ) {
            return null;
        }

        ServiceItem serviceItem = new ServiceItem();

        serviceItem.setServiceDescription( serviceItemDTO.serviceDescription() );
        if ( serviceItemDTO.price() != null ) {
            serviceItem.setPrice( BigDecimal.valueOf( serviceItemDTO.price() ) );
        }

        return serviceItem;
    }

    @Override
    public ServiceItemResponseDTO toDto(ServiceItem serviceItem) {
        if ( serviceItem == null ) {
            return null;
        }

        ServiceItemResponseDTO.ServiceItemResponseDTOBuilder serviceItemResponseDTO = ServiceItemResponseDTO.builder();

        serviceItemResponseDTO.id( serviceItem.getId() );
        serviceItemResponseDTO.serviceName( serviceItem.getServiceName() );
        serviceItemResponseDTO.serviceDescription( serviceItem.getServiceDescription() );
        if ( serviceItem.getPrice() != null ) {
            serviceItemResponseDTO.price( serviceItem.getPrice().doubleValue() );
        }

        return serviceItemResponseDTO.build();
    }
}
