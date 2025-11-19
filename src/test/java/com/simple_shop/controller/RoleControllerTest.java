//package com.simple_shop.controller;
//
//import com.simple_shop.constants.ResponseMessages;
//import com.simple_shop.dto.CustomerDTO;
//import com.simple_shop.model.Customer;
//import com.simple_shop.model.Role;
//import com.simple_shop.response.ApiResponse;
//import com.simple_shop.service.CustomerService;
//import com.simple_shop.service.RoleService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class RoleControllerTest {
//
//    @Mock
//    private RoleService roleService;
//
//    @Mock
//    private CustomerService customerService;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @InjectMocks
//    private RoleController roleController;
//
//    private String token;
//    private String requesterId;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        token = "Bearer validToken";
//        requesterId = "ADMIN001";
//    }
//
//    // ------------------ CREATE NEW ADMIN ------------------
//    @Test
//    void createNewAdmin_Success() {
//        Customer newCustomer = new Customer();
//        newCustomer.setName("John");
//
//        CustomerDTO customerDTO = new CustomerDTO();
//        customerDTO.setName("John");
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//        when(customerService.createAdminCustomer(any(Customer.class)))
//                .thenReturn(Optional.of(customerDTO));
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, newCustomer, null);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertTrue(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ADMIN_CREATED, response.getBody().getMessage());
//        assertEquals(customerDTO, response.getBody().getData());
//    }
//
//    @Test
//    void createNewAdmin_Failure() {
//        Customer newCustomer = new Customer();
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//        when(customerService.createAdminCustomer(newCustomer)).thenReturn(Optional.empty());
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, newCustomer, null);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ADMIN_CREATION_FAILED, response.getBody().getMessage());
//    }
//
//    // ------------------ PROMOTE EXISTING CUSTOMER ------------------
//    @Test
//    void promoteAdmin_Success() {
//        String targetCustomerId = "CUS123";
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//        when(roleService.promoteToAdmin(targetCustomerId)).thenReturn(true);
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, null, targetCustomerId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ADMIN_PROMOTED, response.getBody().getMessage());
//    }
//
//    @Test
//    void promoteAdmin_NotFound() {
//        String targetCustomerId = "CUS123";
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//        when(roleService.promoteToAdmin(targetCustomerId)).thenReturn(false);
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, null, targetCustomerId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ADMIN_PROMOTION_FAILED, response.getBody().getMessage());
//    }
//
//    // ------------------ UNAUTHORIZED / FORBIDDEN ------------------
//    @Test
//    void createOrPromoteAdmin_NotAdminRequester() {
//        Customer newCustomer = new Customer();
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(false);
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, newCustomer, null);
//
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, response.getBody().getMessage());
//    }
//
//    @Test
//    void createOrPromoteAdmin_BadRequest() {
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, null, null);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//    }
//
//    @Test
//    void createOrPromoteAdmin_InvalidToken() {
//        when(jwtUtil.extractCustomerId(anyString())).thenThrow(new RuntimeException("Invalid token"));
//
//        ResponseEntity<ApiResponse> response = roleController.createOrPromoteAdmin(token, null, null);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals("Invalid or expired token.", response.getBody().getMessage());
//    }
//
//    // ------------------ GET ROLE ------------------
//    @Test
//    void getRole_Success() {
//        String customerId = "CUS123";
//        Role role = new Role();
//        role.setRoleName("ADMIN");
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//        when(roleService.getRoleByCustomerId(customerId)).thenReturn(Optional.of(role));
//
//        ResponseEntity<ApiResponse> response = roleController.getRole(token, customerId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ROLE_FETCH_SUCCESS, response.getBody().getMessage());
//        assertEquals(role, response.getBody().getData());
//    }
//
//    @Test
//    void getRole_NotFound() {
//        String customerId = "CUS123";
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(true);
//        when(roleService.getRoleByCustomerId(customerId)).thenReturn(Optional.empty());
//
//        ResponseEntity<ApiResponse> response = roleController.getRole(token, customerId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ROLE_NOT_FOUND, response.getBody().getMessage());
//    }
//
//    @Test
//    void getRole_NotAdmin() {
//        String customerId = "CUS123";
//
//        when(jwtUtil.extractCustomerId(anyString())).thenReturn(requesterId);
//        when(roleService.isAdmin(requesterId)).thenReturn(false);
//
//        ResponseEntity<ApiResponse> response = roleController.getRole(token, customerId);
//
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals(ResponseMessages.ACCESS_DENIED_ADMIN_ONLY, response.getBody().getMessage());
//    }
//
//    @Test
//    void getRole_InvalidToken() {
//        String customerId = "CUS123";
//
//        when(jwtUtil.extractCustomerId(anyString())).thenThrow(new RuntimeException("Invalid token"));
//
//        ResponseEntity<ApiResponse> response = roleController.getRole(token, customerId);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertFalse(response.getBody().isSuccess());
//        assertEquals("Invalid or expired token.", response.getBody().getMessage());
//    }
//}
