package com.simpleshop.controller;

import com.simpleshop.dto.CustomerDTO;
import com.simpleshop.model.Customer;
import com.simpleshop.response.ApiResponse;
import com.simpleshop.service.CustomerServiceInterface;
import com.simpleshop.service.RoleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerServiceInterface service;

    @Mock
    private RoleService roleService;

    @Mock
    private Jwt principal;

    @InjectMocks
    private CustomerController controller;

    private CustomerDTO customerDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customerDTO = new CustomerDTO(
                1L,
                "CUST001",
                "KC123",
                "john",
                "John",
                "Doe",
                "john@example.com",
                true
        );

        customer = new Customer();
        customer.setUserName("john");
        customer.setEmail("john@example.com");
    }

    // ------------------------------------------------------
    // GET ALL CUSTOMERS
    // ------------------------------------------------------
    @Test
    void testGetAllCustomers_AdminAccess() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.getAllCustomers()).thenReturn(Arrays.asList(customerDTO));

        ResponseEntity<ApiResponse> response = controller.getAllCustomers(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, ((List<?>) response.getBody().getData()).size());
    }

    @Test
    void testGetAllCustomers_AccessDenied() {
        when(roleService.isAdmin(principal)).thenReturn(false);

        ResponseEntity<ApiResponse> response = controller.getAllCustomers(principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    // ------------------------------------------------------
    // GET CUSTOMER BY ID
    // ------------------------------------------------------
    @Test
    void testGetCustomerByCustomerId_Found_Owner() {
        when(principal.getSubject()).thenReturn("KC123");
        when(service.getCustomerSecure("CUST001", "KC123")).thenReturn(customerDTO);

        ResponseEntity<ApiResponse> response = controller.getCustomerByCustomerId("CUST001", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testGetCustomerByCustomerId_NotFound() {
        when(principal.getSubject()).thenReturn("KC123");
        when(service.getCustomerSecure("CUST002", "KC123")).thenReturn(null);

        ResponseEntity<ApiResponse> response = controller.getCustomerByCustomerId("CUST002", principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }


    // ------------------------------------------------------
    // CREATE CUSTOMER
    // ------------------------------------------------------
    @Test
    void testCreateCustomer_Success() {
        when(service.createCustomer(any(Customer.class))).thenReturn(Optional.of(customerDTO));

        ResponseEntity<ApiResponse> response = controller.createCustomer(customer);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testCreateCustomer_Failure() {
        when(service.createCustomer(any(Customer.class))).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.createCustomer(customer);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }


    // ------------------------------------------------------
    // UPDATE CUSTOMER (USER = only self, ADMIN = anyone)
    // ------------------------------------------------------
    @Test
    void testUpdateCustomer_Success_Owner() {
        when(principal.getSubject()).thenReturn("KC123");
        when(roleService.isAdmin(principal)).thenReturn(false);
        when(service.isOwner("CUST001", "KC123")).thenReturn(true);

        when(service.updateCustomer(eq("CUST001"), any(Customer.class)))
                .thenReturn(Optional.of(customerDTO));

        ResponseEntity<ApiResponse> response =
                controller.updateCustomer("CUST001", customer, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testUpdateCustomer_Success_Admin() {
        when(principal.getSubject()).thenReturn("ADMIN123");
        when(roleService.isAdmin(principal)).thenReturn(true);

        when(service.updateCustomer(eq("CUST001"), any(Customer.class)))
                .thenReturn(Optional.of(customerDTO));

        ResponseEntity<ApiResponse> response =
                controller.updateCustomer("CUST001", customer, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(principal.getSubject()).thenReturn("KC123");
        when(roleService.isAdmin(principal)).thenReturn(false);
        when(service.isOwner("CUST999", "KC123")).thenReturn(true);

        when(service.updateCustomer(eq("CUST999"), any(Customer.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response =
                controller.updateCustomer("CUST999", customer, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testUpdateCustomer_Forbidden_NotOwner() {
        when(principal.getSubject()).thenReturn("OTHER_USER");
        when(roleService.isAdmin(principal)).thenReturn(false);
        when(service.isOwner("CUST001", "OTHER_USER")).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.updateCustomer("CUST001", customer, principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }


    // ------------------------------------------------------
    // DELETE CUSTOMER (USER self + ADMIN anyone)
    // ------------------------------------------------------
    @Test
    void testDeleteCustomer_Admin_Success() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.deleteCustomer("CUST001")).thenReturn(true);

        ResponseEntity<ApiResponse> response =
                controller.deleteCustomer("CUST001", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testDeleteCustomer_UserSelf_Success() {
        when(principal.getSubject()).thenReturn("KC123");
        when(roleService.isAdmin(principal)).thenReturn(false);
        when(service.isOwner("CUST001", "KC123")).thenReturn(true);

        when(service.deleteCustomer("CUST001")).thenReturn(true);

        ResponseEntity<ApiResponse> response =
                controller.deleteCustomer("CUST001", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testDeleteCustomer_Forbidden() {
        when(principal.getSubject()).thenReturn("USER123");
        when(roleService.isAdmin(principal)).thenReturn(false);
        when(service.isOwner("CUST001", "USER123")).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.deleteCustomer("CUST001", principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.deleteCustomer("CUST999")).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.deleteCustomer("CUST999", principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }


    // ------------------------------------------------------
    // BLOCK CUSTOMER (ADMIN only)
    // ------------------------------------------------------
    @Test
    void testBlockCustomer_Success() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.blockCustomer("CUST001")).thenReturn(true);

        ResponseEntity<ApiResponse> response =
                controller.blockCustomer("CUST001", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testBlockCustomer_NotFound() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.blockCustomer("CUST002")).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.blockCustomer("CUST002", principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testBlockCustomer_Forbidden() {
        when(roleService.isAdmin(principal)).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.blockCustomer("CUST001", principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ------------------------------------------------------
    // UNBLOCK CUSTOMER (ADMIN only)
    // ------------------------------------------------------
    @Test
    void testUnblockCustomer_Success() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.unblockCustomer("CUST001")).thenReturn(true);

        ResponseEntity<ApiResponse> response =
                controller.unblockCustomer("CUST001", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testUnblockCustomer_NotFound() {
        when(roleService.isAdmin(principal)).thenReturn(true);
        when(service.unblockCustomer("CUST002")).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.unblockCustomer("CUST002", principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUnblockCustomer_Forbidden() {
        when(roleService.isAdmin(principal)).thenReturn(false);

        ResponseEntity<ApiResponse> response =
                controller.unblockCustomer("CUST001", principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
