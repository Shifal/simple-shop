package com.simpleshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    // Link to customer (foreign key)
    @OneToOne
    @JoinColumn(name = "customer_ref_id", referencedColumnName = "customer_id", unique = true)
    private Customer customer;
}
