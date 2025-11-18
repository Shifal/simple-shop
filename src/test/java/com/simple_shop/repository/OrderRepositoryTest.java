package com.simple_shop.repository;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private Customer customer;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        // Setup customer and order
        customer = new Customer();
        customer.setCustomerId("cust123");
        customer.setName("John Doe");

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
    }

    @Test
    void testExistsByIdAndCustomer_CustomerId_ReturnsTrue() {
        // Mock behavior
        when(orderRepository.existsByIdAndCustomer_CustomerId(1L, "cust123")).thenReturn(true);

        // Call the method
        boolean exists = orderRepository.existsByIdAndCustomer_CustomerId(1L, "cust123");

        // Verify
        assertThat(exists).isTrue();
        verify(orderRepository, times(1)).existsByIdAndCustomer_CustomerId(1L, "cust123");
    }

    @Test
    void testExistsByIdAndCustomer_CustomerId_ReturnsFalse_ForWrongOrderId() {
        when(orderRepository.existsByIdAndCustomer_CustomerId(999L, "cust123")).thenReturn(false);

        boolean exists = orderRepository.existsByIdAndCustomer_CustomerId(999L, "cust123");

        assertThat(exists).isFalse();
        verify(orderRepository, times(1)).existsByIdAndCustomer_CustomerId(999L, "cust123");
    }

    @Test
    void testExistsByIdAndCustomer_CustomerId_ReturnsFalse_ForWrongCustomerId() {
        when(orderRepository.existsByIdAndCustomer_CustomerId(1L, "wrongCustId")).thenReturn(false);

        boolean exists = orderRepository.existsByIdAndCustomer_CustomerId(1L, "wrongCustId");

        assertThat(exists).isFalse();
        verify(orderRepository, times(1)).existsByIdAndCustomer_CustomerId(1L, "wrongCustId");
    }
}
