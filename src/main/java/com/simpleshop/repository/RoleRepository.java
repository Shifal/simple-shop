package com.simpleshop.repository;

import com.simpleshop.model.Customer;
import com.simpleshop.model.Role;
import org.springframework.data.jpa.repository.JpaRepository; //providing database operations without writing SQL.

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCustomer_CustomerId(String customerId);
    void deleteByCustomer(Customer customer);

}
