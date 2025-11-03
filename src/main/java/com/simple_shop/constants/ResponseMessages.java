package com.simple_shop.constants;

public class ResponseMessages {

    // Success Messages
    public static final String LOGIN_SUCCESS = "Login successful.";
    public static final String LOGOUT_SUCCESS = "Logged out successfully.";

    public static final String CUSTOMER_CREATED = "Customer created successfully.";
    public static final String CUSTOMER_UPDATED = "Customer updated successfully.";
    public static final String CUSTOMER_DELETED = "Customer deleted successfully.";
    public static final String FETCH_SUCCESS = "Data fetched successfully.";
    public static final String NO_CUSTOMERS_FOUND = "No customers found.";
    public static final String CUSTOMER_NOT_FOUND = "Customer not found.";


    public static final String ORDER_CREATED = "Order placed successfully.";
    public static final String ORDER_UPDATED = "Order updated successfully.";
    public static final String ORDER_DELETED = "Order deleted successfully.";
    public static final String ORDER_FETCH_SUCCESS = "Order fetched successfully.";
    public static final String NO_ORDERS_FOUND = "No orders found in the database.";

    // Error Messages
    public static final String INVALID_PASSWORD = "Invalid password. Please try again.";
    public static final String USER_NOT_FOUND = "User not found. Please check your email.";
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error occurred. Please try again later.";


    public static final String CUSTOMER_CREATION_FAILED = "Failed to create Customer.";

    public static final String ORDER_CREATION_FAILED = "Failed to create order.";
    public static final String ORDER_ACCESS_DENIED = "You are not allowed to modify this order.";
    public static final String TOKEN_EXPIRED = "Token expired. Please login again.";
}
