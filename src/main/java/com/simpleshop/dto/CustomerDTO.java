package com.simpleshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String customerId;
    private String keycloakId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
}
