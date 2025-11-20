package com.simpleshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerId("CUS-0001");
        customer.setKeycloakId("kc-123");
        customer.setUserName("john_doe");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("pass123");
        customer.setActive(true);
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        assertEquals(1L, customer.getId());
        assertEquals("CUS-0001", customer.getCustomerId());
        assertEquals("kc-123", customer.getKeycloakId());
        assertEquals("john_doe", customer.getUserName());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("pass123", customer.getPassword());
        assertTrue(customer.isActive());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Customer c = new Customer(
                2L, "CUS-0002", "kc-456", "alice", "Alice", "Smith",
                "alice@example.com", "password", false
        );

        assertEquals(2L, c.getId());
        assertEquals("CUS-0002", c.getCustomerId());
        assertEquals("kc-456", c.getKeycloakId());
        assertEquals("alice", c.getUserName());
        assertEquals("Alice", c.getFirstName());
        assertEquals("Smith", c.getLastName());
        assertEquals("alice@example.com", c.getEmail());
        assertEquals("password", c.getPassword());
        assertFalse(c.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer c1 = new Customer(1L, "CUS-0003", "kc-789", "bob", "Bob", "Brown",
                "bob@example.com", "pass", true);
        Customer c2 = new Customer(1L, "CUS-0003", "kc-789", "bob", "Bob", "Brown",
                "bob@example.com", "pass", true);
        Customer c3 = new Customer(2L, "CUS-0004", "kc-101", "charlie", "Charlie", "Black",
                "charlie@example.com", "pass", true);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }

    @Test
    void testToString() {
        String str = customer.toString();
        assertTrue(str.contains("CUS-0001"));
        assertTrue(str.contains("John"));
        assertTrue(str.contains("john@example.com"));
        assertTrue(str.contains("kc-123"));
    }
}
