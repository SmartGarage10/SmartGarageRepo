package com.example.demo.mappers;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.*;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface VisitMapper {
    // ========== VisitDTO -> Visit ==========
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "vehicle", source = "vehicle")
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "visitDate", source = "visitDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "visitItems", source = "visitItems")
    Visit toEntity(VisitDTO visitDTO);

    // ========== Custom Mapping Methods ==========

    // Helper to get client from vehicle's user
    default User getClientFromVehicle(Vehicle vehicle) {
        if (vehicle == null) return null;
        return vehicle.getClient();
    }

    // Optional: Map pack from visit items if needed
    default Pack extractPackFromVisitItems(List<VisitItem> visitItems) {
        if (visitItems == null || visitItems.isEmpty()) return null;

        return visitItems.stream()
                .filter(item -> item.getItemType() == VisitItem.ItemType.PACK && item.getPack() != null)
                .map(VisitItem::getPack)
                .findFirst()
                .orElse(null);
    }
}