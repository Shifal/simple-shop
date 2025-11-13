package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.OrderRequestDTO;
import com.simple_shop.dto.OrderResponseDTO;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.service.OrderService;
import com.simple_shop.service.RoleService;
import com.simple_shop.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor // Automatically injects dependencies (no need for @Autowired).
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;


    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrders(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            String requesterId = jwtUtil.extractCustomerId(token);

            if (!roleService.isAdmin(requesterId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, null));
            }
            List<OrderResponseDTO> orders = orderService.getAllOrders();
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, ResponseMessages.NO_ORDERS_FOUND, null));
            }
            return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ORDER_FETCH_SUCCESS, orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, ResponseMessages.TOKEN_EXPIRED, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ORDER_FETCH_SUCCESS, order)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, ResponseMessages.NO_ORDERS_FOUND, null)));
    }

    @PostMapping("/place/{customerId}")
    public ResponseEntity<ApiResponse> placeOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable String customerId,
            @RequestBody OrderRequestDTO requestDTO) {

        token = token.replace("Bearer ", "");
        System.out.println("my Tokennnnnnn" + token + customerId);

        // Validate token here — this covers expired + malformed + signature issues
        jwtUtil.validateToken(token, customerId);

        return orderService.placeOrder(customerId, requestDTO)
                .map(order -> ResponseEntity.ok(
                        new ApiResponse(true, ResponseMessages.ORDER_CREATED, order)))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, ResponseMessages.ORDER_CREATION_FAILED, null)));
    }


    @PutMapping("/update/{orderId}")
    public ResponseEntity<ApiResponse> updateOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestBody OrderRequestDTO requestDTO) {

        token = token.replace("Bearer ", "");
        String customerId = jwtUtil.extractCustomerId(token);

        if (!orderService.isOrderOwnedByCustomer(orderId, customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ResponseMessages.ORDER_ACCESS_DENIED, null));
        }

        return orderService.updateOrder(orderId, requestDTO)
                .map(order -> ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ORDER_UPDATED, order)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, ResponseMessages.NO_ORDERS_FOUND, null)));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse> deleteOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        try {
            token = token.replace("Bearer ", "");
            String customerId = jwtUtil.extractCustomerId(token);

            // Validate token (expired / malformed / invalid signature)
            jwtUtil.validateToken(token, customerId);

            // Check if order belongs to this customer
            if (!orderService.isOrderOwnedByCustomer(orderId, customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, ResponseMessages.ORDER_ACCESS_DENIED, null));
            }

            boolean deleted = orderService.deleteOrder(orderId);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, ResponseMessages.NO_ORDERS_FOUND, null));
            }

            return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ORDER_DELETED, null));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, ResponseMessages.TOKEN_EXPIRED, null));
        }
    }
}
