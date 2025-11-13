package com.simple_shop.controller;

import com.simple_shop.constants.ResponseMessages;
import com.simple_shop.model.Customer;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.response.ApiResponse;
import com.simple_shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        // customerId is a String in model, so here i have used "1" (String) here
        customer.setCustomerId("1");
        customer.setEmail("test@example.com");
        customer.setPassword("password123");
    }

    // 1. Test: Missing email or password (blank strings)
    @Test
    void testLogin_MissingCredentials_Blank() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail("");
        loginRequest.setPassword("");

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResponseMessages.INVALID_CREDENTIALS, response.getBody().getMessage());
    }

    @Test
    void testLogin_BlankEmailAndPassword_ReturnsBadRequest() {
        Customer request = new Customer();
        request.setEmail(" ");
        request.setPassword(" ");

        ResponseEntity<ApiResponse> response = authController.login(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ResponseMessages.INVALID_CREDENTIALS, response.getBody().getMessage());
    }


    // 1a. Test: Missing password is blank specifically
    @Test
    void testLogin_PasswordBlank() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("   ");

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResponseMessages.INVALID_CREDENTIALS, response.getBody().getMessage());
    }

    // 2. Test: User not found
    @Test
    void testLogin_UserNotFound() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail("unknown@example.com");
        loginRequest.setPassword("password123");

        when(customerRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResponseMessages.USER_NOT_FOUND, response.getBody().getMessage());
    }

    // 3. Test: Invalid password
    @Test
    void testLogin_InvalidPassword() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResponseMessages.INVALID_PASSWORD, response.getBody().getMessage());
    }

    // 4. Test: Successful login
    @Test
    void testLogin_Success() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));
        when(jwtUtil.generateToken("1")).thenReturn("mockedToken123");

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessages.LOGIN_SUCCESS, response.getBody().getMessage());
        assertEquals("mockedToken123", response.getBody().getData());
    }

    // 5. Test: Logout success
    @Test
    void testLogout_Success() {
        ResponseEntity<ApiResponse> response = authController.logout();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessages.LOGOUT_SUCCESS, response.getBody().getMessage());
    }

    // 6. Test: Login - null email and password
    @Test
    void testLogin_NullEmailAndPassword() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail(null);
        loginRequest.setPassword(null);

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResponseMessages.INVALID_CREDENTIALS, response.getBody().getMessage());
    }

    // 7. Test: Check that token generation called once on success
    @Test
    void testTokenGenerationCalledOnceOnSuccess() {
        Customer loginRequest = new Customer();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));
        when(jwtUtil.generateToken("1")).thenReturn("mockedToken123");

        authController.login(loginRequest);

        verify(jwtUtil, times(1)).generateToken("1");
    }
}
