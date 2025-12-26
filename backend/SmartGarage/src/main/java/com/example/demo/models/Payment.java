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
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Column(name = "method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Add amount field to match visit amount
    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double amount;

    @Column(name = "currency", length = 3)
    private String currency = "USD";
}