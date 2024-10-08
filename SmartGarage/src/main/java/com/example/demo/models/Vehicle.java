package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private int id;

    @Column(name = "vehicle_plate", nullable = false)
    private String vehiclePlate;

    @Column(name = "vin", nullable = false)
    private String vin;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private Year year;

    @OneToMany(mappedBy = "vehicle")
    private Set<ServiceOrder> serviceOrders;

    @OneToMany(mappedBy = "vehicle")
    private Set<Visit> visits;

    public int getVehicleId() {
        return id;
    }
}
