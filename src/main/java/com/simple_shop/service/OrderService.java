package com.simple_shop.service;

import com.simple_shop.Model.Customer;
import com.simple_shop.Model.Order;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;

    public OrderService(OrderRepository orderRepo, CustomerRepository customerRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order placeOrder(Long customerId, Order order) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        order.setCustomer(customer);
        order.setStatus("CREATED");
        orderRepo.save(order);

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                order.setStatus("PROCESSING");
                orderRepo.save(order);
                Thread.sleep(3000);
                order.setStatus("COMPLETED");
                orderRepo.save(order);
            } catch (InterruptedException ignored) {}
        }).start();

        return order;
    }

    public Optional<Order> updateOrder(Long orderId, Order updatedOrder) {
        return orderRepo.findById(orderId).map(existingOrder -> {
            // update fields
            existingOrder.setProduct(updatedOrder.getProduct());
            existingOrder.setQuantity(updatedOrder.getQuantity());
            existingOrder.setStatus("UPDATED");

            orderRepo.save(existingOrder);

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    existingOrder.setStatus("PROCESSING");
                    orderRepo.save(existingOrder);
                    Thread.sleep(3000);
                    existingOrder.setStatus("COMPLETED");
                    orderRepo.save(existingOrder);
                } catch (InterruptedException ignored) {}
            }).start();

            return existingOrder;
        });
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepo.findById(id);
    }

    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }

    public boolean isOrderOwnedByCustomer(Long orderId, Long customerId) {
        return orderRepo.findById(orderId)
                .map(order -> order.getCustomer().getId().equals(customerId))
                .orElse(false);
    }
}
