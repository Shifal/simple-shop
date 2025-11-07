package com.simple_shop.service;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepo;
    private final CustomerRepository customerRepo;

    public RoleService(RoleRepository roleRepo, CustomerRepository customerRepo) {
        this.roleRepo = roleRepo;
        this.customerRepo = customerRepo;
    }

    // Assign default USER role when customer is created
    @Transactional
    public void assignDefaultRole(Customer customer) {
        Role role = new Role();
        role.setRoleName("USER");
        role.setCustomer(customer);
        roleRepo.save(role);
    }

    // Only ADMIN can create another ADMIN
    @Transactional
    public Optional<Role> createAdminRole(String adminCustomerId, String targetCustomerId) {
        Optional<Role> adminRole = roleRepo.findByCustomer_CustomerId(adminCustomerId);
        if (adminRole.isEmpty() || !adminRole.get().getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access denied. Only ADMIN can create another ADMIN.");
        }

        Customer targetCustomer = customerRepo.findByCustomerId(targetCustomerId)
                .orElseThrow(() -> new RuntimeException("Target customer not found."));

        Role newAdminRole = new Role();
        newAdminRole.setRoleName("ADMIN");
        newAdminRole.setCustomer(targetCustomer);

        return Optional.of(roleRepo.save(newAdminRole));
    }

    public Optional<Role> getRoleByCustomerId(String customerId) {
        return roleRepo.findByCustomer_CustomerId(customerId);
    }

    public boolean isAdmin(String customerId) {
        return roleRepo.findByCustomer_CustomerId(customerId)
                .map(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))
                .orElse(false);
    }

    @Transactional
    public boolean promoteToAdmin(String customerId) {
        Optional<Role> existingRole = roleRepo.findByCustomer_CustomerId(customerId);

        if (existingRole.isPresent()) {
            Role role = existingRole.get();
            if ("ADMIN".equals(role.getRoleName())) {
                return false;
            }
            role.setRoleName("ADMIN");
            roleRepo.save(role);
            return true;
        }

        return false;
    }


}
