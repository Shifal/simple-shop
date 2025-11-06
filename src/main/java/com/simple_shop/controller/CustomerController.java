package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.model.Customer;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Get all customers
    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.NO_CUSTOMERS_FOUND, customers));
        }
        return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.FETCH_SUCCESS, customers));
    }

    // Get a single customer by customerId
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse> getCustomer(@PathVariable String customerId) {
        return customerService.getCustomerByCustomerId(customerId)
                .map(dto -> ResponseEntity.ok(new ApiResponse(true, ResponseMessages.FETCH_SUCCESS, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null)));
    }

    // Create customer
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Customer customer) {
        return customerService.createCustomer(customer)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse(true, ResponseMessages.CUSTOMER_CREATED, created)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(false, ResponseMessages.CUSTOMER_CREATION_FAILED, null)));
    }

    // Update customer by customerId
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse> update(@PathVariable String customerId, @RequestBody Customer updated) {
        return customerService.updateCustomer(customerId, updated)
                .map(dto -> ResponseEntity.ok(new ApiResponse(true, ResponseMessages.CUSTOMER_UPDATED, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null)));
    }

    // Delete customer by customerId
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String customerId) {
        boolean deleted = customerService.deleteCustomer(customerId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null));
        }
        return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.CUSTOMER_DELETED, null));
    }
}
