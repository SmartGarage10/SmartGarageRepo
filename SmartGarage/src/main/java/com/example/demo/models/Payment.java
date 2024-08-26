package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private ServiceOrder order;

    @Column(name = "method",nullable = false)
    private String paymentMethod;

    @Column(name = "status", nullable = false)
    private String status;
}
