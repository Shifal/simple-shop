package com.simpleshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Initialize a Customer first
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

        // Initialize Role
        role = new Role();
        role.setId(1L);
        role.setRoleName("ADMIN");
        role.setCustomer(customer);
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getRoleName());
        assertNotNull(role.getCustomer());
        assertEquals("CUS-0001", role.getCustomer().getCustomerId());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Role r = new Role(2L, "USER", customer);

        assertEquals(2L, r.getId());
        assertEquals("USER", r.getRoleName());
        assertNotNull(r.getCustomer());
        assertEquals("CUS-0001", r.getCustomer().getCustomerId());
    }

    @Test
    void testEqualsAndHashCode() {
        Role r1 = new Role(1L, "ADMIN", customer);
        Role r2 = new Role(1L, "ADMIN", customer);
        Role r3 = new Role(2L, "USER", customer);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void testToString() {
        String str = role.toString();
        assertTrue(str.contains("ADMIN"));
        assertTrue(str.contains("CUS-0001"));
    }
}
