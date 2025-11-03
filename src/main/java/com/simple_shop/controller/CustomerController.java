package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.model.Customer;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Get all customers (handles empty list)
    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        if (customers == null || customers.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.NO_CUSTOMERS_FOUND, customers));
        }
        return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.FETCH_SUCCESS, customers));
    }

    // Create customer (handles creation failure)
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Customer customer) {
        return customerService.createCustomer(customer)
                .map(created -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new ApiResponse(true, ResponseMessages.CUSTOMER_CREATED, created))
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse(false, ResponseMessages.CUSTOMER_CREATION_FAILED, null))
                );
    }

    // Update customer
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Customer updated) {
        return customerService.updateCustomer(id, updated)
                .map(dto -> ResponseEntity.ok(
                        new ApiResponse(true, ResponseMessages.CUSTOMER_UPDATED, dto)
                ))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null))
                );
    }

    // Delete customer
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null));
        }
        return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.CUSTOMER_DELETED, null));
    }
}
