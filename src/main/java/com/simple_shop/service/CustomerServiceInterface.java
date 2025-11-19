package com.simple_shop.service;

import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerServiceInterface {

    List<CustomerDTO> getAllCustomers();

    Optional<CustomerDTO> createCustomer(Customer customer);

    Optional<CustomerDTO> updateCustomer(String customerId, Customer updated);

    boolean deleteCustomer(String customerId);

    CustomerDTO getCustomerSecure(String customerId, String requesterKcId);

    boolean blockCustomer(String customerId);

    boolean unblockCustomer(String customerId);

}
