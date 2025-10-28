package com.simple_shop.Controller;

import com.simple_shop.Model.Customer;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomerRepository customerRepo;
    private final JwtUtil jwtUtil;

    public AuthController(CustomerRepository customerRepo, JwtUtil jwtUtil) {
        this.customerRepo = customerRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Customer loginRequest) {
        Optional<Customer> customerOpt = customerRepo.findByEmail(loginRequest.getEmail());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getPassword().equals(loginRequest.getPassword())) {
                String token = jwtUtil.generateToken(customer.getId());
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }
}
