package com.example.demo.mappers;

import com.example.demo.DTO.PackDTO;
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
    public PackDTO toDto(Pack pack) {
        if ( pack == null ) {
            return null;
        }

        PackDTO packDTO = new PackDTO();

        packDTO.setPackName( pack.getPackName() );
        packDTO.setDescription( pack.getDescription() );
        packDTO.setAmount( pack.getAmount() );
        List<ServiceItem> list = pack.getServices();
        if ( list != null ) {
            packDTO.setServices( new ArrayList<ServiceItem>( list ) );
        }

        return packDTO;
    }

    @Override
    public Pack toEntity(PackDTO packDTO) {
        if ( packDTO == null ) {
            return null;
        }

        Pack pack = new Pack();

        pack.setPackName( packDTO.getPackName() );
        pack.setDescription( packDTO.getDescription() );
        pack.setAmount( packDTO.getAmount() );
        List<ServiceItem> list = packDTO.getServices();
        if ( list != null ) {
            pack.setServices( new ArrayList<ServiceItem>( list ) );
        }

        return pack;
    }
}
