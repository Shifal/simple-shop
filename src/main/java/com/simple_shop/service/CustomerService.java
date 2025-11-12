package com.simple_shop.service;

import com.simple_shop.dto.CustomerDTO;
import com.simple_shop.mapper.CustomerMapper;
import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import com.simple_shop.repository.CustomerRepository;
import jakarta.persistence.EntityManager; // is used here mainly for custom sequence generation and manual role insertions.
import jakarta.transaction.Transactional; // ensures atomic operations (all or nothing).
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final EntityManager entityManager;
    private final RoleService roleService;

    public CustomerService(CustomerRepository customerRepo, EntityManager entityManager, RoleService roleService) {
        this.customerRepo = customerRepo;
        this.entityManager = entityManager;
        this.roleService = roleService;
    }

    // Get all customers
    public List<CustomerDTO> getAllCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Create customer
    @Transactional
    public Optional<CustomerDTO> createCustomer(Customer customer) {
        try {
            Long nextVal = ((Number) entityManager
                    .createNativeQuery("SELECT nextval('customer_seq')")
                    .getSingleResult()).longValue();

            String prefix = "CUS-" + LocalDate.now().toString().replace("-", "");
            String formattedNumber = String.format("%04d", nextVal);
            String generatedCustomerId = prefix + "-" + formattedNumber;

            customer.setCustomerId(generatedCustomerId);

            Customer saved = customerRepo.save(customer);
            roleService.assignDefaultRole(saved);

            return Optional.of(CustomerMapper.toDTO(saved));
        } catch (DataAccessException | IllegalArgumentException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    // Update customer by customerId
    @Transactional
    public Optional<CustomerDTO> updateCustomer(String customerId, Customer updated) {
        return customerRepo.findByCustomerId(customerId)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setEmail(updated.getEmail());
                    existing.setPassword(updated.getPassword());
                    Customer saved = customerRepo.save(existing);
                    return CustomerMapper.toDTO(saved);
                });
    }

    // Delete customer by customerId
    @Transactional
    public boolean deleteCustomer(String customerId) {
        if (!customerRepo.existsByCustomerId(customerId)) {
            return false;
        }
        customerRepo.deleteByCustomerId(customerId);
        return true;
    }

    // Get customer by customerId
    public Optional<CustomerDTO> getCustomerByCustomerId(String customerId) {
        return customerRepo.findByCustomerId(customerId)
                .map(CustomerMapper::toDTO);
    }

    @Transactional // If any part fails (e.g., DB insert, role creation), the entire operation rolls back automatically.
    public Optional<CustomerDTO> createAdminCustomer(Customer customer) {
        try {
            Long nextVal = ((Number) entityManager
                    .createNativeQuery("SELECT nextval('customer_seq')")
                    .getSingleResult()).longValue();

            String prefix = "CUS-" + LocalDate.now().toString().replace("-", "");
            String formattedNumber = String.format("%04d", nextVal);
            String generatedCustomerId = prefix + "-" + formattedNumber;

            customer.setCustomerId(generatedCustomerId);

            // Save the customer
            Customer saved = customerRepo.save(customer);

            // Assign admin role explicitly
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            adminRole.setCustomer(saved);
            entityManager.persist(adminRole);

            return Optional.of(CustomerMapper.toDTO(saved));

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
