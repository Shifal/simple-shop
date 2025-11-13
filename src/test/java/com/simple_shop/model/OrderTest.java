package com.simple_shop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Order order = new Order();

        order.setId(1L);
        order.setProduct("Laptop");
        order.setQuantity(2);
        order.setStatus("PLACED");

        Customer customer = new Customer(1L, "CUS-0001", "John Doe", "john@example.com", "pass");
        order.setCustomer(customer);

        assertEquals(1L, order.getId());
        assertEquals("Laptop", order.getProduct());
        assertEquals(2, order.getQuantity());
        assertEquals("PLACED", order.getStatus());
        assertEquals(customer, order.getCustomer());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Customer customer = new Customer(1L, "CUS-0002", "Alice", "alice@example.com", "pass");

        Order order = new Order(1L, "Mouse", 5, "SHIPPED", customer);

        assertEquals(1L, order.getId());
        assertEquals("Mouse", order.getProduct());
        assertEquals(5, order.getQuantity());
        assertEquals("SHIPPED", order.getStatus());
        assertEquals(customer, order.getCustomer());
    }

    @Test
    void testBuilder() {
        Customer customer = new Customer(1L, "CUS-0003", "Bob", "bob@example.com", "pass");

        Order order = Order.builder()
                .id(1L)
                .product("Keyboard")
                .quantity(3)
                .status("DELIVERED")
                .customer(customer)
                .build();

        assertEquals(1L, order.getId());
        assertEquals("Keyboard", order.getProduct());
        assertEquals(3, order.getQuantity());
        assertEquals("DELIVERED", order.getStatus());
        assertEquals(customer, order.getCustomer());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer customer = new Customer(1L, "CUS-0004", "Charlie", "charlie@example.com", "pass");

        Order o1 = new Order(1L, "Monitor", 1, "PLACED", customer);
        Order o2 = new Order(1L, "Monitor", 1, "PLACED", customer);
        Order o3 = new Order(2L, "Monitor", 1, "PLACED", customer);

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertNotEquals(o1, o3);
    }

    @Test
    void testToString() {
        Customer customer = new Customer(1L, "CUS-0005", "David", "david@example.com", "pass");

        Order order = new Order(1L, "Tablet", 4, "SHIPPED", customer);
        String str = order.toString();

        assertTrue(str.contains("Tablet"));
        assertTrue(str.contains("4"));
        assertTrue(str.contains("SHIPPED"));
    }
}
