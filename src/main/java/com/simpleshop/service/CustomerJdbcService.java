package com.simpleshop.service;

import com.simpleshop.model.Customer;
import com.simpleshop.repository.CustomerJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerJdbcService {

    private final CustomerJdbcRepository repository;

    public CustomerJdbcService(CustomerJdbcRepository repository) {
        this.repository = repository;
    }

    public List<Customer> getAll() {
        return repository.findAll();
    }

    public Customer getOne(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void addCustomer(Customer customer) {
        repository.save(customer);
    }

    public void updateCustomer(Long id, Customer customer) {
        repository.update(id, customer);
    }

    public void deleteCustomer(Long id) {
        repository.delete(id);
    }
}
