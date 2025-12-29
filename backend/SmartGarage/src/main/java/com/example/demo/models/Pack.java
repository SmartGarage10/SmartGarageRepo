package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"services", "visitItems"})
@EqualsAndHashCode(callSuper = true, exclude = {"services", "visitItems"})
public class Pack extends BaseEntity {

    @Column(name = "pack", length = 50)
    private String packName; // e.g., BASIC, STANDARD, PREMIUM, CUSTOM

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "amount")
    private BigDecimal amount; // Base price, can be 0 for CUSTOM packs

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "pack_services",
            joinColumns = @JoinColumn(name = "pack_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @JsonIgnoreProperties({"packs", "visitItems"})
    private List<ServiceItem> services = new ArrayList<>();

    @OneToMany(mappedBy = "pack")
    @JsonIgnore
    private List<VisitItem> visitItems = new ArrayList<>();

    @Transient
    @JsonProperty("totalPrice")
    public double getTotalPrice() {
        if (services == null || services.isEmpty()) return 0.0;
        return services.stream()
                .map(ServiceItem::getPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }
}