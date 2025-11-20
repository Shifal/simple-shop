package com.simpleshop.mapper;

import com.simpleshop.dto.CustomerDTO;
import com.simpleshop.model.Customer;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) return null;
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setCustomerId(customer.getCustomerId());
        dto.setKeycloakId(customer.getKeycloakId());
        dto.setUserName(customer.getUserName());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setActive(customer.isActive());
        return dto;
    }

    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) return null;
        Customer c = new Customer();
        c.setId(dto.getId());
        c.setCustomerId(dto.getCustomerId());
        c.setKeycloakId(dto.getKeycloakId());
        c.setUserName(dto.getUserName());
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setActive(dto.isActive());
        return c;
    }
}
