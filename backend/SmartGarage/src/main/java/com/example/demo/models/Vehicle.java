package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"client", "visits"})
@EqualsAndHashCode(callSuper = true, exclude = {"client", "visits"})
public class Vehicle extends BaseEntity {
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

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore // Prevent circular JSON serialization
    private Set<Visit> visits;

}
