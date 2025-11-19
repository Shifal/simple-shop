package com.simple_shop.repository;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import org.springframework.data.jpa.repository.JpaRepository; //providing database operations without writing SQL.

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCustomer_CustomerId(String customerId);
    void deleteByCustomer(Customer customer);

}
