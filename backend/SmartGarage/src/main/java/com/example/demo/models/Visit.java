package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class    Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    // ✅ NEW: flag for identifying if this order is a custom pack
    @ManyToOne
    @JoinColumn(name = "pack_id", nullable = false)
    @JsonIgnoreProperties({"visits"}) // Prevent circular reference during serialization while still including the pack data
    private Pack pack; // e.g. BASIC, PREMIUM, DELUXE, CUSTOM

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit_Service> visitServices;

    // Helper method to calculate total from individual services
    public double calculateTotalFromServices() {
        if (visitServices != null && !visitServices.isEmpty()) {
            return visitServices.stream()
                    .mapToDouble(vs -> vs.getService().getPrice())
                    .sum();
        }
        return 0.0;
    }
}
