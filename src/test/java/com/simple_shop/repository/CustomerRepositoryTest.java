package com.simple_shop.repository;

import com.simple_shop.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId("CUS-1001");
        customer.setUserName("john_doe");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("pass123");
        customer.setActive(true);

        customerRepository.save(customer); // save to in-memory DB
    }

    @Test
    void testFindByCustomerId() {
        Optional<Customer> found = customerRepository.findByCustomerId("CUS-1001");
        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().getUserName());
        assertEquals("john@example.com", found.get().getEmail());
    }

    @Test
    void testFindByCustomerIdNotFound() {
        Optional<Customer> found = customerRepository.findByCustomerId("CUS-9999");
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveAndRetrieveCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerId("CUS-1002");
        newCustomer.setUserName("alice");
        newCustomer.setFirstName("Alice");
        newCustomer.setEmail("alice@example.com");
        newCustomer.setPassword("alicepass");
        newCustomer.setActive(true);

        Customer saved = customerRepository.save(newCustomer);
        assertNotNull(saved.getId());

        Optional<Customer> retrieved = customerRepository.findByCustomerId("CUS-1002");
        assertTrue(retrieved.isPresent());
        assertEquals("alice", retrieved.get().getUserName());
    }
}
