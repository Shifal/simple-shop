package com.simple_shop.controller;

import com.simple_shop.model.Customer;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.util.JwtUtil;
import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse> login(
            @RequestBody Customer loginRequest
    ) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                || loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, ResponseMessages.INVALID_CREDENTIALS, null));
        }

        Optional<Customer> customerOpt = customerRepo.findByEmail(loginRequest.getEmail());

        if (customerOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessages.USER_NOT_FOUND, null));
        }

        Customer customer = customerOpt.get();

        if (!customer.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, ResponseMessages.INVALID_PASSWORD, null));
        }

        String token = jwtUtil.generateToken(customer.getCustomerId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, ResponseMessages.LOGIN_SUCCESS, token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, ResponseMessages.LOGOUT_SUCCESS, null));
    }
}
