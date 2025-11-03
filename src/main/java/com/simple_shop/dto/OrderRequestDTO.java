package com.simple_shop.dto;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private String product;
    private int quantity;
}
