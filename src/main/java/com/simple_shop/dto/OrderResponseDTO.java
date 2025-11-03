package com.simple_shop.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private String product;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    private Long customerId;
}
