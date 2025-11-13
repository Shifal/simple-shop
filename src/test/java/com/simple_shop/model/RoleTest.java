package com.simple_shop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Role role = new Role();

        role.setId(1L);
        role.setRoleName("USER");

        Customer customer = new Customer(1L, "CUS-0001", "John Doe", "john@example.com", "pass");
        role.setCustomer(customer);

        assertEquals(1L, role.getId());
        assertEquals("USER", role.getRoleName());
        assertEquals(customer, role.getCustomer());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Customer customer = new Customer(1L, "CUS-0002", "Alice", "alice@example.com", "pass");

        Role role = new Role(1L, "ADMIN", customer);

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getRoleName());
        assertEquals(customer, role.getCustomer());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer customer = new Customer(1L, "CUS-0003", "Bob", "bob@example.com", "pass");

        Role r1 = new Role(1L, "USER", customer);
        Role r2 = new Role(1L, "USER", customer);
        Role r3 = new Role(2L, "ADMIN", customer);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void testToString() {
        Customer customer = new Customer(1L, "CUS-0004", "Charlie", "charlie@example.com", "pass");

        Role role = new Role(1L, "USER", customer);
        String str = role.toString();

        assertTrue(str.contains("USER"));
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Charlie"));
    }
}
