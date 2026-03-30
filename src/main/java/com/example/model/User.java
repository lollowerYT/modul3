package com.example.model;

import java.io.Serializable;

public class User implements Serializable {
    private String login;
    private String passwordHash; // хранится BCrypt-хеш
    private String email;

    public User() {
    }

    public User(String login, String passwordHash, String email) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}