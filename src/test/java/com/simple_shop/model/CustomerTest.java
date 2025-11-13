package com.simple_shop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Customer customer = new Customer();

        customer.setId(1L);
        customer.setCustomerId("CUS-0001");
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("pass123");

        assertEquals(1L, customer.getId());
        assertEquals("CUS-0001", customer.getCustomerId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("pass123", customer.getPassword());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Customer customer = new Customer(1L, "CUS-0002", "Alice", "alice@example.com", "password");

        assertEquals(1L, customer.getId());
        assertEquals("CUS-0002", customer.getCustomerId());
        assertEquals("Alice", customer.getName());
        assertEquals("alice@example.com", customer.getEmail());
        assertEquals("password", customer.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer c1 = new Customer(1L, "CUS-0003", "Bob", "bob@example.com", "pass");
        Customer c2 = new Customer(1L, "CUS-0003", "Bob", "bob@example.com", "pass");
        Customer c3 = new Customer(2L, "CUS-0004", "Charlie", "charlie@example.com", "pass");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }

    @Test
    void testToString() {
        Customer customer = new Customer(1L, "CUS-0005", "David", "david@example.com", "pass");
        String str = customer.toString();

        assertTrue(str.contains("CUS-0005"));
        assertTrue(str.contains("David"));
        assertTrue(str.contains("david@example.com"));
    }
}
