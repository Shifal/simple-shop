package com.simpleshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    // Link to customer (foreign key)
    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", unique = true)
    private Customer customer;
}
