package com.example.demo.service;

import com.example.demo.DTO.visitItem.VisitItemCreateDTO;
import com.example.demo.DTO.visitItem.VisitItemUpdateDTO;
import com.example.demo.exceptions.ResourceConflictException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.Visit;
import com.example.demo.models.VisitItem;
import com.example.demo.repositories.PackRepository;
import com.example.demo.repositories.ServiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class VisitItemServiceImpl implements VisitItemService{

    private final ServiceRepository serviceRepository;
    private final PackRepository packRepository;

    public VisitItemServiceImpl(ServiceRepository serviceRepository,
                                PackRepository packRepository) {
        this.serviceRepository = serviceRepository;
        this.packRepository = packRepository;
    }

    // ========== CREATE ==========
    @Override
    public VisitItem fromCreateDTO(VisitItemCreateDTO dto, Visit visit) {

        VisitItem item = new VisitItem();
        item.setVisit(visit);

        applyRelations(item, dto.serviceItemId(), dto.packId());
        applyDefaults(item, dto.quantity(), dto.price());

        validate(item);

        return item;
    }

    // ========== UPDATE ==========
    @Override
    public VisitItem fromUpdateDTO(VisitItemUpdateDTO dto, Visit visit) {

        VisitItem item = new VisitItem();
        item.setVisit(visit);

        applyRelations(item, dto.serviceItemId(), dto.packId());
        applyDefaults(item, dto.quantity(), dto.price());

        validate(item);

        return item;
    }

    // ========== INTERNAL LOGIC ==========

    private void applyRelations(VisitItem item, Long serviceId, Long packId) {

        if (serviceId != null) {
            ServiceItem service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
            item.setServiceItem(service);
        }

        if (packId != null) {
            Pack pack = packRepository.findById(packId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pack not found"));
            item.setPack(pack);
        }
    }

    private void applyDefaults(VisitItem item, Integer quantity, BigDecimal price) {
        item.setQuantity(quantity != null ? quantity : 1);
        item.setPrice(price);
    }

    private void validate(VisitItem item) {

        if (item.getServiceItem() == null && item.getPack() == null) {
            throw new ResourceNotFoundException("VisitItem must have service or pack");
        }

        if (item.getServiceItem() != null && item.getPack() != null) {
            throw new ResourceConflictException("VisitItem cannot have both service and pack");
        }

        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }

        item.setItemType(
                item.getServiceItem() != null
                        ? VisitItem.ItemType.SERVICE
                        : VisitItem.ItemType.PACK
        );

        if (item.getServiceItem() != null && item.getItemName() == null) {
            item.setItemName(item.getServiceItem().getServiceName());
        }

        if (item.getPack() != null && item.getItemName() == null) {
            item.setItemName(item.getPack().getPackName());
        }

        if (item.getQuantity() == null) {
            item.setQuantity(1);
        }
    }
}