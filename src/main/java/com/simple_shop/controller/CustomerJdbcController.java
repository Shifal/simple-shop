package com.simple_shop.controller;

import com.simple_shop.model.Customer;
import com.simple_shop.service.CustomerJdbcService;
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
