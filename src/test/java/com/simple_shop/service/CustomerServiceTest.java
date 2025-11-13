package com.simple_shop.service;

import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.model.Customer;
import com.simple_shop.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private EntityManager entityManager;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = createCustomer("John Doe", "john@example.com", "password123");
    }

    // ====================== Helper Methods ======================
    private Customer createCustomer(String name, String email, String password) {
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setPassword(password);
        return c;
    }

    private Query mockNextValQuery(long seqValue) {
        Query queryMock = mock(Query.class);
        when(entityManager.createNativeQuery("SELECT nextval('customer_seq')")).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(seqValue);
        return queryMock;
    }

    // ====================== getAllCustomers ======================
    @Test
    void testGetAllCustomers() {
        Customer savedCustomer = createCustomer("John Doe", "john@example.com", "password123");
        savedCustomer.setCustomerId("CUS-20251112-0001");

        when(customerRepo.findAll()).thenReturn(List.of(savedCustomer));

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("CUS-20251112-0001", result.get(0).getCustomerId());
    }

    // ====================== createCustomer ======================
    @Test
    void testCreateCustomer_Success() {
        mockNextValQuery(1L);

        Customer savedCustomer = createCustomer(customer.getName(), customer.getEmail(), customer.getPassword());
        savedCustomer.setCustomerId("CUS-20251112-0001");
        when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);

        Optional<CustomerDTO> result = customerService.createCustomer(customer);

        assertTrue(result.isPresent());
        assertEquals("CUS-20251112-0001", result.get().getCustomerId());
        verify(roleService, times(1)).assignDefaultRole(any(Customer.class));
    }

    @Test
    void testCreateCustomer_Failure() {
        Query queryMock = mock(Query.class);
        when(entityManager.createNativeQuery("SELECT nextval('customer_seq')")).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenThrow(new IllegalArgumentException("DB error"));

        Optional<CustomerDTO> result = customerService.createCustomer(customer);

        assertFalse(result.isPresent());
    }

    // ====================== updateCustomer ======================
    @Test
    void testUpdateCustomer_Success() {
        Customer existing = createCustomer("Old Name", "old@example.com", "oldpass");
        existing.setCustomerId("CUS-0001");

        when(customerRepo.findByCustomerId("CUS-0001")).thenReturn(Optional.of(existing));
        when(customerRepo.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer updated = createCustomer("New Name", "new@example.com", "newpass");

        Optional<CustomerDTO> result = customerService.updateCustomer("CUS-0001", updated);

        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        assertEquals("new@example.com", result.get().getEmail());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerRepo.findByCustomerId("CUS-9999")).thenReturn(Optional.empty());

        Optional<CustomerDTO> result = customerService.updateCustomer("CUS-9999", customer);

        assertFalse(result.isPresent());
    }

    // ====================== deleteCustomer ======================
    @ParameterizedTest
    @CsvSource({
            "CUS-0001,true",
            "CUS-9999,false"
    })
    void testDeleteCustomer(String customerId, boolean exists) {
        when(customerRepo.existsByCustomerId(customerId)).thenReturn(exists);
        if (exists) {
            doNothing().when(customerRepo).deleteByCustomerId(customerId);
        }

        boolean result = customerService.deleteCustomer(customerId);
        assertEquals(exists, result);
    }

    // ====================== getCustomerByCustomerId ======================
    @ParameterizedTest
    @CsvSource({
            "CUS-0001,true",
            "CUS-9999,false"
    })
    void testGetCustomerByCustomerId(String customerId, boolean exists) {
        if (exists) {
            Customer c = createCustomer("John", "john@example.com", "pass");
            c.setCustomerId(customerId);
            when(customerRepo.findByCustomerId(customerId)).thenReturn(Optional.of(c));
        } else {
            when(customerRepo.findByCustomerId(customerId)).thenReturn(Optional.empty());
        }

        Optional<CustomerDTO> result = customerService.getCustomerByCustomerId(customerId);
        assertEquals(exists, result.isPresent());
    }

    // ====================== createAdminCustomer ======================
    @Test
    void testCreateAdminCustomer_Success() {
        mockNextValQuery(1L);

        Customer savedCustomer = createCustomer(customer.getName(), customer.getEmail(), customer.getPassword());
        savedCustomer.setCustomerId("CUS-20251112-0001");
        when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);

        Optional<CustomerDTO> result = customerService.createAdminCustomer(customer);

        assertTrue(result.isPresent());
        assertEquals("CUS-20251112-0001", result.get().getCustomerId());
    }

    @Test
    void testCreateAdminCustomer_Failure() {
        when(entityManager.createNativeQuery("SELECT nextval('customer_seq')")).thenThrow(RuntimeException.class);

        Optional<CustomerDTO> result = customerService.createAdminCustomer(customer);

        assertFalse(result.isPresent());
    }
}
