package com.example.demo.mappers;

import com.example.demo.DTO.ServiceItemDTO;
import com.example.demo.models.ServiceItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class ServiceItemMapperImpl implements ServiceItemMapper {

    @Override
    public ServiceItemDTO toDto(ServiceItem serviceItem) {
        if ( serviceItem == null ) {
            return null;
        }

        ServiceItemDTO serviceItemDTO = new ServiceItemDTO();

        serviceItemDTO.setId( serviceItem.getId() );
        serviceItemDTO.setServiceName( serviceItem.getServiceName() );
        serviceItemDTO.setServiceDescription( serviceItem.getServiceDescription() );
        serviceItemDTO.setPrice( serviceItem.getPrice() );

        return serviceItemDTO;
    }

    @Override
    public ServiceItem toEntity(ServiceItemDTO serviceItemDTO) {
        if ( serviceItemDTO == null ) {
            return null;
        }

        ServiceItem serviceItem = new ServiceItem();

        serviceItem.setId( serviceItemDTO.getId() );
        serviceItem.setServiceName( serviceItemDTO.getServiceName() );
        serviceItem.setServiceDescription( serviceItemDTO.getServiceDescription() );
        serviceItem.setPrice( serviceItemDTO.getPrice() );

        return serviceItem;
    }
}
