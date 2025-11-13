package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.OrderRequestDTO;
import com.simple_shop.dto.OrderResponseDTO;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.service.OrderService;
import com.simple_shop.service.RoleService;
import com.simple_shop.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private OrderController orderController;

    private String token;
    private String customerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        token = "validToken";
        customerId = "CUST123";
    }

    // -------------------- GET ALL ORDERS --------------------
    @Test
    void testGetAllOrders_Success() {
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(roleService.isAdmin(customerId)).thenReturn(true);
        when(orderService.getAllOrders()).thenReturn(List.of(
                new OrderResponseDTO(1L, "CUST123", 2, "NEW", "Laptop")
        ));

        ResponseEntity<ApiResponse> response = orderController.getAllOrders("Bearer " + token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_FETCH_SUCCESS, response.getBody().getMessage());
    }

    @Test
    void testGetAllOrders_Empty() {
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(roleService.isAdmin(customerId)).thenReturn(true);
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse> response = orderController.getAllOrders("Bearer " + token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseMessages.NO_ORDERS_FOUND, response.getBody().getMessage());
    }

    @Test
    void testGetAllOrders_AccessDenied() {
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(roleService.isAdmin(customerId)).thenReturn(false);

        ResponseEntity<ApiResponse> response = orderController.getAllOrders("Bearer " + token);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, response.getBody().getMessage());
    }

    @Test
    void testGetAllOrders_JwtException() {
        when(jwtUtil.extractCustomerId(anyString())).thenThrow(new JwtException("Invalid token"));

        ResponseEntity<ApiResponse> response = orderController.getAllOrders("Bearer " + token);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ResponseMessages.TOKEN_EXPIRED, response.getBody().getMessage());
    }

    // -------------------- GET ORDER BY ID --------------------
    @Test
    void testGetOrderById_Found() {
        OrderResponseDTO order = new OrderResponseDTO(1L, "CUST123", 1, "PLACED", "Keyboard");
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<ApiResponse> response = orderController.getOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_FETCH_SUCCESS, response.getBody().getMessage());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderService.getOrderById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = orderController.getOrder(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseMessages.NO_ORDERS_FOUND, response.getBody().getMessage());
    }

    // -------------------- PLACE ORDER --------------------
    @Test
    void testPlaceOrder_Success() {
        OrderRequestDTO request = new OrderRequestDTO();
        OrderResponseDTO orderResponse = new OrderResponseDTO(1L, "CUST123", 1, "PLACED", "Mouse");

        // Use when().thenReturn() instead of doNothing()
        when(jwtUtil.validateToken(anyString(), anyString())).thenReturn(true);
        when(orderService.placeOrder(eq(customerId), any())).thenReturn(Optional.of(orderResponse));

        ResponseEntity<ApiResponse> response = orderController.placeOrder("Bearer " + token, customerId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_CREATED, response.getBody().getMessage());
    }

    // -------------------- UPDATE ORDER --------------------
    @Test
    void testUpdateOrder_Success() {
        OrderRequestDTO request = new OrderRequestDTO();
        OrderResponseDTO orderResponse = new OrderResponseDTO(1L, "CUST123", 2, "UPDATED", "Laptop");

        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(orderService.isOrderOwnedByCustomer(1L, customerId)).thenReturn(true);
        when(orderService.updateOrder(1L, request)).thenReturn(Optional.of(orderResponse));

        ResponseEntity<ApiResponse> response = orderController.updateOrder("Bearer " + token, 1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_UPDATED, response.getBody().getMessage());
    }

    @Test
    void testUpdateOrder_NotOwned() {
        OrderRequestDTO request = new OrderRequestDTO();
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(orderService.isOrderOwnedByCustomer(1L, customerId)).thenReturn(false);

        ResponseEntity<ApiResponse> response = orderController.updateOrder("Bearer " + token, 1L, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_ACCESS_DENIED, response.getBody().getMessage());
    }

    @Test
    void testUpdateOrder_NotFound() {
        OrderRequestDTO request = new OrderRequestDTO();
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(orderService.isOrderOwnedByCustomer(1L, customerId)).thenReturn(true);
        when(orderService.updateOrder(1L, request)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = orderController.updateOrder("Bearer " + token, 1L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseMessages.NO_ORDERS_FOUND, response.getBody().getMessage());
    }

    // -------------------- DELETE ORDER --------------------
    @Test
    void testDeleteOrder_Success() {
        when(jwtUtil.validateToken(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(orderService.isOrderOwnedByCustomer(1L, customerId)).thenReturn(true);
        when(orderService.deleteOrder(1L)).thenReturn(true);

        ResponseEntity<ApiResponse> response = orderController.deleteOrder("Bearer " + token, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_DELETED, response.getBody().getMessage());
    }

    @Test
    void testDeleteOrder_NotOwned() {
        when(jwtUtil.validateToken(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(orderService.isOrderOwnedByCustomer(1L, customerId)).thenReturn(false);

        ResponseEntity<ApiResponse> response = orderController.deleteOrder("Bearer " + token, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ResponseMessages.ORDER_ACCESS_DENIED, response.getBody().getMessage());
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(jwtUtil.validateToken(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.extractCustomerId(anyString())).thenReturn(customerId);
        when(orderService.isOrderOwnedByCustomer(1L, customerId)).thenReturn(true);
        when(orderService.deleteOrder(1L)).thenReturn(false);

        ResponseEntity<ApiResponse> response = orderController.deleteOrder("Bearer " + token, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseMessages.NO_ORDERS_FOUND, response.getBody().getMessage());
    }

    @Test
    void testDeleteOrder_InvalidToken() {
        // Mock extractCustomerId to return any ID
        when(jwtUtil.extractCustomerId(anyString())).thenReturn("CUST123");
        // Mock validateToken to throw JwtException
        when(jwtUtil.validateToken(anyString(), anyString()))
                .thenThrow(new JwtException("Invalid token"));

        ResponseEntity<ApiResponse> response = orderController.deleteOrder("Bearer " + token, 1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ResponseMessages.TOKEN_EXPIRED, response.getBody().getMessage());
    }
}
