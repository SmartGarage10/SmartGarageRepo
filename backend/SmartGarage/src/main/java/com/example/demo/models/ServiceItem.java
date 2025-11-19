package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int id;

    @Column(name = "name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "description")
    private String serviceDescription;

    @Column(name = "price", nullable = false)
    private double price;

    @ManyToMany(mappedBy = "visitServices")
    @JsonIgnoreProperties({"visitServices"})
    @ToString.Exclude
    private List<Visit> visits;

    @ManyToMany(mappedBy = "services")
    @JsonIgnoreProperties({"services"})
    @ToString.Exclude
    private List<Pack> packs;
}
