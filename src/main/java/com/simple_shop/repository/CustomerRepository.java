package com.simple_shop.repository;

import com.simple_shop.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository; //providing database operations without writing SQL.
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByCustomerId(String customerId);
    boolean existsByCustomerId(String customerId);
    void deleteByCustomerId(String customerId);
}