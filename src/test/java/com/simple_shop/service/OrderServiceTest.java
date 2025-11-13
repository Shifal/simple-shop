package com.simple_shop.service;

import com.simple_shop.dto.OrderRequestDTO;
import com.simple_shop.dto.OrderResponseDTO;
import com.simple_shop.model.Customer;
import com.simple_shop.model.Order;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Order order;
    private OrderRequestDTO orderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = createCustomer("CUST123");
        order = createOrder(1L, "Laptop", 2, "NEW", customer);
        orderRequest = createOrderRequest("Mouse", 5);

        // default stubs for saving
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ====================== Helper Methods ======================
    private Customer createCustomer(String customerId) {
        Customer c = new Customer();
        c.setCustomerId(customerId);
        return c;
    }

    private Order createOrder(Long id, String product, int quantity, String status, Customer customer) {
        Order o = new Order();
        o.setId(id);
        o.setProduct(product);
        o.setQuantity(quantity);
        o.setStatus(status);
        o.setCustomer(customer);
        return o;
    }

    private OrderRequestDTO createOrderRequest(String product, int quantity) {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setProduct(product);
        dto.setQuantity(quantity);
        return dto;
    }

    // ====================== getAllOrders ======================
    @Test
    void testGetAllOrders_WithOrders() {
        when(orderRepo.findAll()).thenReturn(List.of(order));

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getProduct());
    }

    @Test
    void testGetAllOrders_Empty() {
        when(orderRepo.findAll()).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertTrue(result.isEmpty());
    }

    // ====================== getOrderById ======================
    @Test
    void testGetOrderById_Found() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        Optional<OrderResponseDTO> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getProduct());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepo.findById(99L)).thenReturn(Optional.empty());

        Optional<OrderResponseDTO> result = orderService.getOrderById(99L);

        assertFalse(result.isPresent());
    }

    // ====================== placeOrder ======================
    @Test
    void testPlaceOrder_Success() {
        when(customerRepo.findByCustomerId("CUST123")).thenReturn(Optional.of(customer));

        Optional<OrderResponseDTO> result = orderService.placeOrder("CUST123", orderRequest);

        assertTrue(result.isPresent());
        assertEquals("Mouse", result.get().getProduct());
        assertEquals(5, result.get().getQuantity());
    }

    @Test
    void testPlaceOrder_CustomerNotFound() {
        when(customerRepo.findByCustomerId("CUST999")).thenReturn(Optional.empty());

        Optional<OrderResponseDTO> result = orderService.placeOrder("CUST999", orderRequest);

        assertFalse(result.isPresent());
    }

    // ====================== updateOrder ======================
    @Test
    void testUpdateOrder_Success() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        OrderRequestDTO updateRequest = createOrderRequest("Keyboard", 3);
        Optional<OrderResponseDTO> result = orderService.updateOrder(1L, updateRequest);

        assertTrue(result.isPresent());
        assertEquals("Keyboard", result.get().getProduct());
        assertEquals(3, result.get().getQuantity());
        assertEquals("UPDATED", result.get().getStatus());
    }

    @Test
    void testUpdateOrder_NotFound() {
        when(orderRepo.findById(99L)).thenReturn(Optional.empty());

        Optional<OrderResponseDTO> result = orderService.updateOrder(99L, orderRequest);

        assertFalse(result.isPresent());
    }

    // ====================== deleteOrder ======================
    @ParameterizedTest
    @CsvSource({
            "1,true",
            "99,false"
    })
    void testDeleteOrder(Long orderId, boolean exists) {
        when(orderRepo.existsById(orderId)).thenReturn(exists);
        if (exists) {
            doNothing().when(orderRepo).deleteById(orderId);
        }

        boolean result = orderService.deleteOrder(orderId);
        assertEquals(exists, result);
    }

    // ====================== isOrderOwnedByCustomer ======================
    @ParameterizedTest
    @CsvSource({
            "1,CUST123,true",
            "1,CUST999,false"
    })
    void testIsOrderOwnedByCustomer(Long orderId, String customerId, boolean expected) {
        when(orderRepo.existsByIdAndCustomer_CustomerId(orderId, customerId)).thenReturn(expected);

        boolean result = orderService.isOrderOwnedByCustomer(orderId, customerId);
        assertEquals(expected, result);
    }
}
