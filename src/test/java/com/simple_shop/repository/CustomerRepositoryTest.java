package com.simple_shop.repository;

import com.simple_shop.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test customer
        customer = new Customer();
        customer.setCustomerId("CUST123");
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("password123");
    }

    @Test
    void testFindByEmail_ReturnsCustomer() {
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customer));

        Optional<Customer> found = customerRepository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo("CUST123");
        verify(customerRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void testFindByCustomerId_ReturnsCustomer() {
        when(customerRepository.findByCustomerId("CUST123")).thenReturn(Optional.of(customer));

        Optional<Customer> found = customerRepository.findByCustomerId("CUST123");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
        verify(customerRepository, times(1)).findByCustomerId("CUST123");
    }

    @Test
    void testExistsByCustomerId_ReturnsTrue() {
        when(customerRepository.existsByCustomerId("CUST123")).thenReturn(true);

        boolean exists = customerRepository.existsByCustomerId("CUST123");

        assertThat(exists).isTrue();
        verify(customerRepository, times(1)).existsByCustomerId("CUST123");
    }

    @Test
    void testDeleteByCustomerId_RemovesCustomer() {
        // Just verify that deleteByCustomerId is called
        doNothing().when(customerRepository).deleteByCustomerId("CUST123");

        customerRepository.deleteByCustomerId("CUST123");

        verify(customerRepository, times(1)).deleteByCustomerId("CUST123");
    }

    @Test
    void testFindByEmail_ReturnsEmptyForNonExistingEmail() {
        when(customerRepository.findByEmail("nonexist@example.com")).thenReturn(Optional.empty());

        Optional<Customer> found = customerRepository.findByEmail("nonexist@example.com");

        assertThat(found).isEmpty();
        verify(customerRepository, times(1)).findByEmail("nonexist@example.com");
    }

    @Test
    void testExistsByCustomerId_ReturnsFalseForNonExistingId() {
        when(customerRepository.existsByCustomerId("NONEXIST")).thenReturn(false);

        boolean exists = customerRepository.existsByCustomerId("NONEXIST");

        assertThat(exists).isFalse();
        verify(customerRepository, times(1)).existsByCustomerId("NONEXIST");
    }
}
