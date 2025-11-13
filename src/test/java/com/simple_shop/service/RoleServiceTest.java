package com.simple_shop.service;

import com.simple_shop.model.Customer;
import com.simple_shop.model.Role;
import com.simple_shop.repository.CustomerRepository;
import com.simple_shop.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private RoleService roleService;

    private Customer customer;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = createCustomer("CUST123");
        role = createRole(customer, "USER");

        // default stub for saving roles
        when(roleRepo.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ====================== Helper Methods ======================
    private Customer createCustomer(String id) {
        Customer c = new Customer();
        c.setCustomerId(id);
        return c;
    }

    private Role createRole(Customer customer, String roleName) {
        Role r = new Role();
        r.setCustomer(customer);
        r.setRoleName(roleName);
        return r;
    }

    // ====================== assignDefaultRole ======================
    @Test
    void testAssignDefaultRole() {
        roleService.assignDefaultRole(customer);

        verify(roleRepo, times(1)).save(any(Role.class));
    }

    // ====================== createAdminRole ======================
    @Test
    void testCreateAdminRole_Success() {
        Customer targetCustomer = createCustomer("CUST999");
        Role adminRole = createRole(customer, "ADMIN");

        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(adminRole));
        when(customerRepo.findByCustomerId("CUST999")).thenReturn(Optional.of(targetCustomer));

        Optional<Role> result = roleService.createAdminRole("CUST123", "CUST999");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getRoleName());
        assertEquals(targetCustomer, result.get().getCustomer());
    }

    @Test
    void testCreateAdminRole_NotAdmin() {
        Role userRole = createRole(customer, "USER");
        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(userRole));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.createAdminRole("CUST123", "CUST999"));

        assertEquals("Access denied. Only ADMIN can create another ADMIN.", exception.getMessage());
    }

    @Test
    void testCreateAdminRole_TargetNotFound() {
        Role adminRole = createRole(customer, "ADMIN");

        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(adminRole));
        when(customerRepo.findByCustomerId("CUST999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.createAdminRole("CUST123", "CUST999"));

        assertEquals("Target customer not found.", exception.getMessage());
    }

    // ====================== getRoleByCustomerId ======================
    @Test
    void testGetRoleByCustomerId_Found() {
        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleByCustomerId("CUST123");

        assertTrue(result.isPresent());
        assertEquals("USER", result.get().getRoleName());
    }

    @Test
    void testGetRoleByCustomerId_NotFound() {
        when(roleRepo.findByCustomer_CustomerId("CUST999")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleByCustomerId("CUST999");

        assertFalse(result.isPresent());
    }

    // ====================== isAdmin ======================
    @ParameterizedTest
    @CsvSource({
            "ADMIN,true",
            "USER,false"
    })
    void testIsAdminCases(String roleName, boolean expected) {
        role.setRoleName(roleName);
        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(role));

        boolean result = roleService.isAdmin("CUST123");

        assertEquals(expected, result);
    }

    @Test
    void testIsAdmin_NotFound() {
        when(roleRepo.findByCustomer_CustomerId("CUST999")).thenReturn(Optional.empty());

        boolean result = roleService.isAdmin("CUST999");

        assertFalse(result);
    }

    // ====================== promoteToAdmin ======================
    @Test
    void testPromoteToAdmin_Success() {
        role.setRoleName("USER");
        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(role));

        boolean result = roleService.promoteToAdmin("CUST123");

        assertTrue(result);
        assertEquals("ADMIN", role.getRoleName());
        verify(roleRepo, times(1)).save(role);
    }

    @Test
    void testPromoteToAdmin_AlreadyAdmin() {
        role.setRoleName("ADMIN");
        when(roleRepo.findByCustomer_CustomerId("CUST123")).thenReturn(Optional.of(role));

        boolean result = roleService.promoteToAdmin("CUST123");

        assertFalse(result);
        verify(roleRepo, never()).save(any(Role.class));
    }

    @Test
    void testPromoteToAdmin_NotFound() {
        when(roleRepo.findByCustomer_CustomerId("CUST999")).thenReturn(Optional.empty());

        boolean result = roleService.promoteToAdmin("CUST999");

        assertFalse(result);
        verify(roleRepo, never()).save(any(Role.class));
    }
}
