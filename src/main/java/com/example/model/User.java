package com.example.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "login", length = 50)
    private String login;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // Конструкторы
    public User() {}

    public User(String login, String passwordHash, String email) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // Геттеры и сеттеры
    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
}