package com.example.demo.mappers;

import com.example.demo.DTO.pack.PackCreateDTO;
import com.example.demo.DTO.pack.PackResponseDTO;
import com.example.demo.DTO.pack.PackUpdateDTO;
import com.example.demo.DTO.serviceItem.ServiceItemResponseDTO;
import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class PackMapperImpl implements PackMapper {

    @Override
    public Pack toEntity(PackCreateDTO packDTO) {
        if ( packDTO == null ) {
            return null;
        }

        Pack pack = new Pack();

        pack.setPackName( packDTO.packName() );
        pack.setDescription( packDTO.description() );
        pack.setAmount( packDTO.amount() );

        return pack;
    }

    @Override
    public Pack toEntity(PackUpdateDTO packDTO) {
        if ( packDTO == null ) {
            return null;
        }

        Pack pack = new Pack();

        pack.setPackName( packDTO.packName() );
        pack.setDescription( packDTO.description() );
        pack.setAmount( packDTO.amount() );

        return pack;
    }

    @Override
    public PackResponseDTO toDto(Pack pack) {
        if ( pack == null ) {
            return null;
        }

        PackResponseDTO.PackResponseDTOBuilder packResponseDTO = PackResponseDTO.builder();

        packResponseDTO.id( pack.getId() );
        packResponseDTO.packName( pack.getPackName() );
        packResponseDTO.description( pack.getDescription() );
        packResponseDTO.amount( pack.getAmount() );
        packResponseDTO.services( serviceItemListToServiceItemResponseDTOList( pack.getServices() ) );
        packResponseDTO.totalPrice( pack.getTotalPrice() );

        return packResponseDTO.build();
    }

    protected ServiceItemResponseDTO serviceItemToServiceItemResponseDTO(ServiceItem serviceItem) {
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

    protected List<ServiceItemResponseDTO> serviceItemListToServiceItemResponseDTOList(List<ServiceItem> list) {
        if ( list == null ) {
            return null;
        }

        List<ServiceItemResponseDTO> list1 = new ArrayList<ServiceItemResponseDTO>( list.size() );
        for ( ServiceItem serviceItem : list ) {
            list1.add( serviceItemToServiceItemResponseDTO( serviceItem ) );
        }

        return list1;
    }
}
