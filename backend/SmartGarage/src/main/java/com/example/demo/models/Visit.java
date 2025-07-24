package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    private int visitId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "date", nullable = false)
    private LocalDateTime visitDate;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOrderDetails> serviceOrderDetails;

    public void addServiceOrderDetails(ServiceOrderDetails detail) {
        serviceOrderDetails.add(detail);
        detail.setVisit(this);
    }

    public void removeServiceOrderDetails(ServiceOrderDetails detail) {
        serviceOrderDetails.remove(detail);
        detail.setVisit(null);
    }

}
