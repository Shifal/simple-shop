package com.simple_shop.dto;

import lombok.Data; //don’t have to manually write getters/setters

@Data
public class CustomerDTO {
    private Long id;
    private String customerId;
    private String name;
    private String email;
}
