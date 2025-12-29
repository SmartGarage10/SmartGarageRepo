package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"packs", "visitItems"})
@ToString(exclude = {"packs", "visitItems"})
public class ServiceItem extends  BaseEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "description", length = 500)
    private String serviceDescription;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
    // KEEP: Relationship with Pack
    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"services", "visitItems"})
    private List<Pack> packs = new ArrayList<>();

    // ADD: New relationship with VisitItem
    @OneToMany(mappedBy = "serviceItem", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<VisitItem> visitItems = new ArrayList<>();
}