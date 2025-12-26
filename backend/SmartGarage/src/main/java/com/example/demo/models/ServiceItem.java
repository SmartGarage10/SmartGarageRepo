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

    @Column(name = "description", length = 500)
    private String serviceDescription;

    @Column(name = "price", nullable = false)
    private double price;

    // REMOVE: Old relationship with Visit
    // @ManyToMany(mappedBy = "visitServices")
    // private List<Visit> visits;

    // KEEP: Relationship with Pack
    @ManyToMany(mappedBy = "services")
    @JsonIgnoreProperties({"services", "visitItems"})
    @ToString.Exclude
    private List<Pack> packs;

    // ADD: New relationship with VisitItem
    @OneToMany(mappedBy = "serviceItem")
    @JsonIgnore
    @ToString.Exclude
    private List<VisitItem> visitItems;
}