package com.simple_shop.Controller;

import com.simple_shop.Model.Customer;
import com.simple_shop.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                    //tells Spring this class will handle HTTP requests and automatically convert results into JSON.
@RequestMapping("/api/customers")  //means all APIs inside this class will start with this URL:
public class CustomerController {

    private final CustomerRepository customerRepo;

    public CustomerController(CustomerRepository customerRepo) {   // This connects your controller to the database through CustomerRepository.
        this.customerRepo = customerRepo;
    }

    @GetMapping
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Customer customer) {   //@RequestBody converts that JSON into a Customer Java object
        customerRepo.save(customer);                                         //saves it into the database.
        return ResponseEntity.ok("Customer Created Successfully!");
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer updated) {
        return customerRepo.findById(id)       // It finds the customer by id
                .map(c -> {
                    c.setName(updated.getName());
                    c.setEmail(updated.getEmail());
                    return customerRepo.save(c);
                })                            // If the customer exists → updates the name & email → saves it.
                .orElseThrow(() -> new RuntimeException("Customer not found"));  //If the customer doesn’t exist → throws an exception
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!customerRepo.existsById(id)) {
            return ResponseEntity.status(404).body("No customer found with ID: " + id);
        }

        customerRepo.deleteById(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

}
