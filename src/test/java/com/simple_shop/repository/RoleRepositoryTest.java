package com.simple_shop.repository;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;
    private Role role;

    @BeforeEach
    void setUp() {
        // Create and save a customer
        customer = new Customer();
        customer.setCustomerId("CUS-1001");
        customer.setUserName("john_doe");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("pass123");
        customer.setActive(true);
        customerRepository.save(customer);

        // Create and save a role linked to customer
        role = new Role();
        role.setRoleName("ADMIN");
        role.setCustomer(customer);
        roleRepository.save(role);
    }

    @Test
    void testFindByCustomerCustomerId() {
        Optional<Role> found = roleRepository.findByCustomer_CustomerId("CUS-1001");
        assertTrue(found.isPresent());
        assertEquals("ADMIN", found.get().getRoleName());
        assertEquals("john_doe", found.get().getCustomer().getUserName());
    }

    @Test
    void testFindByCustomerCustomerIdNotFound() {
        Optional<Role> found = roleRepository.findByCustomer_CustomerId("CUS-9999");
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByCustomer() {
        roleRepository.deleteByCustomer(customer);
        Optional<Role> found = roleRepository.findByCustomer_CustomerId("CUS-1001");
        assertFalse(found.isPresent());
    }
}
