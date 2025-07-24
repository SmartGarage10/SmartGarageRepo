package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ServiceOrderDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_order_detail_id")
    private int serviceOrderDetailId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private ServiceOrder order;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem service;

    @Setter
    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

}
