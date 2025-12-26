package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({"visits"})
    @ToString.Exclude
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"vehicles", "visits"})
    @ToString.Exclude
    private User employee;

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    // REMOVE: Old pack relationship
    // @ManyToOne
    // @JoinColumn(name = "pack_id")
    // private Pack pack;

    // REMOVE: Old services relationship
    // @ManyToMany
    // @JoinTable(...)
    // private List<ServiceItem> visitServices;

    // ADD: Unified items list
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<VisitItem> visitItems = new ArrayList<>();

    // Helper methods
    public void addServiceItem(ServiceItem service, Double customPrice, Integer quantity) {
        VisitItem item = new VisitItem(service, customPrice, quantity);
        item.setVisit(this);
        this.visitItems.add(item);
        calculateTotal();
    }

    public void addPackItem(Pack pack, Double customPrice, Integer quantity) {
        VisitItem item = new VisitItem(pack, customPrice, quantity);
        item.setVisit(this);
        this.visitItems.add(item);
        calculateTotal();
    }

    public void calculateTotal() {
        if (visitItems == null || visitItems.isEmpty()) {
            this.amount = 0.0;
            return;
        }

        this.amount = visitItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
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