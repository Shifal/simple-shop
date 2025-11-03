package com.simple_shop.mapper;

import com.simple_shop.dto.OrderRequestDTO;
import com.simple_shop.dto.OrderResponseDTO;
import com.simple_shop.model.Order;
import com.simple_shop.model.Customer;

public class OrderMapper {

    public static Order toEntity(OrderRequestDTO dto, Customer customer) {
        return Order.builder()
                .product(dto.getProduct())
                .quantity(dto.getQuantity())
                .status("CREATED")
                .customer(customer)
                .build();
    }

    public static OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .product(order.getProduct())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .customerId(order.getCustomer().getId())
                .build();
    }
}
