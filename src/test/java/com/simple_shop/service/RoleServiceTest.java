package com.simple_shop.service;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import com.simple_shop.repository.RoleRepository;
import com.simple_shop.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private RoleRepository roleRepo;
    private CustomerRepository customerRepo; // not used but can be mocked
    private RoleService roleService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        roleRepo = mock(RoleRepository.class);
        customerRepo = mock(CustomerRepository.class);
        roleService = new RoleService(roleRepo, customerRepo);

        customer = new Customer();
        customer.setCustomerId("CUS-1001");
        customer.setUserName("john_doe");
    }

    @Test
    void testAssignDefaultRole() {
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);

        roleService.assignDefaultRole(customer);

        verify(roleRepo, times(1)).save(roleCaptor.capture());
        Role savedRole = roleCaptor.getValue();

        assertEquals("USER", savedRole.getRoleName());
        assertEquals(customer, savedRole.getCustomer());
    }

    @Test
    void testIsAdminByCustomerIdTrue() {
        Role role = new Role();
        role.setRoleName("ADMIN");
        role.setCustomer(customer);

        when(roleRepo.findByCustomer_CustomerId("CUS-1001")).thenReturn(Optional.of(role));

        boolean result = roleService.isAdmin("CUS-1001");
        assertTrue(result);
    }

    @Test
    void testIsAdminByCustomerIdFalse() {
        Role role = new Role();
        role.setRoleName("USER");
        role.setCustomer(customer);

        when(roleRepo.findByCustomer_CustomerId("CUS-1001")).thenReturn(Optional.of(role));

        boolean result = roleService.isAdmin("CUS-1001");
        assertFalse(result);
    }

    @Test
    void testIsAdminByCustomerIdNotFound() {
        when(roleRepo.findByCustomer_CustomerId("CUS-9999")).thenReturn(Optional.empty());

        boolean result = roleService.isAdmin("CUS-9999");
        assertFalse(result);
    }

    @Test
    void testIsAdminByJwtAdminRole() {
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("USER", "ADMIN"));

        when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

        boolean result = roleService.isAdmin(jwt);
        assertTrue(result);
    }

    @Test
    void testIsAdminByJwtNotAdminRole() {
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("USER"));

        when(jwt.getClaimAsMap("realm_access")).thenReturn(realmAccess);

        boolean result = roleService.isAdmin(jwt);
        assertFalse(result);
    }

    @Test
    void testIsAdminByJwtNoRoles() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsMap("realm_access")).thenReturn(null);

        boolean result = roleService.isAdmin(jwt);
        assertFalse(result);
    }

    @Test
    void testDeleteRolesByCustomer() {
        roleService.deleteRolesByCustomer(customer);

        verify(roleRepo, times(1)).deleteByCustomer(customer);
    }
}
