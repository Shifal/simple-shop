package com.simple_shop.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String customerId;
    private String name;
    private String email;
}
