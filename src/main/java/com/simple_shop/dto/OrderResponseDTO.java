package com.simple_shop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private String product;
    private int quantity;
    private String status;
    private String customerId;
}
