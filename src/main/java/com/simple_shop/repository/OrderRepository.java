package com.simple_shop.repository;

import com.simple_shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByIdAndCustomer_CustomerId(Long orderId, String customerId);
}
