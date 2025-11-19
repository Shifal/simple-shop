//package com.simple_shop.controller;
//
//import com.simple_shop.constants.ResponseMessages;
//import com.simple_shop.model.Customer;
//import com.simple_shop.response.ApiResponse;
//import com.simple_shop.service.CustomerService;
//import com.simple_shop.service.RoleService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//
//@RestController  // This class will handle API requests and return JSON responses.
//@RequestMapping("/api/roles")
//public class RoleController {
//
//    private final RoleService roleService;
//    private final CustomerService customerService;
//
//    public RoleController(RoleService roleService, CustomerService customerService) {
//        this.roleService = roleService;
//        this.customerService = customerService;
//    }
//
//    // ADMIN can:
//    // (a) promote existing USER to ADMIN
//    // (b) create a brand-new ADMIN user
//    @PostMapping("/create/admin")
//    public ResponseEntity<ApiResponse> createOrPromoteAdmin(
//            @AuthenticationPrincipal Jwt principal,
//            @RequestBody(required = false) Customer newCustomer,
//            @RequestParam(required = false) String targetCustomerId) {
//
//        try {
//            String requesterId = principal.getSubject();
//
//            // Check if requester is an ADMIN
//            if (!roleService.isAdmin(requesterId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ApiResponse(false, ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, null));
//            }
//
//            // CASE 1: Promote existing customer to ADMIN
//            if (targetCustomerId != null) {
//                boolean updated = roleService.promoteToAdmin(targetCustomerId);
//                if (updated) {
//                    return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ADMIN_PROMOTED, updated));
//                } else {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                            .body(new ApiResponse(false, ResponseMessages.ADMIN_PROMOTION_FAILED, null));
//                }
//            }
//
//            // CASE 2: Create a new ADMIN customer
//            if (newCustomer != null) {
//                return customerService.createAdminCustomer(newCustomer)
//                        .map(created -> ResponseEntity.status(HttpStatus.CREATED)
//                                .body(new ApiResponse(true, ResponseMessages.ADMIN_CREATED, created)))
//                        .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                .body(new ApiResponse(false, ResponseMessages.ADMIN_CREATION_FAILED, null)));
//            }
//
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse(false, "Provide either targetCustomerId (to promote) or Customer data (to create new ADMIN).", null));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ApiResponse(false, "Invalid or expired token.", null));
//        }
//    }
//
//    // Only ADMIN can view roles
//    @GetMapping("/{customerId}")
//    public ResponseEntity<ApiResponse> getRole(
//            @AuthenticationPrincipal Jwt principal,
//            @PathVariable String customerId) {
//
//        try {
//            String requesterId = principal.getSubject();
//
//            if (!roleService.isAdmin(requesterId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ApiResponse(false, ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, null));
//            }
//
//            return roleService.getRoleByCustomerId(customerId)
//                    .map(role -> ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ROLE_FETCH_SUCCESS, role)))
//                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//                            .body(new ApiResponse(false, ResponseMessages.ROLE_NOT_FOUND, null)));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ApiResponse(false, "Invalid or expired token.", null));
//        }
//    }
//}
