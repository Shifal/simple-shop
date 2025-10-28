package com.simple_shop.Controller;

import com.simple_shop.Model.Order;
import com.simple_shop.service.OrderService;
import com.simple_shop.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController                         // tells Spring this class handles REST API requests and automatically returns data as JSON.
@RequestMapping("/api/orders")        //means every method in this controller will start with:
public class OrderController {

    private final OrderService orderService;        //contains the business logic for handling orders (fetching, saving, updating, deleting).
    private final JwtUtil jwtUtil;                  //handles JWT (JSON Web Token) authentication — like extracting customer ID from the token, checking if a token is expired, etc.

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();        // It uses the service layer (orderService.getAllOrders()) to get all orders from the database.
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {    // The {id} part is taken from the URL using @PathVariable.
        return orderService.getOrderById(id)          // The controller calls the service method getOrderById(id):  If found → returns the order.
                .orElseThrow(() -> new RuntimeException("Order not found"));     //If not found → throws RuntimeException("Order not found").
    }

    @PostMapping("/place/{customerId}")
    public ResponseEntity<?> placeOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long customerId,
            @RequestBody Order order) {

        token = token.replace("Bearer ", ""); // Removes "Bearer " to get the pure token string. clean token
        Long tokenCustomerId = jwtUtil.extractCustomerId(token);  // This reads the customer ID encoded inside the JWT.

        if (!tokenCustomerId.equals(customerId)) {   // If the token’s customer ID matches the one in the URL → proceed.
            return ResponseEntity.status(403).body("You are not allowed to place order for another customer!");
        }                                            //If not → return 403 Forbidden.

        if (jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(401).body("Token expired, please login again.");
        }                                           //If token expired → return 401 Unauthorized.

        Order placedOrder = orderService.placeOrder(customerId, order);
        return ResponseEntity.ok(placedOrder);
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestBody Order order) {

        token = token.replace("Bearer ", ""); // remove prefix
        Long customerId = jwtUtil.extractCustomerId(token);

        boolean canUpdate = orderService.isOrderOwnedByCustomer(orderId, customerId);

        if (!canUpdate) {
            return ResponseEntity.status(403).body("You are not allowed to update this order!");
        }

        return orderService.updateOrder(orderId, order)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
