package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ServiceOrderDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceOrderDetailId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private ServiceOrder order;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem service;
}
