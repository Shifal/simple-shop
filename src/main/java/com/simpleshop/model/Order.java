package com.simpleshop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;
    private int quantity;
    private String status;

    // IMPORTANT PART:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_ref_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;
}
