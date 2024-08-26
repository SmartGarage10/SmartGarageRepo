package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ServiceOrder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "currency", nullable = false)
    private String currency;
}
