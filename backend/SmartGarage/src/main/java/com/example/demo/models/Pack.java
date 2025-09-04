package com.example.demo.models;

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
    private int packId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pack", length = 50)
    private PackType pack; // e.g., BASIC, STANDARD, PREMIUM, CUSTOM

    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "pack_services",
            joinColumns = @JoinColumn(name = "pack_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceItem> services = new ArrayList<>();

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits; // Visits that use this pack

    public enum PackType {
        BASIC,
        STANDARD,
        PREMIUM,
        CUSTOM
    }

    // You MUST keep these manual methods
    public double getTotalPrice() {
        if (services == null || services.isEmpty()) return 0.0;
        return services.stream().mapToDouble(ServiceItem::getPrice).sum();
    }

    public void addService(ServiceItem service) {
        if (this.services == null) this.services = new ArrayList<>();
        this.services.add(service);
    }

    public void removeService(ServiceItem service) {
        if (this.services != null) this.services.remove(service);
    }
}
