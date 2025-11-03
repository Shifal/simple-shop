package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.OrderRequestDTO;
import com.simple_shop.dto.OrderResponseDTO;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.service.OrderService;
import com.simple_shop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, ResponseMessages.NO_ORDERS_FOUND, null));
        }
        return ResponseEntity.ok(new ApiResponse(true, ResponseMessages.ORDER_FETCH_SUCCESS, orders));
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
            @PathVariable Long customerId,
            @RequestBody OrderRequestDTO requestDTO) {

        token = token.replace("Bearer ", "");

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
        Long customerId = jwtUtil.extractCustomerId(token);

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

        token = token.replace("Bearer ", "");
        Long customerId = jwtUtil.extractCustomerId(token);

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
    }
}
