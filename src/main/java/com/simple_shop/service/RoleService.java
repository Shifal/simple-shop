package com.simple_shop.service;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    private final RoleRepository roleRepo;

    public RoleService(RoleRepository roleRepo, CustomerRepository customerRepo) {
        this.roleRepo = roleRepo;
    }

    // Assign default USER role when customer is created
    @Transactional
    public void assignDefaultRole(Customer customer) {
        Role role = new Role();
        role.setRoleName("USER".toUpperCase());
        role.setCustomer(customer);
        roleRepo.save(role);
    }


    public boolean isAdmin(String customerId) {
        return roleRepo.findByCustomer_CustomerId(customerId)
                .map(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))
                .orElse(false);
    }

    // Role check based on Keycloak token
    public boolean isAdmin(Jwt principal) {

        Map<String, Object> realmAccess = principal.getClaimAsMap("realm_access");

        if (realmAccess == null) return false;

        List<String> roles = (List<String>) realmAccess.get("roles");

        if (roles == null) return false;

        return roles.contains("ADMIN");
    }

    @Transactional
    public void deleteRolesByCustomer(Customer customer) {
        roleRepo.deleteByCustomer(customer);
    }

}
