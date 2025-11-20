package com.simpleshop.repository;

import com.simpleshop.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Customer> mapper = (rs, rowNum) -> {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setCustomerId(rs.getString("customer_id"));
        c.setUserName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPassword(rs.getString("password"));
        return c;
    };

    public List<Customer> findAll() {
        return jdbcTemplate.query(
                "SELECT id, customer_id, name, email, password FROM customers", mapper);
    }

    public Optional<Customer> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT id, customer_id, name, email, password FROM customers WHERE id = ?",
                mapper,
                id
        ).stream().findFirst();
    }

    public void save(Customer customer) {
        jdbcTemplate.update(
                "INSERT INTO customers(customer_id, name, email, password) VALUES (?, ?, ?, ?)",
                customer.getCustomerId(),
                customer.getUserName(),
                customer.getEmail(),
                customer.getPassword()
        );
    }

    public void update(Long id, Customer customer) {
        jdbcTemplate.update(
                "UPDATE customers SET customer_id = ?, name = ?, email = ?, password = ? WHERE id = ?",
                customer.getCustomerId(),
                customer.getUserName(),
                customer.getEmail(),
                customer.getPassword(),
                id
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM customers WHERE id = ?", id);
    }
}
