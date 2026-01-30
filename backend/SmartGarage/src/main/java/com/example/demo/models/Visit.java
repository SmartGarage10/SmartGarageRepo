package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"vehicle", "employee", "visitItems"})
@ToString(exclude = {"vehicle", "employee", "visitItems"})
public class Visit extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({"visits"})
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"vehicles", "visits"})
    private User employee;

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "EUR";

    // ADD: Unified items list
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<VisitItem> visitItems = new ArrayList<>();

    // Helper methods
    public void addServiceItem(ServiceItem service, BigDecimal customPrice, Integer quantity) {
        VisitItem item = new VisitItem(service, customPrice, quantity);
        item.setVisit(this);
        this.visitItems.add(item);
        calculateTotal();
    }

    public void addPackItem(Pack pack, BigDecimal customPrice, Integer quantity) {
        VisitItem item = new VisitItem(pack, customPrice, quantity);
        item.setVisit(this);
        this.visitItems.add(item);
        calculateTotal();
    }

    public void calculateTotal() {
        if (visitItems == null || visitItems.isEmpty()) {
            this.amount = BigDecimal.ZERO;
            return;
        }

        this.amount = visitItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Get all services included (from both individual items and packs)
    @Transient
    public List<ServiceItem> getAllServices() {
        List<ServiceItem> allServices = new ArrayList<>();

        for (VisitItem item : visitItems) {
            if (item.getItemType() == VisitItem.ItemType.SERVICE && item.getServiceItem() != null) {
                allServices.add(item.getServiceItem());
            } else if (item.getItemType() == VisitItem.ItemType.PACK && item.getPack() != null) {
                allServices.addAll(item.getPack().getServices());
            }
        }

        return allServices;
    }
}