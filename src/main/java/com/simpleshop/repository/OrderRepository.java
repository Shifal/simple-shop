package com.simpleshop.repository;

import com.simpleshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository; //providing database operations without writing SQL.

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByIdAndCustomer_CustomerId(Long orderId, String customerId);
}
