package com.simpleshop.controller;

import com.simpleshop.model.Customer;
import com.simpleshop.service.CustomerJdbcService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jdbc/customers")
public class CustomerJdbcController {

    private final CustomerJdbcService service;

    public CustomerJdbcController(CustomerJdbcService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Customer one(@PathVariable Long id) {
        return service.getOne(id);
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Customer customer) {
        service.addCustomer(customer);
        return ResponseEntity.ok("Created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Customer customer) {
        service.updateCustomer(id, customer);
        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.ok("Deleted");
    }
}
