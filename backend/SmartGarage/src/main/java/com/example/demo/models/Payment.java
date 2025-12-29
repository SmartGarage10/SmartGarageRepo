package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "visit")
@EqualsAndHashCode(callSuper = true, exclude = {"visit"})
public class Payment extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Column(name = "method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Add amount field to match visit amount
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency = "USD";
}