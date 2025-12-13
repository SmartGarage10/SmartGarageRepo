package com.example.demo.mappers;

import com.example.demo.DTO.PackDTO;
import com.example.demo.models.Pack;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-13T09:46:43+0200",
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

        return packDTO;
    }

    @Override
    public Pack toEntity(PackDTO packDTO) {
        if ( packDTO == null ) {
            return null;
        }

        Pack pack = new Pack();

        return pack;
    }
}
