//package com.simpleshop.service;
//
//import com.simpleshop.dto.OrderRequestDTO;
//import com.simpleshop.dto.OrderResponseDTO;
//import com.simpleshop.mapper.OrderMapper;
//import com.simpleshop.model.Order;
//import com.simpleshop.repository.CustomerRepository;
//import com.simpleshop.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class OrderService {
//
//    private final OrderRepository orderRepo;
//    private final CustomerRepository customerRepo;
//
//    public List<OrderResponseDTO> getAllOrders() {
//        return orderRepo.findAll()
//                .stream()
//                .map(OrderMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public Optional<OrderResponseDTO> getOrderById(Long id) {
//        return orderRepo.findById(id).map(OrderMapper::toDTO);
//    }
//
//    public Optional<OrderResponseDTO> placeOrder(String customerId, OrderRequestDTO requestDTO) {
//        return customerRepo.findByCustomerId(customerId).map(customer -> {
//            Order order = OrderMapper.toEntity(requestDTO, customer);
//            orderRepo.save(order);
//            return OrderMapper.toDTO(order);
//        });
//    }
//
//    public Optional<OrderResponseDTO> updateOrder(Long orderId, OrderRequestDTO dto) {
//        return orderRepo.findById(orderId).map(existingOrder -> {
//            existingOrder.setProduct(dto.getProduct());
//            existingOrder.setQuantity(dto.getQuantity());
//            existingOrder.setStatus("UPDATED");
//            orderRepo.save(existingOrder);
//            return OrderMapper.toDTO(existingOrder);
//        });
//    }
//
//    public boolean deleteOrder(Long id) {
//        if (!orderRepo.existsById(id)) {
//            return false;
//        }
//        orderRepo.deleteById(id);
//        return true;
//    }
//
//    public boolean isOrderOwnedByCustomer(Long orderId, String customerId) {
//        return orderRepo.existsByIdAndCustomer_CustomerId(orderId, customerId);
//    }
//
//}
