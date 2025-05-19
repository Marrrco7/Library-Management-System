package org.example.model;

import org.example.validation.ValidEmail;

import javax.persistence.*;


/**
 * Represents a user in the library management system.
 * Each User has a name, email, phone number, address, password, and role.
 * The email field is validated using the {@link ValidEmail} annotation to ensure proper format.
 *
 * <p>This entity is mapped to the "Users" table in the database.
 * It enforces unique, non-null email addresses and non-null values for name, password, and role.</p>
 */

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    @ValidEmail
    private String email;

    private String phoneNumber;
    private String address;

    @Column(nullable = false)
    private String password; // Add this field

    @Column(nullable = false)
    private String role; // Add this field

    public User() {
    }

    public User(String name, String email, String phoneNumber, String address, String password, String role) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
