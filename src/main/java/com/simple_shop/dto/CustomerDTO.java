package com.simple_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data; //don’t have to manually write getters/setters
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String customerId;
    private String name;
    private String email;
}
