package com.simpleshop.repository;

import com.simpleshop.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository; //providing database operations without writing SQL.
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(String customerId);
    Optional<Customer> findByKeycloakId(String keycloakId);

}