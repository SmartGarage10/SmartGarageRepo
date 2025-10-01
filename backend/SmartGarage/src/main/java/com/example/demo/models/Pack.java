package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pack_id")
    private int id;

    @Column(name = "pack", length = 50)
    private String packName; // e.g., BASIC, STANDARD, PREMIUM, CUSTOM

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "amount")
    private Double amount; // Base price, can be 0 for CUSTOM packs

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "pack_services",
            joinColumns = @JoinColumn(name = "pack_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceItem> services = new ArrayList<>();

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Visit> visits; // Visits that use this pack

    // You MUST keep these manual methods
    @Transient
    @JsonProperty("totalPrice")
    public double getTotalPrice() {
        if (services == null || services.isEmpty()) return 0.0;
        return services.stream()
                .map(ServiceItem::getPrice) // ServiceItem.getPrice() must return double
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public void addService(ServiceItem service) {
        if (this.services == null) this.services = new ArrayList<>();
        this.services.add(service);
    }

    public void removeService(ServiceItem service) {
        if (this.services != null) this.services.remove(service);
    }
}
