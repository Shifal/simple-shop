package com.simpleshop.service;

import com.simpleshop.dto.CustomerDTO;
import com.simpleshop.model.Customer;
import com.simpleshop.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository customerRepo;
    private EntityManager entityManager;
    private RoleService roleService;
    private KeycloakService keycloakService;

    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepo = mock(CustomerRepository.class);
        entityManager = mock(EntityManager.class);
        roleService = mock(RoleService.class);
        keycloakService = mock(KeycloakService.class);

        customerService = new CustomerService(customerRepo, entityManager, roleService, keycloakService);

        customer = new Customer();
        customer.setCustomerId("CUS-1001");
        customer.setUserName("john_doe");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("pass123");
        customer.setActive(true);
    }

    @Test
    void testGetAllCustomers() {
        when(customerRepo.findAll()).thenReturn(Arrays.asList(customer));

        List<CustomerDTO> customers = customerService.getAllCustomers();

        assertEquals(1, customers.size());
        assertEquals("CUS-1001", customers.get(0).getCustomerId());
    }

    @Test
    void testCreateCustomerSuccess() {
        // Mock entityManager for sequence
        when(entityManager.createNativeQuery("SELECT nextval('customer_seq')").getSingleResult()).thenReturn(1L);
        // Mock Keycloak
        when(keycloakService.createKeycloakUser(anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn("kc-123");
        // Mock repository save
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        Optional<CustomerDTO> result = customerService.createCustomer(customer);

        assertTrue(result.isPresent());
        assertEquals("CUS-", result.get().getCustomerId().substring(0, 4));
        assertEquals("john_doe", result.get().getUserName());
        verify(roleService, times(1)).assignDefaultRole(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() {
        Customer updated = new Customer();
        updated.setUserName("john_updated");
        updated.setFirstName("John");
        updated.setLastName("Doe");
        updated.setEmail("john_updated@example.com");
        updated.setPassword("newpass");
        updated.setActive(true);

        when(customerRepo.findByCustomerId("CUS-1001")).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        Optional<CustomerDTO> result = customerService.updateCustomer("CUS-1001", updated);

        assertTrue(result.isPresent());
        assertEquals("john_updated", result.get().getUserName());
        verify(keycloakService, times(1)).updateKeycloakUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDeleteCustomer() {
        customer.setKeycloakId("kc-123");
        when(customerRepo.findByCustomerId("CUS-1001")).thenReturn(Optional.of(customer));

        boolean deleted = customerService.deleteCustomer("CUS-1001");

        assertTrue(deleted);
        verify(roleService, times(1)).deleteRolesByCustomer(customer);
        verify(keycloakService, times(1)).deleteUserByKeycloakId("kc-123");
        verify(customerRepo, times(1)).delete(customer);
    }

    @Test
    void testBlockAndUnblockCustomer() {
        when(customerRepo.findByCustomerId("CUS-1001")).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        boolean blocked = customerService.blockCustomer("CUS-1001");
        assertTrue(blocked);
        assertFalse(customer.isActive());
        verify(keycloakService, times(1)).disableUser("kc-123");

        boolean unblocked = customerService.unblockCustomer("CUS-1001");
        assertTrue(unblocked);
        assertTrue(customer.isActive());
        verify(keycloakService, times(1)).enableUser("kc-123");
    }

    @Test
    void testGetCustomerSecureOwner() {
        customer.setKeycloakId("kc-123");
        when(customerRepo.findByCustomerId("CUS-1001")).thenReturn(Optional.of(customer));
        when(roleService.isAdmin("kc-123")).thenReturn(false);

        CustomerDTO dto = customerService.getCustomerSecure("CUS-1001", "kc-123");
        assertNotNull(dto);
        assertEquals("CUS-1001", dto.getCustomerId());
    }

    @Test
    void testGetCustomerSecureAdmin() {
        customer.setKeycloakId("kc-123");
        when(customerRepo.findByCustomerId("CUS-1001")).thenReturn(Optional.of(customer));
        when(roleService.isAdmin("kc-admin")).thenReturn(true);

        CustomerDTO dto = customerService.getCustomerSecure("CUS-1001", "kc-admin");
        assertNotNull(dto);
        assertEquals("CUS-1001", dto.getCustomerId());
    }

    @Test
    void testGetCustomerSecureUnauthorized() {
        customer.setKeycloakId("kc-123");
        when(customerRepo.findByCustomerId("CUS-1001")).thenReturn(Optional.of(customer));
        when(roleService.isAdmin("kc-other")).thenReturn(false);

        CustomerDTO dto = customerService.getCustomerSecure("CUS-1001", "kc-other");
        assertNotNull(dto);
        assertNull(dto.getCustomerId()); // empty DTO
    }
}
