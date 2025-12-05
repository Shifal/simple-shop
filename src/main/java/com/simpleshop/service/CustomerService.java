package com.simpleshop.service;

import com.simpleshop.dto.CustomerDTO;
import com.simpleshop.mapper.CustomerMapper;
import com.simpleshop.model.Customer;
import com.simpleshop.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService implements CustomerServiceInterface {

    private final CustomerRepository customerRepo;
    private final EntityManager entityManager;
    private final RoleService roleService;
    private final KeycloakService keycloakService;

    public CustomerService(CustomerRepository customerRepo, EntityManager entityManager, RoleService roleService, KeycloakService keycloakService) {
        this.customerRepo = customerRepo;
        this.entityManager = entityManager;
        this.roleService = roleService;
        this.keycloakService = keycloakService;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepo.findAll().stream().map(CustomerMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CustomerDTO> createCustomer(Customer customer) {
        try {

            String uuid = UUID.randomUUID().toString().replace("-", "");
            String lastDigits = uuid.substring(uuid.length() - 7).toUpperCase();

            String prefix = "CUS_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
            String generatedCustomerId = prefix + "_" + lastDigits;

            System.out.println("generatedCustomerId................"+ generatedCustomerId);

            customer.setCustomerId(generatedCustomerId);

            String kcId = keycloakService.createKeycloakUser(
                    customer.getUserName(),
                    customer.getEmail(),
                    customer.getPassword(),
                    "USER",
                    customer.isActive(),
                    customer.getFirstName(),
                    customer.getLastName()
            );
            System.out.println("kcId................"+ kcId);

            if (kcId == null) {
                System.out.println("kcID is null");
                return Optional.empty();
            }

            System.out.println("kcId................"+ kcId);
            customer.setKeycloakId(kcId);

            System.out.println("customer................"+ customer);
            Customer saved = customerRepo.save(customer);
            System.out.println("saved................"+ saved);
            roleService.assignDefaultRole(saved);
            return Optional.of(CustomerMapper.toDTO(saved));

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    @Override
    @Transactional
    public Optional<CustomerDTO> updateCustomer(String customerId, Customer updated) {
        try {
            return customerRepo.findByCustomerId(customerId).map(existing -> {

                // Update DB fields
                existing.setUserName(updated.getUserName());
                existing.setEmail(updated.getEmail());
                existing.setFirstName(updated.getFirstName());
                existing.setLastName(updated.getLastName());

                Customer saved = customerRepo.save(existing);

                // Update Keycloak safely
                keycloakService.updateKeycloakUser(
                        saved.getKeycloakId(),
                        saved.getUserName(),
                        saved.getEmail(),
                        saved.getFirstName(),
                        saved.getLastName()
                );

                // Return DTO wrapped in Optional ONCE
                return CustomerMapper.toDTO(saved);

            });

        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean deleteCustomer(String customerId) {

        Optional<Customer> opt = customerRepo.findByCustomerId(customerId);
        if (opt.isEmpty()) return false;

        Customer customer = opt.get();

        roleService.deleteRolesByCustomer(customer);

        if (customer.getKeycloakId() != null) {
            keycloakService.deleteUserByKeycloakId(customer.getKeycloakId());
        }

        customerRepo.delete(customer);

        return true;
    }

    @Override
    public CustomerDTO getCustomerSecure(String customerId, String requesterKcId) {

        Customer customer = customerRepo.findByCustomerId(customerId).orElse(null);

        if (customer == null) return null;

        boolean isOwner = requesterKcId.equals(customer.getKeycloakId());
        boolean isAdmin = roleService.isAdmin(requesterKcId);

        if (!isOwner && !isAdmin) return new CustomerDTO();

        return CustomerMapper.toDTO(customer);
    }

    @Override
    public CustomerDTO getCustomerByKeycloakId(String keycloakId) {
        return customerRepo.findByKeycloakId(keycloakId)
                .map(customer -> new CustomerDTO(
                        customer.getId(),
                        customer.getCustomerId(),
                        customer.getKeycloakId(),
                        customer.getUserName(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.isActive()
                ))
                .orElse(null);
    }

    @Override
    @Transactional
    public boolean blockCustomer(String customerId) {

        Optional<Customer> opt = customerRepo.findByCustomerId(customerId);
        if (opt.isEmpty()) return false;

        Customer customer = opt.get();

        // Block in Keycloak
        keycloakService.disableUser(customer.getKeycloakId());

        // Update DB
        customer.setActive(false);
        customerRepo.save(customer);

        return true;
    }

    @Override
    @Transactional
    public boolean unblockCustomer(String customerId) {

        Optional<Customer> opt = customerRepo.findByCustomerId(customerId);
        if (opt.isEmpty()) return false;

        Customer customer = opt.get();

        // Unblock in Keycloak
        keycloakService.enableUser(customer.getKeycloakId());

        // Update DB
        customer.setActive(true);
        customerRepo.save(customer);

        return true;
    }

    public boolean isOwner(String customerId, String requesterKcId) {
        return customerRepo.findByCustomerId(customerId)
                .map(c -> c.getKeycloakId().equals(requesterKcId))
                .orElse(false);
    }

}
