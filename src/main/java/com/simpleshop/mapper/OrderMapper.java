package com.simpleshop.mapper;

import com.simpleshop.dto.OrderRequestDTO;
import com.simpleshop.dto.OrderResponseDTO;
import com.simpleshop.model.Order;
import com.simpleshop.model.Customer;

public class OrderMapper {

    public static Order toEntity(OrderRequestDTO dto, Customer customer) {
        return Order.builder()
                .product(dto.getProduct())
                .quantity(dto.getQuantity())
                .status("PLACED")
                .customer(customer)
                .build();
    }

    public static OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .product(order.getProduct())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .customerId(order.getCustomer().getCustomerId())
                .build();
    }
}
