package com.simple_shop.service;

import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.mapper.CustomerMapper;
import com.simple_shop.model.Customer;
import com.simple_shop.repository.CustomerRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;

    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    // Get all customers
    public List<CustomerDTO> getAllCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Create a new customer - return Optional.empty() if creation fails
    public Optional<CustomerDTO> createCustomer(Customer customer) {
        try {
            Customer saved = customerRepo.save(customer);
            return Optional.ofNullable(CustomerMapper.toDTO(saved));
        } catch (DataAccessException | IllegalArgumentException ex) {
            // log.error("Error creating customer", ex);  // add logger if needed
            return Optional.empty();
        }
    }

    // Update existing customer
    public Optional<CustomerDTO> updateCustomer(Long id, Customer updated) {
        return customerRepo.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setEmail(updated.getEmail());
                    Customer saved = customerRepo.save(existing);
                    return CustomerMapper.toDTO(saved);
                });
    }

    // Delete a customer
    public boolean deleteCustomer(Long id) {
        if (!customerRepo.existsById(id)) {
            return false;
        }
        customerRepo.deleteById(id);
        return true;
    }
}
