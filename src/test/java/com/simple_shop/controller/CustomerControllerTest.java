package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.model.Customer;
import com.simple_shop.service.CustomerService;
import com.simple_shop.service.RoleService;
import com.simple_shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId("CUST123");
        customer.setName("John Doe");

        customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("CUST123");
        customerDTO.setName("John Doe");
    }

    @Test
    void testGetAllCustomers_AsAdmin_Success() throws Exception {
        Mockito.when(jwtUtil.extractCustomerId(anyString())).thenReturn("ADMIN1");
        Mockito.when(roleService.isAdmin("ADMIN1")).thenReturn(true);
        Mockito.when(customerService.getAllCustomers()).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessages.FETCH_SUCCESS));
    }

    @Test
    void testGetAllCustomers_AsNonAdmin_Forbidden() throws Exception {
        Mockito.when(jwtUtil.extractCustomerId(anyString())).thenReturn("USER1");
        Mockito.when(roleService.isAdmin("USER1")).thenReturn(false);

        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ResponseMessages.ACCESS_DENIED_ADMIN_ONLY));
    }

    @Test
    void testGetAllCustomers_EmptyList() throws Exception {
        Mockito.when(jwtUtil.extractCustomerId(anyString())).thenReturn("ADMIN1");
        Mockito.when(roleService.isAdmin("ADMIN1")).thenReturn(true);
        Mockito.when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessages.NO_CUSTOMERS_FOUND))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllCustomers_TokenError() throws Exception {
        Mockito.when(jwtUtil.extractCustomerId(anyString())).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ResponseMessages.TOKEN_EXPIRED));
    }

    @Test
    void testGetCustomerById_Success() throws Exception {
        Mockito.when(customerService.getCustomerByCustomerId("CUST123"))
                .thenReturn(Optional.of(customerDTO));

        mockMvc.perform(get("/api/customers/CUST123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessages.FETCH_SUCCESS));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        Mockito.when(customerService.getCustomerByCustomerId("CUST123"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/CUST123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_NOT_FOUND));
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        Mockito.when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(Optional.of(customerDTO));

        String json = "{\"customerId\":\"CUST123\",\"name\":\"John Doe\"}";

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_CREATED));
    }

    @Test
    void testCreateCustomer_Failure() throws Exception {
        Mockito.when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(Optional.empty());

        String json = "{\"customerId\":\"CUST123\",\"name\":\"John Doe\"}";

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_CREATION_FAILED));
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        Mockito.when(customerService.updateCustomer(anyString(), any(Customer.class)))
                .thenReturn(Optional.of(customerDTO));

        String json = "{\"name\":\"Updated Name\"}";

        mockMvc.perform(put("/api/customers/CUST123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_UPDATED));
    }

    @Test
    void testUpdateCustomer_NotFound() throws Exception {
        Mockito.when(customerService.updateCustomer(anyString(), any(Customer.class)))
                .thenReturn(Optional.empty());

        String json = "{\"name\":\"Updated Name\"}";

        mockMvc.perform(put("/api/customers/CUST123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_NOT_FOUND));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        Mockito.when(customerService.deleteCustomer("CUST123")).thenReturn(true);

        mockMvc.perform(delete("/api/customers/CUST123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_DELETED));
    }

    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        Mockito.when(customerService.deleteCustomer("CUST123")).thenReturn(false);

        mockMvc.perform(delete("/api/customers/CUST123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ResponseMessages.CUSTOMER_NOT_FOUND));
    }
}
