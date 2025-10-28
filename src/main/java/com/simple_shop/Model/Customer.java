package com.simple_shop.Model;

import jakarta.persistence.*;

@Entity
public class Customer {

    @Id                                       // marks this field as the primary key (unique identifier for each record).
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // automatically generates the ID value whenever a new customer is added. // tells the database to auto-increment the ID (like 1, 2, 3…).
    private Long id;   // Table columns

    private String name;  // Table columns
    private String email;  // Table columns
    private String password;  //Table columns

    // Getters + Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
