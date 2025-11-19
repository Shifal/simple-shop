package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.model.Customer;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.service.CustomerService;
import com.simple_shop.service.CustomerServiceInterface;
import com.simple_shop.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerServiceInterface  customerService;
    private final RoleService roleService;

    public CustomerController(CustomerServiceInterface customerService, RoleService roleService) {
        this.customerService = customerService;
        this.roleService = roleService;
    }

    // ADMIN → Get all customers
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCustomers(@AuthenticationPrincipal Jwt principal) {

        if (!roleService.isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, null));
        }

        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(
                new ApiResponse(true, ResponseMessages.FETCH_SUCCESS, customers)
        );
    }

    // USER → Access own data | ADMIN → Access any user
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse> getCustomerByCustomerId(
            @PathVariable String customerId,
            @AuthenticationPrincipal Jwt principal) {

        String requesterKcId = principal.getSubject();
        CustomerDTO dto = customerService.getCustomerSecure(customerId, requesterKcId);

        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null));
        }

        // dto exists but user not allowed
        if (dto.getCustomerId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ResponseMessages.ACCESS_DENIED, null));
        }

        return ResponseEntity.ok(
                new ApiResponse(true, ResponseMessages.FETCH_SUCCESS, dto)
        );
    }

    // Public → Self-onboarding (no auth)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody Customer customer) {

        return customerService.createCustomer(customer)
                .map(created ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ApiResponse(true, ResponseMessages.CUSTOMER_CREATED, created))
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse(false, ResponseMessages.CUSTOMER_CREATION_FAILED, null))
                );
    }

    // USER → Update self | ADMIN → Update anyone
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse> updateCustomer(
            @PathVariable String customerId,
            @RequestBody Customer updated) {

        return customerService.updateCustomer(customerId, updated)
                .map(dto -> ResponseEntity.ok(
                        new ApiResponse(true, ResponseMessages.CUSTOMER_UPDATED, dto)))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null))
                );
    }

    // ADMIN → Delete user
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable String customerId) {

        boolean deleted = customerService.deleteCustomer(customerId);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessages.CUSTOMER_NOT_FOUND, null));
        }

        return ResponseEntity.ok(
                new ApiResponse(true, ResponseMessages.CUSTOMER_DELETED, null)
        );
    }

    // ADMIN → Block user
    @PutMapping("/block/{customerId}")
    public ResponseEntity<ApiResponse> blockCustomer(@PathVariable String customerId) {

        if (!customerService.blockCustomer(customerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Customer not found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Customer blocked successfully", null)
        );
    }

    // ADMIN → Unblock user
    @PutMapping("/unblock/{customerId}")
    public ResponseEntity<ApiResponse> unblockCustomer(@PathVariable String customerId) {

        if (!customerService.unblockCustomer(customerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Customer not found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Customer unblocked successfully", null)
        );
    }
}
