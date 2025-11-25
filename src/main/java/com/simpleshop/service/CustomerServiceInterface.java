package com.simpleshop.service;

import com.simpleshop.dto.CustomerDTO;
import com.simpleshop.model.Customer;

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

    boolean isOwner(String customerId, String requesterKcId);

}
