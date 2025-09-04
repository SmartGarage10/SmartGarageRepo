package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "visit_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visit_Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_service_id")
    private int visitServiceId;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem service;

    @Setter
    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

}
